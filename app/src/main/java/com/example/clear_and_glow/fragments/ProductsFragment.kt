package com.example.clear_and_glow.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clear_and_glow.adapters.ProductCategoryAdapter
import com.example.clear_and_glow.adapters.ProductsAdapter
import com.example.clear_and_glow.databinding.FragmentProductsBinding
import com.example.clear_and_glow.interfaces.ProductCategoryListCallback
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.models.ProductCategory
import com.example.clear_and_glow.utilities.AuthManager
import com.example.clear_and_glow.utilities.FirestoreManager
import com.example.clear_and_glow.viewmodels.ProductsViewModel

class ProductsFragment : Fragment() {

    private val productsViewModel: ProductsViewModel by viewModels()
    private lateinit var binding: FragmentProductsBinding
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var authManager: AuthManager
    private val firestoreManager = FirestoreManager.getInstance()

    private var categoriesAdapter = ProductCategoryAdapter(emptyList()) { selectedCategory ->
        filterProductsByCategory(selectedCategory, isGlobalView)
    }
    private var selectedCategory: ProductCategory? = null

    private var isGlobalView = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager.getInstance(requireContext())

        setupRecyclerView()
        initObservers()
        setupClickListeners()
        loadCategories()
        setupSearchListener()
        productsViewModel.loadUserProducts(authManager.getCurrentUserUid() ?: "")
    }

    private fun initObservers() {
        productsViewModel.globalProducts.observe(viewLifecycleOwner) { products ->
            if (isGlobalView) {
                productsAdapter.updateList(products)
                updateUi(products)
            }
        }

        productsViewModel.userProducts.observe(viewLifecycleOwner) { products ->
            if (!isGlobalView) {
                productsAdapter.updateList(products)
                updateUi(products)
            }
        }
    }

    private fun setupRecyclerView() {
        productsAdapter = ProductsAdapter(
            emptyList(),
            onProductClick = { product ->
                if (isGlobalView) {
                    productsAdapter.toggleSelection(product)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Clicked on: ${product.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            isGlobal = isGlobalView
        )

        binding.productRVCategories.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriesAdapter
        }

        binding.productRVList.layoutManager = LinearLayoutManager(requireContext())
        binding.productRVList.adapter = productsAdapter
    }

    private fun setupClickListeners() {
        binding.productListIMGBack.setOnClickListener {
            isGlobalView = false
            changeView()
        }

        binding.productListIMGAdd.setOnClickListener {
            isGlobalView = true
            changeView()
        }

        binding.productListIMGSubmit.setOnClickListener {
            addSelectedProductsToUser()
        }
    }

    private fun changeView() {
        if (isGlobalView) {
            binding.productListTXTTitle.text = "All Products"
            binding.productListIMGAdd.visibility = View.INVISIBLE
            binding.productListIMGSubmit.visibility = View.VISIBLE
            binding.productListIMGBack.visibility = View.VISIBLE
            productsAdapter = ProductsAdapter(
                emptyList(),
                onProductClick = { product -> productsAdapter.toggleSelection(product) },
                isGlobal = true
            )
            productsViewModel.loadGlobalProducts()

        } else {
            binding.productListTXTTitle.text = "My Products"
            binding.productListIMGAdd.visibility = View.VISIBLE
            binding.productListIMGSubmit.visibility = View.INVISIBLE
            binding.productListIMGBack.visibility = View.INVISIBLE
            productsAdapter = ProductsAdapter(
                emptyList(),
                onProductClick = { product ->
                    Toast.makeText(
                        requireContext(),
                        "Clicked on: ${product.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                isGlobal = false
            )
            productsViewModel.loadUserProducts(authManager.getCurrentUserUid() ?: "")
        }

        binding.productRVList.adapter = productsAdapter
        productsAdapter.updateList(
            if (isGlobalView) productsViewModel.globalProducts.value ?: emptyList()
            else productsViewModel.userProducts.value ?: emptyList()
        )

        updateUi(
            if (isGlobalView) productsViewModel.globalProducts.value ?: emptyList()
            else productsViewModel.userProducts.value ?: emptyList()
        )
    }

    private fun addSelectedProductsToUser() {
        val userId = authManager.getCurrentUserUid() ?: return
        val selectedList =
            productsAdapter.getSelectedProducts()

        if (selectedList.isEmpty()) {
            Toast.makeText(requireContext(), "No products selected!", Toast.LENGTH_SHORT).show()
            return
        }

        productsViewModel.addProductsToUser(userId, selectedList) { success, error ->
            if (success) {
                Toast.makeText(requireContext(), "Products added successfully!", Toast.LENGTH_SHORT)
                    .show()
//                productsAdapter.clearSelection()
                isGlobalView = false
                changeView()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to add products: $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadCategories() {
        firestoreManager.getAllCategories(object : ProductCategoryListCallback {
            override fun onSuccess(categories: List<ProductCategory>) {
                categoriesAdapter.updateList(categories)
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUi(products: List<Product>) {
        binding.productListLBLEmpty.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
        binding.productRVList.visibility = if (products.isEmpty()) View.GONE else View.VISIBLE
    }


    private fun filterProductsByCategory(category: ProductCategory, isGlobalView: Boolean) {
        selectedCategory = if (selectedCategory == category) null else category
        productsViewModel.filterProductsByCategory(selectedCategory, isGlobalView)
        categoriesAdapter.updateSelectedCategory(selectedCategory)
    }


    private fun setupSearchListener() {
        binding.productETSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                productsViewModel.searchProducts(s.toString(), isGlobalView)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
