import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clear_and_glow.interfaces.FirestoreCallback
import com.example.clear_and_glow.interfaces.RoutinesCallback
import com.example.clear_and_glow.models.Product
import com.example.clear_and_glow.models.Routine
import com.example.clear_and_glow.utilities.FirestoreManager

class RoutineViewModel : ViewModel() {
    private val firestoreManager = FirestoreManager.getInstance()

    private val _userRoutines = MutableLiveData<List<Routine>>()
    val userRoutines: LiveData<List<Routine>> get() = _userRoutines

    fun loadUserRoutines(userId: String) {
        firestoreManager.getUserRoutines(userId, object : RoutinesCallback {
            override fun onSuccess(routines: List<Routine>) {
                _userRoutines.value = routines
            }

            override fun onFailure(errorMessage: String) {
                _userRoutines.value = emptyList()
            }
        })
    }


    fun addProductToRoutine(userId: String, product: Product, timeOfDay: String) {
        val currentRoutines = _userRoutines.value ?: emptyList()
        val currentRoutine = currentRoutines.find { it.timeOfDay == timeOfDay }
        val updatedRoutine = currentRoutine?.copy(
            products = (currentRoutine.products ?: emptyList()) + product
        ) ?: Routine(timeOfDay = timeOfDay, products = listOf(product))

        val updatedRoutines = if (currentRoutine != null) {
            currentRoutines.map { if (it.timeOfDay == timeOfDay) updatedRoutine else it }
        } else {
            currentRoutines + updatedRoutine
        }
        _userRoutines.value = updatedRoutines
        updateRoutine(userId, updatedRoutine)
    }

//    fun addProductToRoutine(userId: String, product: Product, timeOfDay: String) {
//        val currentRoutineList = _userRoutines.value?.toMutableList() ?: mutableListOf()
//
//        if (currentRoutineList.isEmpty()) {
//            currentRoutineList.add(Routine(timeOfDay = "Morning", products = emptyList()))
//            currentRoutineList.add(Routine(timeOfDay = "Evening", products = emptyList()))
//        }
//
//        val routineIndex = currentRoutineList.indexOfFirst { it.timeOfDay == timeOfDay }
//        if (routineIndex != -1) {
//            val existingRoutine = currentRoutineList[routineIndex]
//            val updatedRoutine = existingRoutine.copy(
//                products = (existingRoutine.products ?: emptyList()) + product
//            )
//            currentRoutineList[routineIndex] = updatedRoutine
//        }
//        _userRoutines.value = currentRoutineList
//    }

//
//    fun addProductToRoutine(userId: String, product: Product, timeOfDay: String) {
//        val currentRoutine = _userRoutines.value?.find { it.timeOfDay == timeOfDay }
//        val updatedRoutine = currentRoutine?.copy(
//            products = (currentRoutine.products ?: emptyList()) + product
//        ) ?: Routine(timeOfDay = timeOfDay, products = listOf(product))
//
//        _userRoutines.value
//    }

    fun updateRoutine(userId: String, routine: Routine) {
        firestoreManager.saveRoutine(userId, routine, object : FirestoreCallback {
            override fun onSuccess() {
                val updatedList = _userRoutines.value?.map {
                    if (it.id == routine.id) routine else it
                } ?: listOf(routine)
                _userRoutines.value = updatedList
            }
            override fun onFailure(errorMessage: String) {
                Log.e("Firestore", "Failed to update routine")
            }
        })
    }

    fun removeProductFromRoutine(userId: String, product: Product, timeOfDay: String) {
        val currentRoutine = _userRoutines.value?.find { it.timeOfDay == timeOfDay } ?: return
        val updatedRoutine = currentRoutine.copy(
            products = currentRoutine.products?.filterNot { it.id == product.id } ?: emptyList()
        )
        updateRoutine(userId, updatedRoutine)
    }
}
