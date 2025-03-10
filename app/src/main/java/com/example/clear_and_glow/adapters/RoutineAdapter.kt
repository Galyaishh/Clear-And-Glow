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
            binding.productLBLSkinType.text = product.category

            // Load product image
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


//class RoutineAdapter(
//    private var productList: List<Product>,
//    private val onStartDrag: ((RecyclerView.ViewHolder) -> Unit)? = null,
//    private val onRemoveProduct: ((Product) -> Unit)? = null,
//    private val orderChangeListener: RoutineOrderChangeListener? = null
//
//) : RecyclerView.Adapter<RoutineAdapter.ProductViewHolder>(), ItemTouchHelperAdapter {
//
//    inner class ProductViewHolder(private val binding: ProductItemBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        @SuppressLint("ClickableViewAccessibility")
//        fun bind(product: Product) {
//            binding.productLBLTitle.text = product.name
//            binding.productTXTOpenedDate.text = product.openingDate ?: "N/A"
//            binding.productTXTExpiredDate.text = product.calculateExpiryDate() ?: "N/A"
//
//            // Load product image
//            ImageLoader.getInstance().loadImage(product.picture, binding.productIMGPicture)
//
//            binding.productLYSkinType.visibility = View.GONE
//            binding.productTXTIngredients.visibility = View.GONE
//
//            binding.productBTNDrag.visibility = if (onStartDrag != null) View.VISIBLE else View.GONE
//
//            binding.productBTNDrag.setOnTouchListener { v, event ->
//                if (event.action == MotionEvent.ACTION_DOWN) {
//                    onStartDrag?.invoke(this)
//                    v.performClick() // Ensure accessibility compatibility
//                }
//                false
//            }
////            binding.productBTNDrag.setOnClickListener {
////                it.performClick() // Ensure accessibility support
////            }
//
//            binding.productBTNDelete.visibility =
//                if (onRemoveProduct != null) View.VISIBLE else View.GONE
//
//            binding.productBTNDelete.setOnClickListener {
//                onRemoveProduct?.invoke(product)
//            }
//
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
//        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ProductViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
//        holder.bind(productList[position])
//    }
//
//    override fun getItemCount(): Int = productList.size
//
//    override fun onItemMove(fromPosition: Int, toPosition: Int) {
//        Collections.swap(productList, fromPosition, toPosition)
//        notifyItemMoved(fromPosition, toPosition)
//        orderChangeListener?.onRoutineOrderChanged("Morning", productList)
//
//    }
//
//    fun updateList(newProducts: List<Product>) {
//        productList = newProducts
//        notifyDataSetChanged()
//    }
//
//    fun removeProduct(product: Product) {
//        val position = productList.indexOf(product)
//        if (position != -1) {
//            productList = productList.toMutableList().apply { removeAt(position) }
//            notifyItemRemoved(position)
//        }
//    }
//
//    fun getProducts(): List<Product> = productList
//}
