package com.example.clear_and_glow.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clear_and_glow.databinding.FeedProductItemBinding
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.utilities.ImageLoader

class FeedProductsAdapter(private val products: List<Product>) :
    RecyclerView.Adapter<FeedProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: FeedProductItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            ImageLoader.getInstance().loadImage(product.picture, binding.feedProductIMGPicture)
            binding.feedProductLBLProductName.text = product.name
            binding.feedProductLYBrand.visibility= View.VISIBLE
            binding.feedProductTXTBrand.text = product.brand
            binding.feedProductLBLCategory.text = product.category
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = FeedProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

}