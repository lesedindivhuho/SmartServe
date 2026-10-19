package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.databinding.ActivityProfileBinding
import com.example.cafeteriamanagementsystem.utils.SessionManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)




        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = "My Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }


        // GET LOGGED-IN USER


        val user = SessionManager.currentUser

        if (user == null) {

            Toast.makeText(
                this,
                "No user is currently logged in",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }


        // DISPLAY CURRENT INFORMATION


        binding.etFullName.setText(user.fullName)
        binding.etUsername.setText(user.username)

        binding.tvRole.text = when (user.role.name) {

            "STUDENT" -> "Student"

            "STAFF" -> "Cafeteria Staff"

            "MANAGER" -> "Manager"

            else -> "Unknown"
        }


        // SAVE PROFILE CHANGES


        binding.btnSaveProfile.setOnClickListener {

            val newFullName =
                binding.etFullName.text.toString().trim()

            val newUsername =
                binding.etUsername.text.toString().trim()

            // Check that the full name is not empty
            if (newFullName.isEmpty()) {

                binding.etFullName.error =
                    "Please enter your full name"

                binding.etFullName.requestFocus()

                return@setOnClickListener
            }

            // Check that the username is not empty
            if (newUsername.isEmpty()) {

                binding.etUsername.error =
                    "Please enter a username"

                binding.etUsername.requestFocus()

                return@setOnClickListener
            }

            // CHECK USERNAME AVAILABILITY


            val usernameAlreadyExists =
                SessionManager.users.any {

                    it !== user &&
                            it.username.equals(
                                newUsername,
                                ignoreCase = true
                            )
                }

            if (usernameAlreadyExists) {

                binding.etUsername.error =
                    "Username is already in use"

                binding.etUsername.requestFocus()

                Toast.makeText(
                    this,
                    "Please choose another username",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // UPDATE USER INFORMATION


            user.fullName = newFullName
            user.username = newUsername


            SessionManager.currentUser = user


            // SUCCESS MESSAGE


            Toast.makeText(
                this,
                "Profile updated successfully",
                Toast.LENGTH_SHORT
            ).show()

            // Close profile page
            finish()
        }


        // LOGOUT


        binding.btnLogout.setOnClickListener {

            SessionManager.logout(this)

            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }
    }
}