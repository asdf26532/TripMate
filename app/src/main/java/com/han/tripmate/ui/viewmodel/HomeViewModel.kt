package com.han.tripmate.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.han.tripmate.data.PlanRepository
import com.han.tripmate.data.model.Plan
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val planRepository = PlanRepository()

    private val _upcomingPlan = MutableStateFlow()<Plan?>(null)
    val upcomingPlan = _upcomingPlan.asStateFlow()

    fun loadHomeData(plans: List<Plan>) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            _upcomingPlan.value = plans
                .filter { it.startDate >= now }
                .minByOrNull { it.startDate }
        }
    }
}