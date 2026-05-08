package com.han.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.han.tripmate.ui.viewmodel.PlanViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import com.han.tripmate.ui.util.ItineraryInputForm
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planTitle: String,
    planId: String,
    itineraryList: List<Itinerary>,
    planViewModel: PlanViewModel
) {
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(planTitle, fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                containerColor = MainBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "일정 추가")
            }
        }

    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }

                itemsIndexed(
                    items = itineraryList,
                    key = { _, itinerary -> itinerary.id }
                ) { index, itinerary ->

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                planViewModel.deleteItinerary(planId, itinerary.id)
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                Color.Red.copy(alpha = 0.15f)
                            } else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 8.dp)
                                    .background(color, shape = RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    "삭제",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 20.dp)
                                )
                            }
                        }
                    ) {
                        Column(modifier = Modifier.background(Color.White)) {
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
                                val current = itineraryList[index]
                                val next = itineraryList[index + 1]

                                TravelInfoIndicator(
                                    duration = planViewModel.estimateTravelTime(current, next),
                                    distance = planViewModel.getDistanceString(current, next)
                                )
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false },
                    sheetState = sheetState,
                    containerColor = Color.White,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    ItineraryInputForm(
                        onSave = { title, time, memo ->
                            planViewModel.addItinerary(planId, title, time, memo)
                            showSheet = false
                        }
                    )
                }
            }
        }
    }
}
