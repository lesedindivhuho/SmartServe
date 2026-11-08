package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.databinding.ActivityLoginBinding
import com.example.cafeteriamanagementsystem.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            val user = SessionManager.users.find {
                it.username == username &&
                        it.password == password
            }

            if (user != null) {

                SessionManager.currentUser = user

                Toast.makeText(
                    this,
                    "Welcome ${user.fullName}",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(Intent(this, DashboardActivity::class.java))
                finish()

            } else {

                Toast.makeText(
                    this,
                    "Invalid Student ID or Password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Register
        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}