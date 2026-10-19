package com.example.cafeteriamanagementsystem.models

data class User(
    var fullName: String,
    var username: String,
    val password: String,
    val role: UserRole = UserRole.STUDENT,
    val email: String = ""
)