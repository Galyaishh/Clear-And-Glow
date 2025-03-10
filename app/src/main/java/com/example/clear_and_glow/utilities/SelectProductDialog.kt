package com.example.clear_and_glow.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clear_and_glow.adapters.ProductsAdapter
import com.example.clear_and_glow.databinding.DialogSelectProductBinding
import com.example.clear_and_glow.models.Product

class SelectProductDialog(
    context: Context,
    private val productList: List<Product>,
    private val onProductSelected: (Product) -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogSelectProductBinding
    private lateinit var adapter: ProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSelectProductBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = ProductsAdapter(productList, { selectedProduct ->
            onProductSelected(selectedProduct)
            dismiss()
        },false)

        Log.d("SelectProductDialog", "Product list size: ${productList.size}")



        binding.dialogProductRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@SelectProductDialog.adapter
        }

    }

    private fun setupListeners() {
        binding.dialogProductBTNCancel.setOnClickListener { dismiss() }
    }
}
