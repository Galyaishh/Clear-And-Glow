package com.example.clear_and_glow.adapters

import android.util.Log
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
            binding.productTXTSkinType.text = product.skinType
            binding.productTXTIngredients.text = product.ingredients.joinToString(", ")

            ImageLoader.getInstance().loadImage(product.picture, binding.productIMGPicture)

            if (isGlobal) {
                binding.root.isSelected = selectedProducts.contains(product)
                binding.root.setOnClickListener { toggleSelection(product) }
            } else {
                binding.root.setOnClickListener { onProductClick(product) }
            }

            updateUi(product)
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
        Log.d("ProductsAdapter", "Binding product: ${productList[position].name}")

    }

    override fun getItemCount(): Int = productList.size

    fun toggleSelection(product: Product) {
        if (selectedProducts.contains(product)) {
            selectedProducts.remove(product)
        } else {
            selectedProducts.add(product)
        }

        notifyDataSetChanged()
    }

    fun getSelectedProducts(): List<Product> = selectedProducts

    fun updateList(newProducts: List<Product>) {
        selectedProducts.clear()
        productList = newProducts
        notifyDataSetChanged()
    }
}


//class ProductsAdapter(
//    private var productList: List<Product>,
//    private val onProductClick: (Product) -> Unit,
//    private val isGlobal: Boolean = false
//) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {
//
//    private val selectedProducts: MutableList<Product> = mutableListOf()
//
//
//    inner class ProductViewHolder(private val binding: ProductItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(product: Product) {
//            binding.productLBLTitle.text = product.name
//            binding.productTXTBrand.text = product.brand
//            binding.productTXTSkinType.text = product.skinType
//            binding.productTXTIngredients.text = product.ingredients.joinToString(", ")
//
//            ImageLoader.getInstance().loadImage(product.picture, binding.productIMGPicture)
//            if (isGlobal)
//                handleSelectedGlobalProduct(product)
//            else
//                binding.root.setOnClickListener { onProductClick(product) }
//
//            updateUi(product)
//        }
//
//        fun toggleSelection(product: Product) {
//            if (selectedProducts.contains(product)) {
//                selectedProducts.remove(product)
//            } else {
//                selectedProducts.add(product)
//            }
//
//            // Ensure UI updates properly
//            binding.root.isSelected = selectedProducts.contains(product)
//            notifyItemChanged(adapterPosition)
//        }
//
//
//        private fun handleSelectedGlobalProduct(product: Product) {
//            binding.root.isSelected = selectedProducts.contains(product)
//            binding.root.setOnClickListener {
//                toggleSelection(product)
//            }
//        }
//
//
//        private fun updateUi(product: Product) {
//            if (isGlobal) {
//                binding.productLYSkinType.visibility = View.VISIBLE
//                binding.productLYExpiredDate.visibility = View.GONE
//                binding.productLYOpenedDate.visibility = View.GONE
//            } else {
//                if (product.openingDate == null) {
//                    binding.productTXTOpenedDate.text = "Not opened yet"
//                    binding.productLYExpiredDate.visibility = View.GONE
//                } else {
//                    binding.productTXTOpenedDate.text = product.openingDate
//                    binding.productTXTExpiredDate.text = product.calculateExpiryDate()
//                    binding.productLYExpiredDate.visibility = View.VISIBLE
//                }
//                binding.productLYOpenedDate.visibility = View.VISIBLE
//                binding.productLYSkinType.visibility = View.GONE
//            }
//        }
//    }
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
//        val binding =
//            ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ProductViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
//        holder.bind(productList[position])
//    }
//
//    override fun getItemCount(): Int = productList.size
//
//    fun updateList(newProducts: List<Product>) {
//        selectedProducts.clear()
//        productList = newProducts
//        notifyDataSetChanged()
//    }
//
//    fun getAllProducts(): List<Product> = productList
//
//
//    fun getSelectedProducts(): List<Product> = selectedProducts
//}