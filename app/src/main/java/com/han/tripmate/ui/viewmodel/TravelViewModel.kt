package com.han.tripmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.han.tripmate.data.TravelService
import com.han.tripmate.data.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TravelViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val repository = TravelRepository()
    private var serviceListener: ListenerRegistration? = null

    private val _services = MutableStateFlow<List<TravelService>>(emptyList())
    val services: StateFlow<List<TravelService>> = _services.asStateFlow()

    private val _favoriteIds = MutableStateFlow(setOf<String>())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    init {
        fetchServices()

        serviceListener = repository.observeServices { list ->
            _services.value = list
        }
    }

    private fun fetchServices() {
        db.collection("travel_services")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(TravelService::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                _services.value = list
            }
    }

    fun addService(service: TravelService, onComplete: (Boolean) -> Unit) {
        repository.addService(service, onComplete)
    }

    override fun onCleared() {
        super.onCleared()
        serviceListener?.remove()
    }

    fun toggleFavorite(serviceId: String) {
        val current = _favoriteIds.value
        _favoriteIds.value = if (current.contains(serviceId)) current - serviceId else current + serviceId
    }
}