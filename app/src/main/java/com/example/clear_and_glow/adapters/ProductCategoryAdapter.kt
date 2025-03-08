package com.example.clear_and_glow.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clear_and_glow.databinding.ProductCategoryItemBinding
import com.example.clear_and_glow.models.ProductCategory

class ProductCategoryAdapter(
    private var categoriesList: List<ProductCategory>,
    private val onCategoryClick: (ProductCategory) -> Unit
) : RecyclerView.Adapter<ProductCategoryAdapter.ProductCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        val binding =
            ProductCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductCategoryViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {
        return holder.bind(categoriesList[position])
    }

    override fun getItemCount():Int = categoriesList.size

    fun updateList(newCategoriesList: List<ProductCategory>){
        categoriesList = newCategoriesList
        notifyDataSetChanged()
    }

    inner class ProductCategoryViewHolder(private val binding: ProductCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(category: ProductCategory) {
                binding.categoriesTXTItem.text = category.name
                binding.categoriesBTNItem.setIconResource(category.iconResId)
                binding.root.setOnClickListener{ onCategoryClick(category)}
            }
        }
    }
