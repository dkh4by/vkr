package com.example.electronicreception.data

import android.content.Context
import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val httpClient = OkHttpClient()

    private val cloudinaryCloudName = "dzuaakxmz"
    private val cloudinaryUploadPreset = "district_unsigned"
    fun currentUserId(): String? = auth.currentUser?.uid

    fun currentUserEmail(): String? = auth.currentUser?.email

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun register(
        lastName: String,
        firstName: String,
        middleName: String,
        email: String,
        password: String
    ) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: return

        val fullName = listOf(lastName, firstName, middleName)
            .filter { it.isNotBlank() }
            .joinToString(" ")

        val user = hashMapOf(
            "lastName" to lastName,
            "firstName" to firstName,
            "middleName" to middleName,
            "fullName" to fullName,
            "email" to email,
            "phone" to "",
            "isAdmin" to false,
            "position" to "",
            "department" to "",
            "avatarUrl" to "",
            "createdAt" to Timestamp.now()
        )

        db.collection("users").document(uid).set(user).await()
    }

    suspend fun getCurrentUserProfile(): AppUser? {
        val uid = auth.currentUser?.uid ?: return null
        val doc = db.collection("users").document(uid).get().await()

        if (!doc.exists()) return null

        return AppUser(
            uid = uid,
            fullName = doc.getString("fullName") ?: "",
            email = doc.getString("email") ?: auth.currentUser?.email.orEmpty(),
            phone = doc.getString("phone") ?: "",
            isAdmin = doc.getBoolean("isAdmin") ?: false,
            position = doc.getString("position") ?: "",
            department = doc.getString("department") ?: "",
            avatarUrl = doc.getString("avatarUrl") ?: ""
        )
    }

    suspend fun createComplaint(
        fullName: String,
        phone: String,
        address: String,
        category: String,
        subject: String,
        description: String,
        imageUris: List<Uri> = emptyList(),
        locationLat: Double? = null,
        locationLng: Double? = null,
        context: Context? = null
    ): String {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Пользователь не авторизован")
        val email = auth.currentUser?.email.orEmpty()
        val complaintId = "COMP-${System.currentTimeMillis()}"

        val imageUrls = if (context != null && imageUris.isNotEmpty()) {
            uploadComplaintImagesToCloudinary(
                context = context,
                complaintId = complaintId,
                imageUris = imageUris
            )
        } else {
            emptyList()
        }

        val locationMap = if (locationLat != null && locationLng != null) {
            mapOf(
                "lat" to locationLat,
                "lng" to locationLng
            )
        } else {
            null
        }

        val complaint = hashMapOf(
            "id" to complaintId,
            "userId" to uid,
            "userEmail" to email,
            "fullName" to fullName,
            "phone" to phone,
            "address" to address,
            "category" to category,
            "subject" to subject,
            "description" to description,
            "images" to imageUrls,
            "location" to locationMap,
            "status" to "Новое",
            "createdAt" to Timestamp.now(),
            "updatedAt" to Timestamp.now()
        )

        db.collection("complaints").document(complaintId).set(complaint).await()

        return complaintId
    }


    private suspend fun uploadComplaintImagesToCloudinary(
        context: Context,
        complaintId: String,
        imageUris: List<Uri>
    ): List<String> {
        val urls = mutableListOf<String>()

        imageUris.take(3).forEachIndexed { index, uri ->
            val imageFile = uriToTempFile(
                context = context,
                uri = uri,
                fileName = "complaint_${complaintId}_$index.jpg"
            )

            val url = uploadSingleImageToCloudinary(
                file = imageFile,
                folder = "complaints/$complaintId"
            )

            urls.add(url)

            imageFile.delete()
        }

        return urls
    }

    private suspend fun uploadSingleImageToCloudinary(
        file: File,
        folder: String
    ): String = withContext(Dispatchers.IO) {
        val mediaType = "image/*".toMediaTypeOrNull()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                name = "file",
                filename = file.name,
                body = file.asRequestBody(mediaType)
            )
            .addFormDataPart("upload_preset", cloudinaryUploadPreset)
            .addFormDataPart("folder", folder)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudinaryCloudName/image/upload")
            .post(requestBody)
            .build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IllegalStateException("Ошибка загрузки изображения в Cloudinary")
        }

        val responseBody = response.body?.string()
            ?: throw IllegalStateException("Пустой ответ Cloudinary")

        val secureUrlRegex = """"secure_url"\s*:\s*"([^"]+)"""".toRegex()
        val match = secureUrlRegex.find(responseBody)

        match?.groups?.get(1)?.value
            ?: throw IllegalStateException("Не удалось получить ссылку на изображение")
    }

    private fun uriToTempFile(
        context: Context,
        uri: Uri,
        fileName: String
    ): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Не удалось открыть изображение")

        val tempFile = File(context.cacheDir, fileName)

        inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }

    suspend fun getMyComplaints(): List<Complaint> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        val snapshot = db.collection("complaints")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toComplaint()
        }.sortedByDescending {
            it.createdAt?.toDate()?.time ?: 0L
        }
    }
    suspend fun getComplaintById(id: String): Complaint? {
        val doc = db.collection("complaints")
            .document(id.trim())
            .get()
            .await()

        if (!doc.exists()) {
            return null
        }

        return doc.toComplaint()
    }
    suspend fun getAllComplaints(): List<Complaint> {
        val snapshot = db.collection("complaints")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toComplaint()
        }.sortedByDescending {
            it.createdAt?.toDate()?.time ?: 0L
        }
    }

    suspend fun updateComplaintStatus(id: String, newStatus: String) {
        db.collection("complaints").document(id)
            .update(
                mapOf(
                    "status" to newStatus,
                    "updatedAt" to Timestamp.now()
                )
            )
            .await()
    }

    fun logout() {
        auth.signOut()
    }


    suspend fun updateProfilePhone(phone: String) {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("Пользователь не авторизован")

        db.collection("users").document(uid)
            .update(
                mapOf(
                    "phone" to phone,
                    "updatedAt" to Timestamp.now()
                )
            )
            .await()
    }

    suspend fun updateProfileAvatar(
        context: Context,
        imageUri: Uri
    ): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("Пользователь не авторизован")

        val file = uriToTempFile(
            context = context,
            uri = imageUri,
            fileName = "avatar_$uid.jpg"
        )

        val avatarUrl = uploadSingleImageToCloudinary(
            file = file,
            folder = "avatars/$uid"
        )

        file.delete()

        db.collection("users").document(uid)
            .update(
                mapOf(
                    "avatarUrl" to avatarUrl,
                    "updatedAt" to Timestamp.now()
                )
            )
            .await()

        return avatarUrl
    }





    suspend fun getNews(): List<NewsItem> {
        val snapshot = db.collection("news")
            .get()
            .await()

        return snapshot.documents.map { doc ->
            NewsItem(
                id = doc.id,
                title = doc.getString("title") ?: "",
                text = doc.getString("text")
                    ?: doc.getString("content")
                    ?: "",
                category = doc.getString("category") ?: "Общее",
                imageUrl = doc.getString("imageUrl")
                    ?: doc.getString("image")
                    ?: "",
                createdAt = doc.getTimestamp("createdAt")
            )
        }.sortedByDescending {
            it.createdAt?.toDate()?.time ?: 0L
        }
    }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toComplaint(): Complaint? {
    val location = get("location") as? Map<*, *>
    val images = get("images") as? List<*>

    return Complaint(
        id = getString("id") ?: id,
        userId = getString("userId") ?: "",
        userEmail = getString("userEmail") ?: "",
        fullName = getString("fullName") ?: "",
        phone = getString("phone") ?: "",
        address = getString("address") ?: "",
        category = getString("category") ?: "",
        subject = getString("subject") ?: "",
        description = getString("description") ?: "",
        status = getString("status") ?: "Новое",
        createdAt = getTimestamp("createdAt"),
        updatedAt = getTimestamp("updatedAt"),
        images = images?.filterIsInstance<String>() ?: emptyList(),
        locationLat = location?.get("lat") as? Double,
        locationLng = location?.get("lng") as? Double,
        assigneeName = getString("assigneeName") ?: "",
        assigneeRole = getString("assigneeRole") ?: "",
        assigneeDepartment = getString("assigneeDepartment") ?: ""
    )
}