package com.han.tripmate.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel

import com.han.tripmate.data.model.PackingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TravelPackingViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("packing_prefs", Context.MODE_PRIVATE)

    private val _packingList = MutableStateFlow<List<PackingItem>>(listOf())
    val packingList: StateFlow<List<PackingItem>> = _packingList.asStateFlow()

    val defaultItems = listOf(
        PackingItem("1", "여권 및 비자", "필수품"),
        PackingItem("2", "보조배터리 및 케이블", "전자기기"),
        PackingItem("3", "변환 플러그 (돼지코)", "전자기기")
    )

    init {
        loadPackingList()
    }

    private fun loadPackingList() {
        val savedData = sharedPreferences.getString("saved_items", null)
        if (savedData.isNullOrBlank()) {
            _packingList.value = defaultItems
        } else {

            try {
                val items = savedData.split(";").filter { it.isNotBlank() }.map {
                    val tokens = it.split("|")
                    PackingItem(
                        id = tokens[0],
                        name = tokens[1],
                        category = tokens[2],
                        isPacked = tokens[3].toBoolean()
                    )
                }
                _packingList.value = items
            } catch (e: Exception) {
                _packingList.value = defaultItems
            }
        }
    }

    private fun savePackingList(list: List<PackingItem>) {
        _packingList.value = list
        val serialized = list.joinToString(";") { "${it.id}|${it.name}|${it.category}|${it.isPacked}" }
        sharedPreferences.edit().putString("saved_items", serialized).apply()
    }

    fun toggleItemPacked(id: String) {
        val updated = _packingList.value.map {
            if (it.id == id) it.copy(isPacked = !it.isPacked) else it
        }
        savePackingList(updated)
    }

    fun addItem(name: String, category: String) {
        val newItem = PackingItem(
            id = System.currentTimeMillis().toString(),
            name = name.trim(),
            category = category
        )
        val updated = _packingList.value + newItem
        savePackingList(updated)
    }

    fun deleteItem(id: String) {
        val updated = _packingList.value.filter { it.id != id }
        savePackingList(updated)
    }

    fun loadTemplate(themeItems: List<PackingItem>) {
        val updated = defaultItems + themeItems
        savePackingList(updated)
    }
}