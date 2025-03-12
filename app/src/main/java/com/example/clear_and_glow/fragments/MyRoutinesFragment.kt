package com.example.clear_and_glow.fragments

import RoutineViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clear_and_glow.adapters.RoutineAdapter
import com.example.clear_and_glow.databinding.FragmentMyRoutinesBinding
import com.example.clear_and_glow.dialogs.SelectProductDialog
import com.example.clear_and_glow.interfaces.FirestoreCallback
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.utilities.AuthManager
import com.example.clear_and_glow.viewModels.FeedViewModel
import com.example.clear_and_glow.viewmodels.ProductsViewModel

class MyRoutinesFragment : Fragment() {

    private val routineViewModel: RoutineViewModel by viewModels()
    private val productsViewModel: ProductsViewModel by viewModels()
    private val feedViewModel: FeedViewModel by viewModels()
    private lateinit var selectProductDialog: SelectProductDialog
    private lateinit var binding: FragmentMyRoutinesBinding
    private lateinit var morningAdapter: RoutineAdapter
    private lateinit var eveningAdapter: RoutineAdapter
    private lateinit var authManager: AuthManager

    private var isEditModeMorning = false
    private var isEditModeEvening = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyRoutinesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager.getInstance(requireContext())
        val userId = authManager.getCurrentUserUid() ?: return

        productsViewModel.loadUserProducts(userId)

        setupRecyclerViews()
        initObservers()
        setupClickListeners()

        routineViewModel.loadUserRoutines(userId)
    }


    private fun initObservers() {
        routineViewModel.userRoutines.observe(viewLifecycleOwner) { routines ->
            val morningRoutine = routines.find { it.timeOfDay == "Morning" }
            val eveningRoutine = routines.find { it.timeOfDay == "Evening" }

            morningAdapter.updateList(morningRoutine?.products ?: emptyList())
            eveningAdapter.updateList(eveningRoutine?.products ?: emptyList())

            updateUi(
                binding.morningListLBLEmpty,
                binding.routinesRVMorning,
                morningRoutine?.products
            )
            updateUi(
                binding.eveningListLBLEmpty,
                binding.routinesRVEvening,
                eveningRoutine?.products
            )
        }
    }

    private fun setupRecyclerViews() {

        morningAdapter = RoutineAdapter(mutableListOf(), isEditModeMorning) { product ->
            if (isEditModeMorning) handleRemoveProductClick(product, "Morning")
        }

        eveningAdapter = RoutineAdapter(mutableListOf(), isEditModeEvening) { product ->
            if (isEditModeEvening) handleRemoveProductClick(product, "Evening")
        }

        binding.routinesRVMorning.layoutManager = LinearLayoutManager(requireContext())
        binding.routinesRVMorning.adapter = morningAdapter

        binding.routinesRVEvening.layoutManager = LinearLayoutManager(requireContext())
        binding.routinesRVEvening.adapter = eveningAdapter
    }

    private fun setupClickListeners() {


        binding.routinesBTNEditMorning.setOnClickListener {
            toggleEditModeUi(!isEditModeMorning, "Morning")
        }
        binding.routinesBTNPlusMorning.setOnClickListener {
            openProductSelectionDialog("Morning")
        }

        binding.routinesBTNCheckMorning.setOnClickListener {
            saveChanges()
            toggleEditModeUi(!isEditModeMorning, "Morning")

        }

        binding.routinesBTNShareMorning.setOnClickListener {
            shareRoutine("Morning")
        }

        binding.routinesBTNEditEvening.setOnClickListener {
            toggleEditModeUi(!isEditModeEvening, "Evening")
        }

        binding.routinesBTNPlusEvening.setOnClickListener {
            openProductSelectionDialog("Evening")
        }

        binding.routinesBTNCheckEvening.setOnClickListener {
            saveChanges()
            toggleEditModeUi(!isEditModeEvening, "Evening")
        }

        binding.routinesBTNShareEvening.setOnClickListener {
            shareRoutine("Evening")
        }
    }

    fun shareRoutine(timeOfDay: String) {
        val userId = authManager.getCurrentUserUid() ?: return
        val routine =
            routineViewModel.userRoutines.value?.find { it.timeOfDay == timeOfDay } ?: return
        feedViewModel.shareRoutine(userId, timeOfDay, routine, object : FirestoreCallback {
            override fun onSuccess() {
                Toast.makeText(requireContext(), "Routine shared successfully", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(
                    requireContext(),
                    "Failed to share routine: $errorMessage",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


//    fun openProductSelectionDialog(timeOfDay: String) {
//        val userId = authManager.getCurrentUserUid() ?: return
//        val userProducts = productsViewModel.userProducts.value ?: emptyList()
//
//        if (userProducts.isEmpty()) {
//            Toast.makeText(requireContext(), "No products available", Toast.LENGTH_SHORT).show()
//            return
//        }
//        selectProductDialog =
//            SelectProductDialog(requireContext(), userProducts) { selectedProduct ->
//                addProductToRoutine(selectedProduct, timeOfDay)
//                updateProductDates(userId, selectedProduct)
//            }
//        selectProductDialog.show()
//    }

    fun openProductSelectionDialog(timeOfDay: String) {
        val userId = authManager.getCurrentUserUid() ?: return
        val userProducts = productsViewModel.userProducts.value ?: emptyList()

        if (userProducts.isEmpty()) {
            Toast.makeText(requireContext(), "No products available", Toast.LENGTH_SHORT).show()
            return
        }

        selectProductDialog =
            SelectProductDialog(requireContext(), userProducts) { selectedProduct ->
                productsViewModel.updateProductDates(userId, selectedProduct)
                addProductToRoutine(selectedProduct, timeOfDay)
                routineViewModel.loadUserRoutines(userId)

            }
        selectProductDialog.show()
    }


    private fun addProductToRoutine(product: Product, timeOfDay: String) {
        val userId = authManager.getCurrentUserUid() ?: return
        routineViewModel.addProductToRoutine(userId, product, timeOfDay)
    }

    private fun updateProductDates(userId: String, selectedProduct: Product) {
        productsViewModel.updateProductDates(userId, selectedProduct)
    }


    private fun toggleEditModeUi(isEditMode: Boolean, timeOfDay: String) {
        when (timeOfDay) {
            ("Morning") -> {
                isEditModeMorning = isEditMode
                morningAdapter.setEditMode(isEditMode)
                binding.routinesBTNEditMorning.visibility =
                    if (isEditMode) View.GONE else View.VISIBLE
                binding.routinesBTNPlusMorning.visibility =
                    if (isEditMode) View.VISIBLE else View.GONE
                binding.routinesBTNCheckMorning.visibility =
                    if (isEditMode) View.VISIBLE else View.GONE
            }

            ("Evening") -> {
                isEditModeEvening = isEditMode
                eveningAdapter.setEditMode(isEditMode)
                binding.routinesBTNEditEvening.visibility =
                    if (isEditMode) View.GONE else View.VISIBLE
                binding.routinesBTNPlusEvening.visibility =
                    if (isEditMode) View.VISIBLE else View.GONE
                binding.routinesBTNCheckEvening.visibility =
                    if (isEditMode) View.VISIBLE else View.GONE
            }
        }
    }

    private fun saveChanges() {
        val userId = authManager.getCurrentUserUid() ?: return
        val currentRoutines = routineViewModel.userRoutines.value ?: return
        val morningRoutine = currentRoutines.find { it.timeOfDay == "Morning" }
        val eveningRoutine = currentRoutines.find { it.timeOfDay == "Evening" }

        if (morningAdapter.getProducts() != morningRoutine?.products) {
            val updatedMorningRoutine =
                morningRoutine?.copy(products = morningAdapter.getProducts())
            updatedMorningRoutine?.let { routineViewModel.updateRoutine(userId, it) }
        }
        if (eveningAdapter.getProducts() != eveningRoutine?.products) {
            val updatedEveningRoutine =
                eveningRoutine?.copy(products = eveningAdapter.getProducts())
            updatedEveningRoutine?.let { routineViewModel.updateRoutine(userId, it) }
        }
    }


    private fun handleRemoveProductClick(product: Product, timeOfDay: String) {
        val userId = authManager.getCurrentUserUid() ?: return
        routineViewModel.removeProductFromRoutine(userId, product, timeOfDay)
    }

    private fun updateUi(emptyLabel: View, recyclerView: View, products: List<Product>?) {
        emptyLabel.visibility = if (products.isNullOrEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (products.isNullOrEmpty()) View.GONE else View.VISIBLE
    }
}
