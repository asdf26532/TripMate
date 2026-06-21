package com.han.tripmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.han.tripmate.data.TravelService
import com.han.tripmate.data.TravelRepository
import com.han.tripmate.data.FilterConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class TravelViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val repository = TravelRepository()
    private var serviceListener: ListenerRegistration? = null

    private val _services = MutableStateFlow<List<TravelService>>(emptyList())
    val services: StateFlow<List<TravelService>> = _services.asStateFlow()

    private val _favoriteIds = MutableStateFlow(setOf<String>())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _selectedRegion = MutableStateFlow(FilterConstants.REGIONS[0])
    val selectedRegion: StateFlow<String> = _selectedRegion.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FilterConstants.CATEGORIES[0])
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val filteredServices: StateFlow<List<TravelService>> = combine(
        _services,
        _selectedRegion,
        _selectedCategory
    ) { services, region, category ->
        services.filter { service ->
            val matchesRegion = region == FilterConstants.REGIONS[0] || service.location == region

            val matchesCategory = category == FilterConstants.CATEGORIES[0] || service.category == category

            matchesRegion && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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

    fun selectRegion(region: String) {
        _selectedRegion.value = region
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addService(service: TravelService, onComplete: (Boolean) -> Unit) {
        repository.addService(service, onComplete)
    }

    fun deleteService(serviceId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                db.collection("travel_services")
                    .document(serviceId)
                    .delete()
                    .addOnSuccessListener {
                        _services.value = _services.value.filter { it.id != serviceId }
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateService(
        serviceId: String,
        updatedTitle: String,
        updatedDescription: String,
        updatedPrice: Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val updateData = mapOf(
                    "title" to updatedTitle,
                    "description" to updatedDescription,
                    "price" to updatedPrice
                )

                db.collection("travel_services")
                    .document(serviceId)
                    .update(updateData)
                    .addOnSuccessListener {
                        _services.value = _services.value.map {
                            if (it.id == serviceId) {
                                it.copy(title = updatedTitle, description = updatedDescription, price = updatedPrice)
                            } else it
                        }
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        serviceListener?.remove()
    }

    fun toggleFavorite(serviceId: String) {
        val current = _favoriteIds.value
        _favoriteIds.value = if (current.contains(serviceId)) current - serviceId else current + serviceId
    }

    fun startChatting(
        guideAuthorId: String,
        guideAuthorNickname: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            onFailure(Exception("로그인이 필요한 서비스입니다."))
            return
        }

        if (currentUserId == guideAuthorId) {
            onFailure(Exception("본인과는 채팅할 수 없습니다."))
            return
        }

        val sortedIds = listOf(currentUserId, guideAuthorId).sorted()
        val roomId = "room_${sortedIds[0]}_${sortedIds[1]}"

        val chatRoomRef = db.collection("chat_rooms").document(roomId)

        chatRoomRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                onSuccess(roomId)
            } else {
                val timestampStr = System.currentTimeMillis().toString()
                val chatRoomData = mapOf(
                    "id" to roomId,
                    "otherUserId" to guideAuthorId,
                    "otherNickname" to guideAuthorNickname,
                    "lastMessage" to "채팅방이 개설되었습니다.",
                    "lastTimestamp" to timestampStr,
                    "participants" to listOf(currentUserId, guideAuthorId)
                )

                chatRoomRef.set(chatRoomData)
                    .addOnSuccessListener { onSuccess(roomId) }
                    .addOnFailureListener { onFailure(it) }
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

}