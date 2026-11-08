
package com.example.cafeteriamanagementsystem.activities

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.databinding.ActivityManageMenuBinding
import com.example.cafeteriamanagementsystem.models.UserRole
import com.example.cafeteriamanagementsystem.utils.MenuManager
import com.example.cafeteriamanagementsystem.utils.SessionManager

class ManageMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageMenuBinding

    private val green = Color.rgb(22, 115, 59)
    private val yellow = Color.rgb(255, 193, 7)
    private val red = Color.rgb(211, 47, 47)
    private val textColor = Color.rgb(35, 35, 35)
    private val borderColor = Color.rgb(230, 230, 230)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityManageMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Only managers can access this page.
        if (SessionManager.currentUser?.role != UserRole.MANAGER) {
            Toast.makeText(
                this,
                "Manager access required",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnAddFood.setOnClickListener {
            showFoodDialog(null)
        }

        displayMenu()
    }

    override fun onResume() {
        super.onResume()

        if (::binding.isInitialized) {
            displayMenu()
        }
    }

    private fun displayMenu() {

        binding.menuContainer.removeAllViews()

        val foods = MenuManager.foodItems.toList()

        if (foods.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "No menu items available."
                textSize = 15f
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)
                setPadding(dp(10), dp(30), dp(10), dp(30))
            }

            binding.menuContainer.addView(emptyText)
            return
        }

        // Display each food as a table row.
        foods.forEach { food ->

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(10), dp(10), dp(10), dp(10))
                setBackgroundColor(Color.WHITE)
            }

            // Food name.
            val name = TextView(this).apply {
                text = food.name
                textSize = 12f
                setTextColor(textColor)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER_VERTICAL
            }

            row.addView(
                name,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.6f
                )
            )

            // Food price.
            val price = TextView(this).apply {
                text = String.format("R%.2f", food.price)
                textSize = 12f
                setTextColor(textColor)
                gravity = Gravity.CENTER_VERTICAL
            }

            row.addView(
                price,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.9f
                )
            )

            // Action buttons.
            val actions = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }

            val editButton = createActionButton(
                text = "Edit",
                color = yellow,
                textColor = Color.rgb(45, 45, 45)
            )

            editButton.setOnClickListener {
                showFoodDialog(food.id)
            }

            val deleteButton = createActionButton(
                text = "Delete",
                color = red,
                textColor = Color.WHITE
            )

            deleteButton.setOnClickListener {
                confirmDelete(food.id, food.name)
            }

            actions.addView(
                editButton,
                LinearLayout.LayoutParams(
                    0,
                    dp(36),
                    1f
                ).apply {
                    marginEnd = dp(5)
                }
            )

            actions.addView(
                deleteButton,
                LinearLayout.LayoutParams(
                    0,
                    dp(36),
                    1f
                ).apply {
                    marginStart = dp(3)
                }
            )

            row.addView(
                actions,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.8f
                )
            )

            binding.menuContainer.addView(row)

            // Thin divider between items.
            val divider = View(this).apply {
                setBackgroundColor(borderColor)
            }

            binding.menuContainer.addView(
                divider,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(1)
                )
            )
        }
    }

    private fun createActionButton(
        text: String,
        color: Int,
        textColor: Int
    ): Button {

        return Button(this).apply {
            this.text = text
            textSize = 10f
            isAllCaps = false
            setTypeface(null, Typeface.BOLD)
            setTextColor(textColor)

            minWidth = 0
            minimumWidth = 0
            minHeight = dp(36)
            minimumHeight = dp(36)

            setPadding(dp(2), 0, dp(2), 0)

            background = GradientDrawable().apply {
                setColor(color)
                cornerRadius = dp(5).toFloat()
            }
        }
    }

    private fun showFoodDialog(foodId: Int?) {

        val existingFood = MenuManager.foodItems.find {
            it.id == foodId
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(8), dp(24), dp(8))
        }

        val nameInput = EditText(this).apply {
            hint = "Food name"
            setSingleLine(true)
            setText(existingFood?.name ?: "")
        }

        val descriptionInput = EditText(this).apply {
            hint = "Description"
            setText(existingFood?.description ?: "")
        }

        val priceInput = EditText(this).apply {
            hint = "Price (R)"
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL
            setSingleLine(true)

            if (existingFood != null) {
                setText(existingFood.price.toString())
            }
        }

        layout.addView(nameInput)
        layout.addView(descriptionInput)
        layout.addView(priceInput)

        val isNewFood = existingFood == null

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (isNewFood) "Add Menu Item" else "Edit Menu Item")
            .setView(layout)
            .setPositiveButton(
                if (isNewFood) "ADD" else "SAVE",
                null
            )
            .setNegativeButton("CANCEL", null)
            .create()

        dialog.setOnShowListener {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {

                    val name = nameInput.text.toString().trim()
                    val description =
                        descriptionInput.text.toString().trim()
                    val price =
                        priceInput.text.toString().trim().toDoubleOrNull()

                    if (name.isEmpty()) {
                        nameInput.error = "Enter a food name"
                        return@setOnClickListener
                    }

                    if (description.isEmpty()) {
                        descriptionInput.error =
                            "Enter a description"
                        return@setOnClickListener
                    }

                    if (price == null ||
                        price <= 0 ||
                        price.isNaN() ||
                        price.isInfinite()
                    ) {
                        priceInput.error = "Enter a valid price"
                        return@setOnClickListener
                    }

                    if (isNewFood) {
                        MenuManager.addFood(
                            name,
                            description,
                            price
                        )

                        Toast.makeText(
                            this,
                            "Food added successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        MenuManager.updateFood(
                            existingFood!!.id,
                            name,
                            description,
                            price
                        )

                        Toast.makeText(
                            this,
                            "Food updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    dialog.dismiss()
                    displayMenu()
                }
        }

        dialog.show()
    }

    private fun confirmDelete(
        foodId: Int,
        foodName: String
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Menu Item")
            .setMessage(
                "Are you sure you want to delete $foodName?"
            )
            .setPositiveButton("DELETE") { _, _ ->

                MenuManager.deleteFood(foodId)

                Toast.makeText(
                    this,
                    "$foodName deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()

                displayMenu()
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}