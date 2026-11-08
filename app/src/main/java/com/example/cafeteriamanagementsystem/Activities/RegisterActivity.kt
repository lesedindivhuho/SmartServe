package com.example.cafeteriamanagementsystem.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.databinding.ActivityRegisterBinding
import com.example.cafeteriamanagementsystem.models.User
import com.example.cafeteriamanagementsystem.models.UserRole
import com.example.cafeteriamanagementsystem.utils.SessionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateAccount.setOnClickListener {

            val fullName =
                binding.etFullName.text.toString().trim()

            val username =
                binding.etStudentNumber.text.toString().trim()

            val password =
                binding.etPassword.text.toString()

            val confirmPassword =
                binding.etConfirmPassword.text.toString()



            // CHECK EMPTY FIELDS


            if (
                fullName.isEmpty() ||
                username.isEmpty() ||
                password.isEmpty() ||
                confirmPassword.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please complete all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }



            // PASSWORD VALIDATION


            if (password.length < 8) {

                Toast.makeText(
                    this,
                    "Password must be at least 8 characters long",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }


            // At least one uppercase letter
            if (!password.any { it.isUpperCase() }) {

                Toast.makeText(
                    this,
                    "Password must contain at least 1 uppercase letter",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }


            // At least one lowercase letter
            if (!password.any { it.isLowerCase() }) {

                Toast.makeText(
                    this,
                    "Password must contain at least 1 lowercase letter",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }


            // At least one number
            if (!password.any { it.isDigit() }) {

                Toast.makeText(
                    this,
                    "Password must contain at least 1 number",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }



            // CONFIRM PASSWORD


            if (password != confirmPassword) {

                Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // CHECK USERNAME


            if (
                SessionManager.users.any {
                    it.username.equals(
                        username,
                        ignoreCase = true
                    )
                }
            ) {

                Toast.makeText(
                    this,
                    "Username already exists",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }



            // CREATE STUDENT ACCOUNT


            SessionManager.users.add(
                User(
                    fullName = fullName,
                    username = username,
                    password = password,
                    role = UserRole.STUDENT
                )
            )



            // SUCCESS


            Toast.makeText(
                this,
                "Account created successfully",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }

        // BACK TO LOGIN
        binding.txtLogin.setOnClickListener {
            finish()
        }
    }
}