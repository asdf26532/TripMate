package com.han.tripmate.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.han.tripmate.data.model.Plan

class PlanViewModel : ViewModel() {
    // 임시 데이터
    private val _plans = mutableStateListOf(
        Plan(title = "부산 도착", date = "2024-03-24", time = "10:00", location = "부산역"),
        Plan(title = "점심 식사", date = "2024-03-24", time = "12:30", location = "본전돼지국밥")
    )
    val plans: List<Plan> = _plans

    fun addPlan(plan: Plan) {
        _plans.add(plan)
    }

    fun removePlan(plan: Plan) {
        _plans.remove(plan)
    }
}