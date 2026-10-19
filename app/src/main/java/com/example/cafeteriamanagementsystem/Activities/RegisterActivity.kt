package com.example.cafeteriamanagementsystem.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.databinding.ActivityRegisterBinding
import com.example.cafeteriamanagementsystem.models.User
import com.example.cafeteriamanagementsystem.models.UserRole
import com.example.cafeteriamanagementsystem.utils.FirebaseUserStore
import com.example.cafeteriamanagementsystem.utils.GoogleSignInFlow
import com.example.cafeteriamanagementsystem.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private val googleSignIn = GoogleSignInFlow(this) { user ->
        Toast.makeText(this, "Welcome ${user.fullName}", Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(this, DashboardActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnCreateAccount.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val studentId = binding.etStudentNumber.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (
                fullName.isEmpty() ||
                studentId.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty() ||
                confirmPassword.isEmpty()
            ) {
                showMessage("Please complete all fields")
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showMessage("Please enter a valid email address")
                return@setOnClickListener
            }

            if (password.length < 8) {
                showMessage("Password must be at least 8 characters long")
                return@setOnClickListener
            }

            if (!password.any { it.isUpperCase() }) {
                showMessage("Password must contain at least 1 uppercase letter")
                return@setOnClickListener
            }

            if (!password.any { it.isLowerCase() }) {
                showMessage("Password must contain at least 1 lowercase letter")
                return@setOnClickListener
            }

            if (!password.any { it.isDigit() }) {
                showMessage("Password must contain at least 1 number")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showMessage("Passwords do not match")
                return@setOnClickListener
            }

            FirebaseUserStore.findByStudentId(studentId)
                .addOnSuccessListener profileLookup@ { documents ->
                    if (documents.documents.isNotEmpty()) {
                        showMessage("Student ID already exists")
                        return@profileLookup
                    }

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val firebaseUser = firebaseAuth.currentUser
                            if (firebaseUser == null) {
                                showMessage("Account creation did not return a user")
                            } else {
                                FirebaseUserStore.saveProfile(
                                    firebaseUser = firebaseUser,
                                    fullName = fullName,
                                    studentId = studentId,
                                    provider = "password"
                                )
                                    .addOnSuccessListener {
                                        val user = User(
                                            fullName = fullName,
                                            username = studentId,
                                            password = password,
                                            role = UserRole.STUDENT,
                                            email = email
                                        )
                                        SessionManager.users.add(user)
                                        SessionManager.currentUser = user
                                        showRegistrationSuccess(
                                            "Your account was created successfully. You can now sign in with your Student ID and password."
                                        )
                                    }
                                    .addOnFailureListener {
                                        showMessage("Account created, but profile could not be saved")
                                    }
                            }
                        }
                        .addOnFailureListener { error ->
                            showMessage(error.message ?: "Could not create account")
                        }
                }
                .addOnFailureListener {
                    showMessage("Could not connect to Firebase. Check your connection.")
                }
        }

        binding.btnGoogleSignIn.setOnClickListener {
            googleSignIn.start()
        }

        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showRegistrationSuccess(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Account created")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Continue to login") { _, _ ->
                startActivity(
                    Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                finish()
            }
            .show()
    }
}
