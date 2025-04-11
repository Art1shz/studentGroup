package com.example.studentgroup.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
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
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme

enum class LessonStatus {
    NOT_IN_SCHEDULE,
    UPCOMING,
    NEXT,
    CURRENT,
    COMPLETED
}

@Composable
fun AttendanceCard(
    daySchedule: DaySchedule?,
    modifier: Modifier = Modifier
) {
    val currentTime = remember { System.currentTimeMillis() }
    val isDarkTheme = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme
    
    val entryTime = remember(daySchedule) {
        daySchedule?.lessons?.values?.minByOrNull { it.startTime }?.startTime ?: "08:05"
    }
    val exitTime = remember(daySchedule) {
        daySchedule?.lessons?.values?.maxByOrNull { it.endTime }?.endTime ?: "13:55"
    }
    
    LaunchedEffect(daySchedule) {
        Log.d("AttendanceCard", "Received schedule: $daySchedule")
        daySchedule?.lessons?.forEach { (number, lesson) ->
            Log.d("AttendanceCard", "Lesson $number: $lesson")
        }
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "УРОКИ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp),
                color = colorScheme.onSurface
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = entryTime,
                        fontSize = 14.sp,
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "ВХОД",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    for (lessonNumber in 1..9) {
                        val status = getLessonStatus(lessonNumber, daySchedule, currentTime)
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = when (status) {
                                        LessonStatus.NOT_IN_SCHEDULE -> colorScheme.error
                                        LessonStatus.UPCOMING -> colorScheme.surfaceVariant
                                        LessonStatus.NEXT -> Color(0xFFFFC107) // Material Yellow
                                        LessonStatus.CURRENT -> Color(0xFF2196F3) // Material Blue
                                        LessonStatus.COMPLETED -> colorScheme.primary
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lessonNumber.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = exitTime,
                        fontSize = 14.sp,
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "ВЫХОД",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun getLessonStatus(lessonNumber: Int, daySchedule: DaySchedule?, currentTime: Long): LessonStatus {
    if (daySchedule == null) {
        Log.d("AttendanceCard", "Schedule is null for lesson $lessonNumber")
        return LessonStatus.NOT_IN_SCHEDULE
    }
    
    Log.d("AttendanceCard", "Looking for lesson $lessonNumber in schedule")
    Log.d("AttendanceCard", "Available lessons: ${daySchedule.lessons.keys}")
    
    val lesson = daySchedule.lessons[lessonNumber.toString()]
    if (lesson == null) {
        Log.d("AttendanceCard", "No lesson found for number $lessonNumber")
        return LessonStatus.NOT_IN_SCHEDULE
    }
    
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val currentTimeStr = timeFormat.format(calendar.time)
    
    Log.d("AttendanceCard", "Found lesson $lessonNumber: $lesson")
    Log.d("AttendanceCard", "Checking time - Current: $currentTimeStr, Lesson: ${lesson.startTime}-${lesson.endTime}")
    
    val nextLesson = findNextLesson(daySchedule.lessons, currentTimeStr)
    Log.d("AttendanceCard", "Next lesson: $nextLesson")
    
    return when {
        isLessonCurrent(lesson, currentTimeStr) -> {
            Log.d("AttendanceCard", "Lesson $lessonNumber is CURRENT")
            LessonStatus.CURRENT
        }
        
        nextLesson?.number == lessonNumber -> {
            Log.d("AttendanceCard", "Lesson $lessonNumber is NEXT")
            LessonStatus.NEXT
        }
        
        isLessonCompleted(lesson, currentTimeStr) -> {
            Log.d("AttendanceCard", "Lesson $lessonNumber is COMPLETED")
            LessonStatus.COMPLETED
        }
        
        else -> {
            Log.d("AttendanceCard", "Lesson $lessonNumber is UPCOMING")
            LessonStatus.UPCOMING
        }
    }
}

private fun isLessonCurrent(lesson: Lesson, currentTime: String): Boolean {
    return currentTime >= lesson.startTime && currentTime <= lesson.endTime
}

private fun findNextLesson(lessons: Map<String, Lesson>, currentTime: String): Lesson? {
    return lessons.values
        .filter { !isLessonCompleted(it, currentTime) && !isLessonCurrent(it, currentTime) }
        .minByOrNull { it.startTime }
}

private fun isLessonCompleted(lesson: Lesson, currentTime: String): Boolean {
    return currentTime > lesson.endTime
} 