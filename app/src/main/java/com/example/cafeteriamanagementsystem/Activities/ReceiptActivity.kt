
package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.databinding.ActivityReceiptBinding
import com.example.cafeteriamanagementsystem.utils.CartManager

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TOOLBAR
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            goToDashboard()
        }

        // GET CUSTOMER INFORMATION
        val customerName =
            intent.getStringExtra("CUSTOMER_NAME") ?: "Customer"

        val phone =
            intent.getStringExtra("PHONE") ?: "Not provided"

        // GET ORDER NUMBER
        val orderNumber =
            intent.getStringExtra("RECEIPT_NUMBER") ?: "001"

        // DISPLAY RECEIPT INFORMATION
        binding.tvCustomerName.text = "Name: $customerName"
        binding.tvPhone.text = "Phone: $phone"
        binding.tvReceiptNumber.text = "Order No: $orderNumber"

        // DISPLAY ORDERED ITEMS
        val items = CartManager.cartItems
        val receipt = StringBuilder()

        receipt.append(" (ORDER RECEIPT)\n\n")
        receipt.append("Order Number: $orderNumber\n")
        receipt.append("Customer Name: $customerName\n")
        receipt.append("Phone Number: $phone\n")

        receipt.append("ITEMS ORDERED\n")


        if (items.isEmpty()) {
            receipt.append("No items in order.\n")
        } else {
            for (food in items) {
                receipt.append(
                    "${food.name}    R%.2f\n".format(food.price)
                )
            }
        }

        receipt.append("-----------------------------------\n")

        binding.tvItems.text = receipt.toString()

        // DISPLAY TOTAL
        val total = CartManager.getTotal()

        binding.tvReceiptTotal.text =
            "TOTAL: R%.2f".format(total)

        // DONE BUTTON
        binding.btnDone.setOnClickListener {
            CartManager.clearCart()
            goToDashboard()
        }
    }

    // RETURN TO DASHBOARD
    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)
        finish()
    }
}
