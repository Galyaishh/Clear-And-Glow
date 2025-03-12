package com.example.clear_and_glow.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clear_and_glow.R
import com.example.clear_and_glow.databinding.SharedRoutineItemBinding
import com.example.clear_and_glow.models.SharedRoutine
import com.example.clear_and_glow.utilities.ImageLoader

class SharedRoutineAdapter(
    private var sharedRoutines: List<SharedRoutine>,
    private val currentUserUid: String,
    private val onLikeClick: (SharedRoutine, Int) -> Unit
) : RecyclerView.Adapter<SharedRoutineAdapter.SharedRoutineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedRoutineViewHolder {
        val binding =
            SharedRoutineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SharedRoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SharedRoutineViewHolder, position: Int) {
        holder.bind(sharedRoutines[position])
    }

    override fun onBindViewHolder(
        holder: SharedRoutineViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.contains("LIKE_UPDATE")) {
            holder.updateLikeUI(sharedRoutines[position])
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }


    fun setSharedRoutines(newSharedRoutines: List<SharedRoutine>) {
        sharedRoutines = newSharedRoutines
        notifyDataSetChanged()
    }


    fun updateLikeStatus(position: Int, sharedRoutine: SharedRoutine) {
        val updatedList = sharedRoutines.toMutableList()
        updatedList[position] = sharedRoutine
        sharedRoutines = updatedList
        notifyItemChanged(position, "LIKE_UPDATE") // Updates only likes
    }

    override fun getItemCount(): Int = sharedRoutines.size

    inner class SharedRoutineViewHolder(private val binding: SharedRoutineItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sharedRoutine: SharedRoutine) {
            sharedRoutine.profileImageRes?.let {
                ImageLoader.getInstance().loadImage(it, binding.feedIMGProfile)
            }

            binding.feedTXTName.text = sharedRoutine.userName
            binding.feedTXTRoutineTitle.text = sharedRoutine.title

            val routineAdapter = FeedProductsAdapter(sharedRoutine.routine.products)
            binding.feedRVProducts.adapter = routineAdapter
            binding.feedRVProducts.layoutManager = LinearLayoutManager(itemView.context)

            updateLikeUI(sharedRoutine)

            binding.feedBTNLike.setOnClickListener {
                onLikeClick(sharedRoutine, adapterPosition)
            }
        }

        fun updateLikeUI(sharedRoutine: SharedRoutine) {
            binding.feedTXTLike.text = "${sharedRoutine.likeCount}"
            val likeIcon =
                if (sharedRoutine.isLikedByUser(
                        currentUserUid
                    )
                ) R.drawable.ic_heart else R.drawable.ic_heart_border
            binding.feedBTNLike.setImageResource(likeIcon)
        }
    }
}
