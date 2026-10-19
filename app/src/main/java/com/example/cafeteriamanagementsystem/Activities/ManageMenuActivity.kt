package com.example.cafeteriamanagementsystem.activities

import android.app.AlertDialog
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityManageMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Only managers can access this page
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

        if (MenuManager.foodItems.isEmpty()) {

            val emptyText = TextView(this)

            emptyText.text =
                "No food items are currently available."

            emptyText.textSize = 18f

            emptyText.setPadding(
                10,
                30,
                10,
                30
            )

            binding.menuContainer.addView(emptyText)

            return
        }

        MenuManager.foodItems.forEach { food ->

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

            name.textSize = 20f

            name.setTextColor(
                resources.getColor(
                    R.color.dark_green,
                    theme
                )
            )

            name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )

            val description = TextView(this)

            description.text =
                food.description

            description.textSize = 15f

            val price = TextView(this)

            price.text =
                String.format(
                    "Price: R%.2f",
                    food.price
                )

            price.textSize = 17f

            price.setPadding(
                0,
                8,
                0,
                8
            )

            val buttonLayout =
                LinearLayout(this)

            buttonLayout.orientation =
                LinearLayout.HORIZONTAL

            val editButton =
                android.widget.Button(this)

            editButton.text = "EDIT"

            editButton.setOnClickListener {
                showFoodDialog(food.id)
            }

            val deleteButton =
                android.widget.Button(this)

            deleteButton.text = "DELETE"

            deleteButton.setOnClickListener {
                confirmDelete(food.id, food.name)
            }

            buttonLayout.addView(editButton)

            buttonLayout.addView(deleteButton)

            itemLayout.addView(name)

            itemLayout.addView(description)

            itemLayout.addView(price)

            itemLayout.addView(buttonLayout)

            binding.menuContainer.addView(itemLayout)
        }
    }

    private fun showFoodDialog(foodId: Int?) {

        val existingFood =
            MenuManager.foodItems.find {
                it.id == foodId
            }

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            40,
            10,
            40,
            10
        )

        val nameInput =
            EditText(this)

        nameInput.hint = "Food name"

        nameInput.setText(
            existingFood?.name ?: ""
        )

        val descriptionInput =
            EditText(this)

        descriptionInput.hint =
            "Description"

        descriptionInput.setText(
            existingFood?.description ?: ""
        )

        val priceInput =
            EditText(this)

        priceInput.hint =
            "Price"

        priceInput.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        if (existingFood != null) {

            priceInput.setText(
                existingFood.price.toString()
            )
        }

        layout.addView(nameInput)

        layout.addView(descriptionInput)

        layout.addView(priceInput)

        val dialogTitle =
            if (existingFood == null) {
                "Add Food"
            } else {
                "Edit Food"
            }

        AlertDialog.Builder(this)
            .setTitle(dialogTitle)
            .setView(layout)
            .setPositiveButton(
                if (existingFood == null) {
                    "ADD"
                } else {
                    "SAVE"
                }
            ) { _, _ ->

                val name =
                    nameInput.text.toString().trim()

                val description =
                    descriptionInput.text.toString().trim()

                val price =
                    priceInput.text.toString()
                        .toDoubleOrNull()

                if (
                    name.isEmpty() ||
                    description.isEmpty() ||
                    price == null ||
                    price <= 0
                ) {

                    Toast.makeText(
                        this,
                        "Please enter valid food details",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                if (existingFood == null) {

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
                        existingFood.id,
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

                displayMenu()
            }
            .setNegativeButton(
                "CANCEL",
                null
            )
            .show()
    }

    private fun confirmDelete(
        foodId: Int,
        foodName: String
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Food")
            .setMessage(
                "Are you sure you want to remove $foodName from the menu?"
            )
            .setPositiveButton("DELETE") { _, _ ->

                MenuManager.deleteFood(foodId)

                Toast.makeText(
                    this,
                    "$foodName removed from menu",
                    Toast.LENGTH_SHORT
                ).show()

                displayMenu()
            }
            .setNegativeButton(
                "CANCEL",
                null
            )
            .show()
    }
}