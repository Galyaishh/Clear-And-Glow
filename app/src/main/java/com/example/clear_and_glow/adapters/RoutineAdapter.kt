package com.example.clear_and_glow.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clear_and_glow.R
import com.example.clear_and_glow.databinding.ProductItemBinding
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.utilities.ImageLoader
import java.util.Collections

class RoutineAdapter(
    private var productList: MutableList<Product>,
    private var isEditMode: Boolean = false,
    private val onRemoveClick: (Product) -> Unit
) : RecyclerView.Adapter<RoutineAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.productLBLTitle.text = product.name
            binding.productTXTOpenedDate.text = product.openingDate ?: "N/A"
            binding.productTXTExpiredDate.text = product.calculateExpiryDate() ?: "N/A"
            binding.productLBLSkinType.text =
                product.category //instead of skin type i decided to show category
            binding.productTXTBrand.visibility = View.GONE

            ImageLoader.getInstance().loadImage(product.picture, binding.productIMGPicture)

            if (isEditMode) {
                binding.productBTNDelete.visibility = View.VISIBLE
                binding.productBTNDelete.setOnClickListener {
                    onRemoveClick(product)
                    removeProduct(product)
                }
            } else {
                binding.productBTNDelete.visibility = View.GONE
                binding.productBTNDelete.setOnClickListener(null)
            }

        }
    }

    fun removeProduct(product: Product) {
        val position = productList.indexOf(product)
        if (position != -1) {
            productList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newProducts: List<Product>) {
        productList.clear()
        productList.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun setEditMode(isEdit: Boolean) {
        isEditMode = isEdit
        notifyDataSetChanged()
    }

    fun getProducts(): List<Product> = productList
}