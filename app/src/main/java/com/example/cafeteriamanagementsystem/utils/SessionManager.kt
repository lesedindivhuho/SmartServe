package com.example.cafeteriamanagementsystem.utils

import android.content.Context
import com.example.cafeteriamanagementsystem.models.User
import com.example.cafeteriamanagementsystem.models.UserRole
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

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

    fun logout(context: Context) {
        currentUser = null
        FirebaseAuth.getInstance().signOut()
        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
    }
}
