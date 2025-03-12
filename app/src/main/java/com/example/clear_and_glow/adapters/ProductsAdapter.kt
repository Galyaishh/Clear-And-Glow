package com.example.clear_and_glow.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clear_and_glow.R
import com.example.clear_and_glow.databinding.ProductItemBinding
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.utilities.ImageLoader


class ProductsAdapter(
    private var productList: List<Product>,
    private val onProductClick: (Product) -> Unit,
    private val isGlobal: Boolean = false
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    private val selectedProducts: MutableList<Product> = mutableListOf()

    inner class ProductViewHolder(private val binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.productLBLTitle.text = product.name
            binding.productTXTBrand.text = product.brand
            binding.productTXTSkinType.text = product.skinType
            binding.productTXTIngredients.text = product.ingredients.joinToString(", ")


            ImageLoader.getInstance().loadImage(product.picture, binding.productIMGPicture)

            if (isGlobal) {
                binding.root.isSelected = selectedProducts.contains(product)
                binding.root.setOnClickListener {
                    toggleSelection(product)
                    updateUi(product)
                }
            } else {
                binding.root.setOnClickListener { onProductClick(product) }
            }

            updateUi(product)

            if (selectedProducts.contains(product)) {
                binding.productCVData.setCardBackgroundColor(binding.root.context.getColor(R.color.selected_product_color))
            } else {
                binding.productCVData.setCardBackgroundColor(binding.root.context.getColor(R.color.white))
            }
        }

        private fun updateUi(product: Product) {
            if (isGlobal) {
                binding.productLYSkinType.visibility = View.VISIBLE
                binding.productLYExpiredDate.visibility = View.GONE
                binding.productLYOpenedDate.visibility = View.GONE
            } else {
                if (product.openingDate == null) {
                    binding.productTXTOpenedDate.text = "Not opened yet"
                    binding.productLYExpiredDate.visibility = View.GONE
                } else {
                    binding.productTXTOpenedDate.text = product.openingDate
                    binding.productTXTExpiredDate.text = product.calculateExpiryDate()
                    binding.productLYExpiredDate.visibility = View.VISIBLE
                }
                binding.productLYOpenedDate.visibility = View.VISIBLE
                binding.productLYSkinType.visibility = View.GONE
            }
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

    fun toggleSelection(product: Product) {
        if (selectedProducts.contains(product)) {
            selectedProducts.remove(product)
        } else {
            selectedProducts.add(product)
        }
        notifyItemChanged(productList.indexOf(product))
    }

    fun getSelectedProducts(): List<Product> = selectedProducts

    fun updateList(newProducts: List<Product>) {
        selectedProducts.clear()
        productList = newProducts
        notifyDataSetChanged()
    }
}
