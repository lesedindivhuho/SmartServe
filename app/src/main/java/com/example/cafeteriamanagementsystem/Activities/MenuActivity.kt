
package com.example.cafeteriamanagementsystem.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cafeteriamanagementsystem.adapters.FoodAdapter
import com.example.cafeteriamanagementsystem.databinding.ActivityMenuBinding
import com.example.cafeteriamanagementsystem.utils.MenuManager

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Display menu items in 2 columns
        binding.recyclerFood.layoutManager =
            GridLayoutManager(this, 2)

        loadMenu()
    }

    override fun onResume() {
        super.onResume()
        loadMenu()
    }

    private fun loadMenu() {
        binding.recyclerFood.adapter =
            FoodAdapter(MenuManager.foodItems)
    }
}
