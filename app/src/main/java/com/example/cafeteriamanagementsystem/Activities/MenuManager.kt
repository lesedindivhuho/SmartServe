package com.example.cafeteriamanagementsystem.utils

import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.models.Food

object MenuManager {

    val foodItems = mutableListOf(
        Food(
            id = 1,
            name = "Chicken Burger",
            description = "Grilled chicken burger with lettuce and sauce",
            price = 45.00,
            imageResId = R.drawable.chicken_burger
        ),

        Food(
            id = 2,
            name = "Pizza",
            description = "Cheesy pizza with tomato and mixed toppings",
            price = 65.00,
            imageResId = R.drawable.pizza
        ),

        Food(
            id = 3,
            name = "Chicken Meal",
            description = "Grilled chicken served with chips",
            price = 55.00,
            imageResId = R.drawable.chicken_meal
        ),

        Food(
            id = 4,
            name = "Chips",
            description = "Crispy golden potato chips",
            price = 25.00,
            imageResId = R.drawable.chips
        ),

        Food(
            id = 5,
            name = "Sandwich",
            description = "Fresh sandwich with chicken, lettuce and cheese",
            price = 35.00,
            imageResId = R.drawable.sandwich
        )
    )

    private var nextId = 6

    fun addFood(
        name: String,
        description: String,
        price: Double
    ) {

        foodItems.add(
            Food(
                id = nextId++,
                name = name,
                description = description,
                price = price,
                imageResId = R.drawable.sandwich
            )
        )
    }

    fun updateFood(
        id: Int,
        name: String,
        description: String,
        price: Double
    ) {

        val index = foodItems.indexOfFirst {
            it.id == id
        }

        if (index != -1) {

            val oldFood = foodItems[index]

            foodItems[index] = Food(
                id = oldFood.id,
                name = name,
                description = description,
                price = price,
                imageResId = oldFood.imageResId
            )
        }
    }

    fun deleteFood(id: Int) {

        foodItems.removeAll {
            it.id == id
        }
    }
}