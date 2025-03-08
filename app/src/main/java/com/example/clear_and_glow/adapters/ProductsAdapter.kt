package com.example.clear_and_glow.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
            ImageLoader.getInstance().loadImage(product.picture, binding.productIMGPicture)


            if (isGlobal)
                handleSelectedGlobalProduct(product)
            else
                binding.root.setOnClickListener { onProductClick(product) }

            updateUi(product)
        }

        private fun toggleSelection(product: Product) {
            if (selectedProducts.contains(product)) {
                selectedProducts.remove(product)
            } else {
                selectedProducts.add(product)
            }
            notifyItemChanged(adapterPosition)
        }

        private fun handleSelectedGlobalProduct(product: Product) {
            binding.root.isSelected = selectedProducts.contains(product)
            binding.root.setOnClickListener {
                toggleSelection(product)
            }
        }

        private fun updateUi(product: Product) {
            if (isGlobal) {
                binding.productLYExpiredDate.visibility = View.GONE
                binding.productLYOpenedDate.visibility = View.GONE
            } else {
                binding.productLYExpiredDate.visibility = View.VISIBLE
                binding.productTXTExpiredDate.text = product.calculateExpiryDate()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(newProducts: List<Product>) {
        selectedProducts.clear()
        productList = newProducts
        notifyDataSetChanged()
    }

    fun getSelectedProducts(): List<Product> = selectedProducts
}
