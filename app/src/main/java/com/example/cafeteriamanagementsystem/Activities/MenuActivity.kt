package com.example.cafeteriamanagementsystem.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

        binding.recyclerFood.layoutManager =
            LinearLayoutManager(this)

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