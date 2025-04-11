package com.example.studentgroup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentgroup.data.model.DaySchedule
import com.example.studentgroup.data.model.Lesson
import com.example.studentgroup.data.repository.ScheduleRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onBackClick: () -> Unit
) {
    val scheduleRepository = remember { ScheduleRepository() }
    var selectedDay by remember { mutableStateOf("monday") }
    var daySchedule by remember { mutableStateOf<DaySchedule?>(null) }
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(selectedDay) {
        scope.launch {
            daySchedule = scheduleRepository.getDaySchedule(selectedDay)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Расписание",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.surface,
                    navigationIconContentColor = colorScheme.onSurface,
                    titleContentColor = colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF8B82FF))
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DayTab("ПН", "monday", selectedDay) { selectedDay = it }
                DayTab("ВТ", "tuesday", selectedDay) { selectedDay = it }
                DayTab("СР", "wednesday", selectedDay) { selectedDay = it }
                DayTab("ЧТ", "thursday", selectedDay) { selectedDay = it }
                DayTab("ПТ", "friday", selectedDay) { selectedDay = it }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sortedLessons = daySchedule?.lessons?.entries?.sortedBy { it.key }?.map { it.value } ?: emptyList()
                items(sortedLessons) { lesson ->
                    LessonCard(lesson = lesson)
                }
            }
        }
    }
}

@Composable
fun DayTab(
    text: String,
    day: String,
    selectedDay: String,
    onDaySelected: (String) -> Unit
) {
    Text(
        text = text,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable { onDaySelected(day) },
        color = if (selectedDay == day) Color.White else Color.White.copy(alpha = 0.7f),
        fontSize = 16.sp,
        fontWeight = if (selectedDay == day) FontWeight.Bold else FontWeight.Normal
    )
}

@Composable
fun LessonCard(lesson: Lesson) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8B82FF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "${lesson.number} урок",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lesson.subject,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${lesson.startTime}-${lesson.endTime}",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "к${lesson.cabinet}",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
} 