package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.databinding.ActivityCartBinding
import com.example.cafeteriamanagementsystem.models.Food
import com.example.cafeteriamanagementsystem.utils.CartManager

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Checkout button
        binding.btnCheckout.setOnClickListener {

            if (CartManager.cartItems.isEmpty()) {

                Toast.makeText(
                    this,
                    "Your cart is empty",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            startActivity(
                Intent(
                    this,
                    CheckoutActivity::class.java
                )
            )
        }

        displayCart()
    }

    override fun onResume() {
        super.onResume()

        displayCart()
    }

    private fun displayCart() {

        binding.cartContainer.removeAllViews()

        if (CartManager.cartItems.isEmpty()) {

            binding.tvEmptyCart.visibility = TextView.VISIBLE
            binding.btnCheckout.isEnabled = false

        } else {

            binding.tvEmptyCart.visibility = TextView.GONE
            binding.btnCheckout.isEnabled = true

            CartManager.cartItems.forEach { food ->

                addFoodToCartView(food)
            }
        }

        binding.tvTotal.text =
            String.format(
                "Total: R%.2f",
                CartManager.getTotal()
            )
    }

    private fun addFoodToCartView(food: Food) {

        val itemLayout = LinearLayout(this)

        itemLayout.orientation =
            LinearLayout.VERTICAL

        itemLayout.setPadding(
            20,
            20,
            20,
            20
        )

        val name = TextView(this)

        name.text = food.name
        name.textSize = 18f
        name.setTypeface(null, android.graphics.Typeface.BOLD)

        val price = TextView(this)

        price.text =
            String.format(
                "R%.2f",
                food.price
            )

        price.textSize = 16f

        val removeButton =
            android.widget.Button(this)

        removeButton.text = "Remove"

        removeButton.setOnClickListener {

            CartManager.removeFromCart(food)

            displayCart()

            Toast.makeText(
                this,
                "${food.name} removed",
                Toast.LENGTH_SHORT
            ).show()
        }

        itemLayout.addView(name)
        itemLayout.addView(price)
        itemLayout.addView(removeButton)

        binding.cartContainer.addView(itemLayout)
    }
}