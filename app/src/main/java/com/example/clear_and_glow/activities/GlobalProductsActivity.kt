package com.example.clear_and_glow.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clear_and_glow.R
import com.example.clear_and_glow.adapters.ProductCategoryAdapter
import com.example.clear_and_glow.adapters.ProductsAdapter
import com.example.clear_and_glow.databinding.ActivityGlobalProductsBinding
import com.example.clear_and_glow.fragments.MyProductsFragment
import com.example.clear_and_glow.interfaces.FirestoreCallback
import com.example.clear_and_glow.interfaces.ProductCategoryListCallback
import com.example.clear_and_glow.interfaces.ProductListCallback
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.models.ProductCategory
import com.example.clear_and_glow.utilities.AuthManager
import com.example.clear_and_glow.utilities.FirestoreManager

class GlobalProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGlobalProductsBinding
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var categoriesAdapter: ProductCategoryAdapter
    private val firestoreManager = FirestoreManager.getInstance()
    private var allProducts: List<Product> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlobalProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadGlobalProducts()
        loadCategories()
        initListeners()
    }

    private fun loadGlobalProducts() {
        firestoreManager.getAllGlobalProducts(object : ProductListCallback {
            override fun onSuccess(products: List<Product>) {
                allProducts = products
                productsAdapter.updateList(products)
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@GlobalProductsActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadCategories() {
        firestoreManager.getAllCategories(object : ProductCategoryListCallback {
            override fun onSuccess(categories: List<ProductCategory>) {
                categoriesAdapter = ProductCategoryAdapter(categories) { selectedCategory ->
                    filterProductsByCategory(selectedCategory)
                }
                binding.recyclerViewCategories.adapter = categoriesAdapter
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@GlobalProductsActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initListeners() {
        binding.globalProductBTNCheck.setOnClickListener {
            addGlobalProductsToUser()
        }
    }

    private fun filterProductsByCategory(category: ProductCategory) {
        if (category.name == "All") {
            productsAdapter.updateList(allProducts)
        } else {
            val filteredProducts = allProducts.filter { it.category == category.name } // 🛠 תיקון
            productsAdapter.updateList(filteredProducts)
        }
    }

    private fun setupRecyclerView() {
        productsAdapter = ProductsAdapter(emptyList(), {}, true)


        binding.globalProductRVProducts.apply {
            layoutManager = LinearLayoutManager(this@GlobalProductsActivity)
            adapter = productsAdapter
        }

        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(
                this@GlobalProductsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

//    private fun getCategoriesList(): List<ProductCategory> {
//        return listOf(
//            ProductCategory("All", R.drawable.ic_products),
//            ProductCategory("Cleanser", R.drawable.unavailable_photo),
//            ProductCategory("SPF", R.drawable.unavailable_photo),
//            ProductCategory("Serum", R.drawable.unavailable_photo),
//            ProductCategory("Moisturizer", R.drawable.unavailable_photo),
//            ProductCategory("Eye Cream", R.drawable.unavailable_photo)
//        )
//    }

    private fun addGlobalProductsToUser() {
        val userId = AuthManager.getInstance(this).getCurrentUserUid() ?: return
        val selectedProducts = productsAdapter.getSelectedProducts()

        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "Select at least one product", Toast.LENGTH_SHORT).show()
            return
        }

        firestoreManager.addProductsToUser(userId, selectedProducts, object :
            FirestoreCallback {
            override fun onSuccess() {
                Toast.makeText(this@GlobalProductsActivity, "Products added successfully!", Toast.LENGTH_SHORT).show()
                openMyProductsFragment()
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@GlobalProductsActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openMyProductsFragment() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("navigateTo", "myProductsFragment")
        startActivity(intent)
        finish()
    }
}
