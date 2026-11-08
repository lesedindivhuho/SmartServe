package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.databinding.ActivityDashboardBinding
import com.example.cafeteriamanagementsystem.models.UserRole
import com.example.cafeteriamanagementsystem.utils.SessionManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding =
            ActivityDashboardBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.title =
            "SmartServe"

        // Drawer

        drawerToggle =
            ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )

        binding.drawerLayout.addDrawerListener(
            drawerToggle
        )

        drawerToggle.syncState()

        setupRoleBasedNavigation()

        // Welcome

        val user =
            SessionManager.currentUser

        binding.tvWelcome.text =
            if (user != null) {

                "Welcome, ${user.fullName}"

            } else {

                "Welcome to SmartServe"
            }

        // Food Menu

        binding.navMenu.setOnClickListener {

            closeDrawer()

            startActivity(
                Intent(
                    this,
                    MenuActivity::class.java
                )
            )
        }

        // Cart

        binding.navCart.setOnClickListener {

            closeDrawer()

            startActivity(
                Intent(
                    this,
                    CartActivity::class.java
                )
            )
        }

        // Orders

        binding.navOrders.setOnClickListener {

            closeDrawer()

            startActivity(
                Intent(
                    this,
                    OrdersActivity::class.java
                )
            )
        }

        // Inventory

        binding.navInventory.setOnClickListener {

            closeDrawer()

            startActivity(
                Intent(
                    this,
                    InventoryActivity::class.java
                )
            )
        }

        // Reports

        binding.navReports.setOnClickListener {

            closeDrawer()

            startActivity(
                Intent(
                    this,
                    ReportsActivity::class.java
                )
            )
        }

        // Manage Menu

        binding.navManageMenu.setOnClickListener {

            closeDrawer()

            if (
                SessionManager.currentUser?.role ==
                UserRole.MANAGER
            ) {

                startActivity(
                    Intent(
                        this,
                        ManageMenuActivity::class.java
                    )
                )
            }
        }

        // Profile

        binding.navProfile.setOnClickListener {

            closeDrawer()

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        // Back

        binding.navBack.setOnClickListener {

            closeDrawer()
        }

        // Logout

        binding.navLogout.setOnClickListener {

            SessionManager.currentUser =
                null

            val intent =
                Intent(
                    this,
                    LoginActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            finish()
        }

        // Android Back

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if (
                        binding.drawerLayout.isDrawerOpen(
                            binding.navigationDrawer
                        )
                    ) {

                        closeDrawer()

                    } else {

                        finish()
                    }
                }
            }
        )
    }

    private fun setupRoleBasedNavigation() {

        when (
            SessionManager.currentUser?.role
        ) {

            UserRole.STUDENT -> {

                // Students can still track their own orders
                binding.navOrders.visibility =
                    View.VISIBLE

                binding.navInventory.visibility =
                    View.GONE

                binding.navReports.visibility =
                    View.GONE

                binding.navManageMenu.visibility =
                    View.GONE
            }

            UserRole.STAFF -> {

                binding.navOrders.visibility =
                    View.VISIBLE

                binding.navInventory.visibility =
                    View.VISIBLE

                binding.navReports.visibility =
                    View.GONE

                binding.navManageMenu.visibility =
                    View.GONE
            }

            UserRole.MANAGER -> {

                binding.navOrders.visibility =
                    View.VISIBLE

                binding.navInventory.visibility =
                    View.VISIBLE

                binding.navReports.visibility =
                    View.VISIBLE

                binding.navManageMenu.visibility =
                    View.VISIBLE
            }

            null -> {

                val intent =
                    Intent(
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

    private fun closeDrawer() {

        binding.drawerLayout.closeDrawers()
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {

        if (
            drawerToggle.onOptionsItemSelected(
                item
            )
        ) {

            return true
        }

        return super.onOptionsItemSelected(item)
    }
}