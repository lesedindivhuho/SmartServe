
package com.example.cafeteriamanagementsystem.activities

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.models.UserRole
import com.example.cafeteriamanagementsystem.utils.OrderManager
import com.example.cafeteriamanagementsystem.utils.SessionManager

class OrdersActivity : AppCompatActivity() {

    private lateinit var ordersContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use your existing orders.xml layout
        setContentView(R.layout.orders)

        // Find the views directly
        val toolbar =
            findViewById<com.google.android.material.appbar.MaterialToolbar>(
                R.id.toolbar
            )

        ordersContainer =
            findViewById(R.id.ordersContainer)

        // Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val role = SessionManager.currentUser?.role

        if (role == null) {
            finish()
            return
        }

        displayOrders()
    }

    override fun onResume() {
        super.onResume()

        if (::ordersContainer.isInitialized) {
            displayOrders()
        }
    }

    private fun displayOrders() {

        ordersContainer.removeAllViews()

        val visibleOrders = when (SessionManager.currentUser?.role) {
            UserRole.STUDENT -> OrderManager.orders.filter {
                it.studentNumber == SessionManager.currentUser?.username
            }
            else -> OrderManager.orders
        }

        if (visibleOrders.isEmpty()) {

            val emptyText = TextView(this)

            emptyText.text =
                "No orders have been placed yet."

            emptyText.textSize = 18f

            emptyText.gravity =
                Gravity.CENTER

            emptyText.setPadding(
                20,
                50,
                20,
                50
            )

            ordersContainer.addView(
                emptyText
            )

            return
        }

        // Display newest order first
        visibleOrders
            .asReversed()
            .forEach { order ->

                createOrderView(order)
            }
    }

    private fun createOrderView(
        order: com.example.cafeteriamanagementsystem.models.Order
    ) {

        val orderLayout =
            LinearLayout(this)

        orderLayout.orientation =
            LinearLayout.VERTICAL

        orderLayout.setPadding(
            20,
            20,
            20,
            20
        )


        // ORDER NUMBER


        val orderNumber =
            TextView(this)

        orderNumber.text =
            "Order: ${order.orderId}"

        orderNumber.textSize = 21f

        orderNumber.setTypeface(
            null,
            Typeface.BOLD
        )

        orderLayout.addView(
            orderNumber
        )


        // CUSTOMER


        val customer =
            TextView(this)

        customer.text =
            "Customer: ${order.customerName}"

        customer.textSize = 16f

        orderLayout.addView(
            customer
        )


        // STUDENT NUMBER


        val studentNumber =
            TextView(this)

        studentNumber.text =
            "Student Number: ${order.studentNumber}"

        studentNumber.textSize = 16f

        orderLayout.addView(
            studentNumber
        )


        // PHONE


        val phone =
            TextView(this)

        phone.text =
            "Phone: ${order.phone}"

        phone.textSize = 16f

        orderLayout.addView(
            phone
        )


        // DATE


        val date =
            TextView(this)

        date.text =
            "Date: ${order.dateTime}"

        date.textSize = 15f

        date.setPadding(
            0,
            5,
            0,
            10
        )

        orderLayout.addView(
            date
        )


        // ITEMS TITLE


        val itemsTitle =
            TextView(this)

        itemsTitle.text =
            "ORDER ITEMS"

        itemsTitle.textSize = 17f

        itemsTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        orderLayout.addView(
            itemsTitle
        )


        // ITEMS


        val items =
            TextView(this)

        val itemText =
            StringBuilder()

        order.items.forEach { food ->

            itemText.append(
                "• ${food.name} - R%.2f\n"
                    .format(food.price)
            )
        }

        items.text =
            itemText.toString()

        items.textSize = 16f

        items.setPadding(
            0,
            5,
            0,
            5
        )

        orderLayout.addView(
            items
        )


        // TOTAL


        val total =
            TextView(this)

        total.text =
            "TOTAL: R%.2f"
                .format(order.total)

        total.textSize = 20f

        total.setTypeface(
            null,
            Typeface.BOLD
        )

        total.setTextColor(
            resources.getColor(
                R.color.dark_green,
                theme
            )
        )

        total.setPadding(
            0,
            10,
            0,
            10
        )

        orderLayout.addView(
            total
        )


        // STATUS


        val status =
            TextView(this)

        status.text =
            "Status: ${order.status}"

        status.textSize = 17f

        status.setTypeface(
            null,
            Typeface.BOLD
        )

        orderLayout.addView(
            status
        )


        // PREPARING BUTTON


        val preparingButton =
            Button(this)

        preparingButton.text =
            "MARK AS PREPARING"

        preparingButton.setOnClickListener {

            OrderManager.updateStatus(
                order.orderId,
                "PREPARING"
            )

            displayOrders()
        }

        orderLayout.addView(
            preparingButton
        )


        // COMPLETED BUTTON


        val completedButton =
            Button(this)

        completedButton.text =
            "MARK AS COMPLETED"

        completedButton.setOnClickListener {

            OrderManager.updateStatus(
                order.orderId,
                "COMPLETED"
            )

            displayOrders()
        }

        orderLayout.addView(
            completedButton
        )


        // ADD ORDER TO SCREEN


        ordersContainer.addView(
            orderLayout
        )


        // DIVIDER


        val divider =
            View(this)

        divider.setBackgroundColor(
            0xFFCCCCCC.toInt()
        )

        val dividerParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
            )

        dividerParams.setMargins(
            10,
            20,
            10,
            20
        )

        divider.layoutParams =
            dividerParams

        ordersContainer.addView(
            divider
        )
    }
}