package com.han.tripmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.han.tripmate.data.model.Plan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {
    private val _upcomingPlan = MutableStateFlow<Plan?>(null)
    val upcomingPlan = _upcomingPlan.asStateFlow()

    private val _dDay = MutableStateFlow<Long?>(null)
    val dDay = _dDay.asStateFlow()

    fun calculateUpcomingPlan(plans: List<Plan>) {
        val now = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)


        val futurePlan = plans.filter {
            try {
                val planDate = sdf.parse(it.date)
                planDate?.after(now) ?: false || it.date == sdf.format(now)
            } catch (e: Exception) { false }
        }.minByOrNull { it.date }

        _upcomingPlan.value = futurePlan

        futurePlan?.let {
            val targetDate = sdf.parse(it.date)
            val diff = targetDate!!.time - now.time
            _dDay.value = diff / (24 * 60 * 60 * 1000)
        }
    }
}