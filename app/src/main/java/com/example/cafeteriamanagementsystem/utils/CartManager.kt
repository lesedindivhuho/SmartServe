package com.example.cafeteriamanagementsystem.utils

import com.example.cafeteriamanagementsystem.models.Food

object CartManager {

    val cartItems = mutableListOf<Food>()

    fun addToCart(food: Food) {
        cartItems.add(food)
    }

    fun removeFromCart(food: Food) {
        cartItems.remove(food)
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getTotal(): Double {
        return cartItems.sumOf { it.price }
    }
}