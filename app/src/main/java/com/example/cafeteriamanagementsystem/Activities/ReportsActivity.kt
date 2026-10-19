package com.example.cafeteriamanagementsystem.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cafeteriamanagementsystem.R
import com.example.cafeteriamanagementsystem.models.UserRole
import com.example.cafeteriamanagementsystem.utils.SessionManager

class ReportsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ==========================================
        // MANAGER ACCESS ONLY
        // ==========================================

        if (SessionManager.currentUser?.role != UserRole.MANAGER) {
            finish()
            return
        }

        // ==========================================
        // SET UP SCREEN
        // ==========================================

        enableEdgeToEdge()

        setContentView(R.layout.activity_reports)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }
}