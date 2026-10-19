package com.example.cafeteriamanagementsystem.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeteriamanagementsystem.databinding.ItemFoodBinding
import com.example.cafeteriamanagementsystem.models.Food
import com.example.cafeteriamanagementsystem.utils.CartManager

class FoodAdapter(
    private val foodList: List<Food>
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(
        val binding: ItemFoodBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodViewHolder {

        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FoodViewHolder,
        position: Int
    ) {

        val food = foodList[position]

        holder.binding.imgFood.setImageResource(
            food.imageResId
        )

        holder.binding.tvFoodName.text = food.name

        holder.binding.tvDescription.text =
            food.description

        holder.binding.tvPrice.text =
            String.format("R%.2f", food.price)

        holder.binding.btnAddToCart.setOnClickListener {

            CartManager.addToCart(food)

            Toast.makeText(
                holder.itemView.context,
                "${food.name} added to cart",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}