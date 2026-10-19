package com.example.cafeteriamanagementsystem.models

data class Order(

    val orderId: String,

    val customerName: String,

    val studentNumber: String,

    val phone: String,

    val items: List<Food>,

    val total: Double,

    var status: String = "NEW",

    val dateTime: String
)