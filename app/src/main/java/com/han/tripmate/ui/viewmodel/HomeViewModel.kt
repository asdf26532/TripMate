package com.han.tripmate.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.han.tripmate.data.model.Plan
import com.han.tripmate.ui.util.NotificationHelper
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

    fun checkTodayTrip(context: Context) {
        val currentPlan = _upcomingPlan.value
        val currentDDay = _dDay.value

        if (currentPlan != null && currentDDay == 0L) {
            NotificationHelper(context).showTripNotification(
                "오늘 여행을 떠나요! ✈️",
                "${currentPlan.title} 일정이 오늘 시작됩니다. 짐은 다 챙기셨나요?"
            )
        }
    }

}