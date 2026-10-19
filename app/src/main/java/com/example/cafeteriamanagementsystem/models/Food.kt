package com.example.cafeteriamanagementsystem.models

data class Food(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageResId: Int
)