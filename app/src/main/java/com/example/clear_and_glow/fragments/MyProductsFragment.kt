package com.example.clear_and_glow.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clear_and_glow.activities.GlobalProductsActivity
import com.example.clear_and_glow.adapters.ProductsAdapter
import com.example.clear_and_glow.databinding.FragmentMyProductsBinding
import com.example.clear_and_glow.interfaces.ProductListCallback
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.utilities.AuthManager
import com.example.clear_and_glow.utilities.FirestoreManager

class MyProductsFragment : Fragment() {

    private lateinit var binding: FragmentMyProductsBinding
    private lateinit var productsAdapter: ProductsAdapter
    private val firestoreManager = FirestoreManager.getInstance()
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProductsBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())

        productsAdapter = ProductsAdapter(emptyList(), { product ->
            Toast.makeText(requireContext(), "Clicked on: ${product.name}", Toast.LENGTH_SHORT).show()
        }, false)

        initViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadUserProducts()
    }

    private fun setupRecyclerView() {
        binding.productRVList.layoutManager = LinearLayoutManager(requireContext())
        binding.productRVList.adapter = productsAdapter
    }

    private fun initViews(){
        binding.productListIMGBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.productListIMGAdd.setOnClickListener {
            var intent = Intent(requireContext(), GlobalProductsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserProducts() {
        val userId = authManager.getCurrentUserUid() ?: return

        firestoreManager.getUserProducts(userId, object : ProductListCallback {
            override fun onSuccess(products: List<Product>) {
                productsAdapter.updateList(products)
                updateUi(products)
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(requireContext(), "Failed to load products: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUi(products: List<Product>) {
        if (products.isEmpty()) {
            binding.productListLBLEmpty.visibility = View.VISIBLE
            binding.productRVList.visibility = View.GONE
        } else {
            binding.productListLBLEmpty.visibility = View.GONE
            binding.productRVList.visibility = View.VISIBLE
        }
    }
}
