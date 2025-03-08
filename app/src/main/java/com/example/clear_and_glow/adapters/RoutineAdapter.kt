package com.example.clear_and_glow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clear_and_glow.R
import com.example.clear_and_glow.databinding.RoutineProductItemBinding
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.utilities.ImageLoader

class RoutineAdapter( private var productList: List<Product>
) : RecyclerView.Adapter<RoutineAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: RoutineProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.productLBLTitle.text = product.name
            binding.productTXTOpenedDate.text = product.openingDate ?: "Not set"
            binding.productTXTExpiredDate.text = product.calculateExpiryDate() ?: "N/A"

            // Load product image
            ImageLoader.getInstance().loadImage(product.picture, binding.productIMGPicture)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = RoutineProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newProducts: List<Product>) {
        productList = newProducts
        notifyDataSetChanged()
    }
}
