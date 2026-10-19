package com.example.cafeteriamanagementsystem.utils

import com.example.cafeteriamanagementsystem.models.Food
import com.example.cafeteriamanagementsystem.models.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OrderManager {

    val orders = mutableListOf<Order>()

    fun placeOrder(
        customerName: String,
        studentNumber: String,
        phone: String,
        items: List<Food>,
        total: Double
    ): Order {

        val order = Order(

            orderId =
                "ORD-" +
                        System.currentTimeMillis()
                            .toString()
                            .takeLast(6),

            customerName = customerName,

            studentNumber = studentNumber,

            phone = phone,

            items = items.toList(),

            total = total,

            status = "NEW",

            dateTime =
                SimpleDateFormat(
                    "dd MMM yyyy, HH:mm",
                    Locale.getDefault()
                ).format(Date())
        )

        orders.add(order)

        return order
    }

    fun updateStatus(
        orderId: String,
        status: String
    ) {

        val order =
            orders.find {
                it.orderId == orderId
            }

        order?.status = status
    }
}