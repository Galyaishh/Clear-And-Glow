package com.example.clear_and_glow.models

data class SharedRoutine(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val profileImageRes: String? = null,
    val title: String = "",
    var likeCount: Int = 0,
    var likedBy: MutableList<String> = mutableListOf(),
    val routine: Routine = Routine(),
    val timestamp: Long = System.currentTimeMillis(),

    ) {
    fun isLikedByUser(userId: String): Boolean {
        return likedBy.contains(userId)
    }

    fun toggleLike(userId: String): SharedRoutine {
        val updatedLikedBy = likedBy.toMutableList()
        if (updatedLikedBy.contains(userId))
            updatedLikedBy.remove(userId)
        else
            updatedLikedBy.add(userId)
        return this.copy(
            likedBy = updatedLikedBy,
            likeCount = updatedLikedBy.size
        )
    }
}
