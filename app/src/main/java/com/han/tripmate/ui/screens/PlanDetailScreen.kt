package com.han.tripmate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.han.tripmate.data.model.Itinerary
import com.han.tripmate.ui.util.TimelineIndicator
import com.han.tripmate.ui.theme.MainBlue
import com.han.tripmate.ui.util.TravelInfoIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planTitle: String,
    itineraryList: List<Itinerary>
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(planTitle, fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            itemsIndexed(itineraryList) { index, itinerary ->

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {

                        TimelineIndicator(
                            isFirst = index == 0,
                            isLast = index == itineraryList.size - 1
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = itinerary.time,
                                    fontSize = 12.sp,
                                    color = MainBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = itinerary.title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (itinerary.memo.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = itinerary.memo,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    if (index < itineraryList.size - 1) {
                        TravelInfoIndicator(
                            duration = "20분",
                            distance = "2.5km"
                        )
                    }
                }
            }
        }
    }
}
