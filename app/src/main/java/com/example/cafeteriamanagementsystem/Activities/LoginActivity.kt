package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.databinding.ActivityLoginBinding
import com.example.cafeteriamanagementsystem.utils.FirebaseUserStore
import com.example.cafeteriamanagementsystem.utils.GoogleSignInFlow
import com.example.cafeteriamanagementsystem.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private val googleSignIn = GoogleSignInFlow(this) { user ->
        openDashboard("Welcome ${user.fullName}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        // Login Button
        binding.btnLogin.setOnClickListener {

            val username = binding.edtStudentID.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter your Student ID and Password",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val localUser = SessionManager.users.find {
                it.username == username &&
                        it.password == password
            }

            if (localUser != null) {
                SessionManager.currentUser = localUser
                openDashboard("Welcome ${localUser.fullName}")
                return@setOnClickListener
            }

            FirebaseUserStore.findByStudentId(username)
                .addOnSuccessListener { documents ->
                    val document = documents.documents.firstOrNull()
                    val email = document?.getString("email").orEmpty()

                    if (document == null || email.isBlank()) {
                        showMessage("Invalid Student ID or Password")
                        return@addOnSuccessListener
                    }

                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val user = FirebaseUserStore.toUser(document, password)
                            SessionManager.currentUser = user
                            openDashboard("Welcome ${user.fullName}")
                        }
                        .addOnFailureListener {
                            showMessage("Invalid Student ID or Password")
                        }
                }
                .addOnFailureListener {
                    showMessage("Could not connect to Firebase. Check your connection.")
                }
        }

        // Register
        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnGoogleSignIn.setOnClickListener {
            googleSignIn.start()
        }
    }

    private fun openDashboard(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
