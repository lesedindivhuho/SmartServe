package com.example.cafeteriamanagementsystem.utils

import com.example.cafeteriamanagementsystem.models.User
import com.example.cafeteriamanagementsystem.models.UserRole

object SessionManager {

    val users = mutableListOf<User>()

    var currentUser: User? = null

    init {

        // Default manager account
        users.add(
            User(
                fullName = "Cafeteria Manager",
                username = "manager",
                password = "manager123",
                role = UserRole.MANAGER
            )
        )

        // Default staff account
        users.add(
            User(
                fullName = "Cafeteria Staff",
                username = "staff",
                password = "staff123",
                role = UserRole.STAFF
            )
        )
    }
}