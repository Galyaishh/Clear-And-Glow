package com.example.clear_and_glow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clear_and_glow.adapters.RoutineAdapter
import com.example.clear_and_glow.databinding.FragmentMyRoutinesBinding
import com.example.clear_and_glow.interfaces.RoutinesCallback
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.models.Routine
import com.example.clear_and_glow.utilities.AuthManager
import com.example.clear_and_glow.utilities.FirestoreManager

class MyRoutinesFragment : Fragment() {
    private lateinit var binding: FragmentMyRoutinesBinding
    private lateinit var morningAdapter: RoutineAdapter
    private lateinit var eveningAdapter: RoutineAdapter
    private val firestoreManager = FirestoreManager.getInstance()
    private lateinit var authManager: AuthManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyRoutinesBinding.inflate(inflater, container, false)
        authManager = AuthManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        loadRoutineProducts()
    }

    private fun setupRecyclerViews() {
        // Morning Routine Products
        morningAdapter = RoutineAdapter(emptyList())
        binding.routinesRVMorning.layoutManager = LinearLayoutManager(requireContext())
        binding.routinesRVMorning.adapter = morningAdapter

        // Evening Routine Products
        eveningAdapter = RoutineAdapter(emptyList())
        binding.routinesRVEvening.layoutManager = LinearLayoutManager(requireContext())
        binding.routinesRVEvening.adapter = eveningAdapter
    }

    private fun loadRoutineProducts() {
        val userId = authManager.getCurrentUserUid() ?: return

        firestoreManager.getUserRoutines(userId, object : RoutinesCallback {
            override fun onSuccess(routines: List<Routine>) {
                val morningProducts = routines.filter {
                    it.timeOfDay.equals("Morning", ignoreCase = true)
                }.flatMap { it.products ?: emptyList() }

                val eveningProducts = routines.filter {
                    it.timeOfDay.equals("Evening", ignoreCase = true)
                }.flatMap { it.products ?: emptyList() }

                morningAdapter.updateList(morningProducts)
                eveningAdapter.updateList(eveningProducts)

                updateUi(binding.morningListLBLEmpty, binding.routinesRVMorning, morningProducts)
                updateUi(binding.eveningListLBLEmpty, binding.routinesRVEvening, eveningProducts)
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(
                    requireContext(),
                    "Failed to load routine products: $errorMessage",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateUi(emptyLabel: View, recyclerView: View, products: List<Product>) {
        emptyLabel.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (products.isEmpty()) View.GONE else View.VISIBLE
    }
}
