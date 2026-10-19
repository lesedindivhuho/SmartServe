package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.databinding.ActivityDashboardBinding
import com.example.cafeteriamanagementsystem.models.Order
import com.example.cafeteriamanagementsystem.models.UserRole
import com.example.cafeteriamanagementsystem.utils.CartManager
import com.example.cafeteriamanagementsystem.utils.OrderManager
import com.example.cafeteriamanagementsystem.utils.SessionManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "SmartServe"

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navDashboard -> true
                R.id.navMenu -> {
                    openActivity(MenuActivity::class.java)
                    false
                }
                R.id.navCart -> {
                    openActivity(CartActivity::class.java)
                    false
                }
                R.id.navMore -> {
                    showMoreMenu()
                    false
                }
                else -> false
            }
        }

        binding.btnBrowseMenu.setOnClickListener {
            openActivity(MenuActivity::class.java)
        }

        binding.btnOpenCart.setOnClickListener {
            openActivity(CartActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshDashboard()
    }

    private fun refreshDashboard() {
        val user = SessionManager.currentUser
        if (user == null) {
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            finish()
            return
        }

        binding.tvWelcome.text = "Welcome, ${user.fullName}"

        val cartItems = CartManager.cartItems
        binding.tvCartCount.text = "${cartItems.size} ${if (cartItems.size == 1) "item" else "items"}"
        binding.tvCartTotal.text = String.format("R%.2f", CartManager.getTotal())

        binding.tvCartPreview.text = if (cartItems.isEmpty()) {
            "Your cart is empty. Add something from the menu."
        } else {
            cartItems.take(3).joinToString(separator = "\n") {
                "• ${it.name}  R${String.format("%.2f", it.price)}"
            } + if (cartItems.size > 3) "\n• +${cartItems.size - 3} more item(s)" else ""
        }

        val recentOrder = OrderManager.orders.lastOrNull()
        showRecentOrder(recentOrder)
    }

    private fun showRecentOrder(order: Order?) {
        if (order == null) {
            binding.tvRecentOrder.text = "No orders yet. Your next meal can start here."
            binding.tvRecentOrderStatus.text = "READY"
            return
        }

        binding.tvRecentOrder.text = buildString {
            append(order.orderId)
            append("  •  ")
            append(order.items.joinToString { it.name })
            append("\n")
            append(order.dateTime)
            append("  •  R")
            append(String.format("%.2f", order.total))
        }
        binding.tvRecentOrderStatus.text = order.status
    }

    private fun showMoreMenu() {
        val popup = PopupMenu(this, binding.bottomNav)
        popup.menu.add("My Profile").setOnMenuItemClickListener {
            openActivity(ProfileActivity::class.java)
            true
        }
        popup.menu.add("Orders").setOnMenuItemClickListener {
            openActivity(OrdersActivity::class.java)
            true
        }

        val role = SessionManager.currentUser?.role
        if (role == UserRole.STAFF || role == UserRole.MANAGER) {
            popup.menu.add("Inventory").setOnMenuItemClickListener {
                openActivity(InventoryActivity::class.java)
                true
            }
        }
        if (role == UserRole.MANAGER) {
            popup.menu.add("Reports").setOnMenuItemClickListener {
                openActivity(ReportsActivity::class.java)
                true
            }
            popup.menu.add("Manage Menu").setOnMenuItemClickListener {
                openActivity(ManageMenuActivity::class.java)
                true
            }
        }

        popup.menu.add("Log out").setOnMenuItemClickListener {
            logout()
            true
        }
        popup.show()
    }

    private fun openActivity(activity: Class<*>) {
        startActivity(Intent(this, activity))
    }

    private fun logout() {
        SessionManager.logout(this)
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
