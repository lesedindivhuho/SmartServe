package com.example.cafeteriamanagementsystem.utils

import com.example.cafeteriamanagementsystem.models.User
import com.example.cafeteriamanagementsystem.models.UserRole
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.android.gms.tasks.Task

object FirebaseUserStore {

    private const val USERS_COLLECTION = "users"
    private val firestore = FirebaseFirestore.getInstance()

    fun findByStudentId(studentId: String): Task<QuerySnapshot> =
        firestore.collection(USERS_COLLECTION)
            .whereEqualTo("studentId", studentId)
            .limit(1)
            .get()

    fun findByUid(uid: String) =
        firestore.collection(USERS_COLLECTION)
            .document(uid)
            .get()

    fun saveProfile(
        firebaseUser: FirebaseUser,
        fullName: String,
        studentId: String,
        provider: String
    ) = firestore.collection(USERS_COLLECTION)
        .document(firebaseUser.uid)
        .set(
            mapOf(
                "uid" to firebaseUser.uid,
                "fullName" to fullName,
                "studentId" to studentId,
                "email" to (firebaseUser.email ?: ""),
                "provider" to provider,
                "role" to "STUDENT"
            ),
            SetOptions.merge()
        )

    fun toUser(document: DocumentSnapshot, password: String = ""): User {
        val role = runCatching {
            UserRole.valueOf(document.getString("role") ?: UserRole.STUDENT.name)
        }.getOrDefault(UserRole.STUDENT)

        return User(
            fullName = document.getString("fullName").orEmpty().ifBlank { "Student" },
            username = document.getString("studentId").orEmpty(),
            password = password,
            role = role,
            email = document.getString("email").orEmpty()
        )
    }
}
