package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.databinding.ActivityCheckoutBinding
import com.example.cafeteriamanagementsystem.utils.CartManager
import com.example.cafeteriamanagementsystem.utils.OrderManager

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.tvCheckoutTotal.text =
            String.format(
                "Order Total: R%.2f",
                CartManager.getTotal()
            )

        binding.btnPlaceOrder.setOnClickListener {

            val name =
                binding.etName.text.toString().trim()

            val studentNumber =
                binding.etStudentNumber.text.toString().trim()

            val phone =
                binding.etPhone.text.toString().trim()

            if (
                name.isEmpty() ||
                studentNumber.isEmpty() ||
                phone.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please complete all required fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (CartManager.cartItems.isEmpty()) {

                Toast.makeText(
                    this,
                    "Your cart is empty",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // SAVE ORDER FOR STAFF


            val order = OrderManager.placeOrder(

                customerName = name,

                studentNumber = studentNumber,

                phone = phone,

                items = CartManager.cartItems,

                total = CartManager.getTotal()
            )


            // SEND INFORMATION TO RECEIPT


            val intent = Intent(
                this,
                ReceiptActivity::class.java
            )

            intent.putExtra(
                "CUSTOMER_NAME",
                name
            )

            intent.putExtra(
                "STUDENT_NUMBER",
                studentNumber
            )

            intent.putExtra(
                "PHONE",
                phone
            )

            intent.putExtra(
                "TOTAL",
                CartManager.getTotal()
            )

            intent.putExtra(
                "ORDER_ID",
                order.orderId
            )

            startActivity(intent)

            Toast.makeText(
                this,
                "Order ${order.orderId} placed successfully",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }
}