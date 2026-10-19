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
            intent.getStringExtra("CUSTOMER_NAME")
                ?: "Customer"

        val studentNumber =
            intent.getStringExtra("STUDENT_NUMBER")
                ?: "Not provided"

        val phone =
            intent.getStringExtra("PHONE")
                ?: "Not provided"


        // GET RECEIPT NUMBER


        val receiptNumber =
            intent.getStringExtra("RECEIPT_NUMBER")
                ?: "001"


        // DISPLAY CUSTOMER INFORMATION


        binding.tvCustomerName.text =
            "Name: $customerName"

        binding.tvStudentNumber.text =
            "Student Number: $studentNumber"

        binding.tvPhone.text =
            "Phone: $phone"

        binding.tvReceiptNumber.text =
            "Receipt No: $receiptNumber"


        // DISPLAY ORDER ITEMS


        val items = CartManager.cartItems

        val receipt = StringBuilder()

        if (items.isEmpty()) {

            receipt.append("No items in order.")

        } else {

            for (food in items) {

                receipt.append(
                    "${food.name}     R%.2f\n"
                        .format(food.price)
                )
            }
        }

        binding.tvItems.text =
            receipt.toString()


        // DISPLAY TOTAL


        val total =
            CartManager.getTotal()

        binding.tvReceiptTotal.text =
            "TOTAL: R%.2f".format(total)



        binding.btnDone.setOnClickListener {

            // Clear cart only after the receipt
            // has been displayed.
            CartManager.clearCart()

            goToDashboard()
        }
    }


    // GO TO DASHBOARD


    private fun goToDashboard() {

        val intent = Intent(
            this,
            DashboardActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)

        finish()
    }
}