
package com.example.cafeteriamanagementsystem.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.databinding.ActivityCartBinding
import com.example.cafeteriamanagementsystem.models.Food
import com.example.cafeteriamanagementsystem.utils.CartManager

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    private val green = Color.rgb(8, 115, 51)
    private val darkGreen = Color.rgb(46, 125, 50)
    private val red = Color.rgb(190, 35, 35)
    private val lightGreen = Color.rgb(232, 245, 233)
    private val darkText = Color.rgb(35, 35, 35)
    private val lightBorder = Color.rgb(225, 225, 225)

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

        // Show the success message when requested
        // by the activity returning to the cart.
        if (intent.getBooleanExtra("ORDER_PLACED", false)) {
            showOrderSuccess()
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
                Intent(this, CheckoutActivity::class.java)
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

        val items = CartManager.cartItems

        if (items.isEmpty()) {

            binding.tvEmptyCart.visibility = View.VISIBLE
            binding.btnCheckout.isEnabled = false
            binding.btnCheckout.alpha = 0.5f

        } else {

            binding.tvEmptyCart.visibility = View.GONE
            binding.btnCheckout.isEnabled = true
            binding.btnCheckout.alpha = 1f

            // Table header
            addTableHeader()

            // Individual item rows
            items.toList().forEach { food ->
                addFoodToCartView(food)
            }
        }

        // Update total
        binding.tvTotal.text = String.format(
            "Total: R%.2f",
            CartManager.getTotal()
        )

        binding.tvTotal.setTextColor(darkGreen)
    }

    private fun addTableHeader() {

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(12), dp(8), dp(12))
            setBackgroundColor(Color.rgb(248, 249, 248))
        }

        header.addView(
            createCell("Item", 2f, true),
            cellParams(2f)
        )

        header.addView(
            createCell("Price", 1f, true),
            cellParams(1f)
        )

        header.addView(
            createCell("Action", 1.3f, true),
            cellParams(1.3f)
        )

        binding.cartContainer.addView(header)

        addDivider()
    }

    private fun addFoodToCartView(food: Food) {

        // Table row
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(10), dp(8), dp(10))
            setBackgroundColor(Color.WHITE)
        }

        // Food name
        val name = createCell(
            food.name,
            2f,
            false
        )

        // Food price
        val price = createCell(
            String.format("R%.2f", food.price),
            1f,
            false
        )

        // Remove button
        val removeButton = Button(this).apply {
            text = "Remove"
            textSize = 11f
            isAllCaps = false
            setTextColor(Color.WHITE)
            setPadding(dp(4), 0, dp(4), 0)
            minHeight = dp(38)
            minimumHeight = dp(38)
            minWidth = 0
            minimumWidth = 0

            background = roundedBackground(
                red,
                dp(6)
            )

            setOnClickListener {

                CartManager.removeFromCart(food)

                displayCart()

                Toast.makeText(
                    this@CartActivity,
                    "${food.name} removed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        row.addView(name, cellParams(2f))
        row.addView(price, cellParams(1f))

        val actionContainer = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            orientation = LinearLayout.VERTICAL
        }

        actionContainer.addView(
            removeButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(38)
            )
        )

        row.addView(
            actionContainer,
            cellParams(1.3f)
        )

        binding.cartContainer.addView(row)

        addDivider()
    }

    private fun createCell(
        text: String,
        weight: Float,
        bold: Boolean
    ): TextView {

        return TextView(this).apply {
            this.text = text
            textSize = 13f
            setTextColor(darkText)
            gravity = Gravity.CENTER_VERTICAL

            if (bold) {
                setTypeface(null, Typeface.BOLD)
            }

            if (!bold) {
                setPadding(0, dp(4), 0, dp(4))
            }
        }
    }

    private fun cellParams(
        weight: Float
    ): LinearLayout.LayoutParams {

        return LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            weight
        ).apply {
            marginEnd = dp(5)
        }
    }

    private fun addDivider() {

        val divider = View(this).apply {
            setBackgroundColor(lightBorder)
        }

        binding.cartContainer.addView(
            divider,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
            )
        )
    }

    private fun showOrderSuccess() {

        // The XML layout must contain these IDs.
        val successLayout =
            binding.root.findViewById<LinearLayout>(
                com.example.cafeteriamanagementsystem.R.id.layoutOrderSuccess
            )

        successLayout?.visibility = View.VISIBLE
    }

    private fun roundedBackground(
        color: Int,
        radius: Int
    ): GradientDrawable {

        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }
    }

    private fun dp(value: Int): Int {

        return (value * resources.displayMetrics.density)
            .toInt()
    }
}