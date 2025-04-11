package com.example.studentgroup.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentgroup.data.model.DaySchedule
import com.example.studentgroup.data.model.User
import com.example.studentgroup.data.repository.ScheduleRepository
import com.example.studentgroup.data.auth.FirebaseAuthRepository
import com.example.studentgroup.ui.components.AttendanceCard
import com.example.studentgroup.ui.components.UserAvatar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(
    userData: User?,
    onAttendanceClick: () -> Unit,
    onScheduleClick: () -> Unit,
    onTasksClick: () -> Unit,
    onGroupListClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val currentDate = remember {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = when (calendar.get(Calendar.MONTH)) {
            Calendar.JANUARY -> "января"
            Calendar.FEBRUARY -> "февраля"
            Calendar.MARCH -> "марта"
            Calendar.APRIL -> "апреля"
            Calendar.MAY -> "мая"
            Calendar.JUNE -> "июня"
            Calendar.JULY -> "июля"
            Calendar.AUGUST -> "августа"
            Calendar.SEPTEMBER -> "сентября"
            Calendar.OCTOBER -> "октября"
            Calendar.NOVEMBER -> "ноября"
            Calendar.DECEMBER -> "декабря"
            else -> ""
        }
        "$day $month"
    }
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val isWeekend = dayOfWeek in listOf(Calendar.SATURDAY, Calendar.SUNDAY)
    
    var daySchedule by remember { mutableStateOf<DaySchedule?>(null) }
    val scheduleRepository = remember { ScheduleRepository() }
    val authRepository = remember { FirebaseAuthRepository() }
    val scope = rememberCoroutineScope()

    val colorScheme = MaterialTheme.colorScheme
    val cardColor = colorScheme.surface
    val textColor = colorScheme.onSurface
    val grayTextColor = colorScheme.onSurface.copy(alpha = 0.6f)
    val backgroundColor = colorScheme.background

    LaunchedEffect(Unit) {
        authRepository.getCurrentUser()?.let { firebaseUser ->
            try {
                val user = authRepository.getUserData(firebaseUser.uid)
                if (!isWeekend) {
                    val currentDaySchedule = scheduleRepository.getDaySchedule(getCurrentDayOfWeek())
                    daySchedule = currentDaySchedule
                }
                Log.d("MainScreen", "Loaded user data: $user")
                Log.d("MainScreen", "Loaded schedule: $daySchedule")
            } catch (e: Exception) {
                Log.e("MainScreen", "Error loading user data", e)
            }
        }
    }

    val currentLesson = remember(daySchedule, currentTime) {
        if (!isWeekend) {
            daySchedule?.lessons?.values?.find { lesson ->
                currentTime >= lesson.startTime && currentTime <= lesson.endTime
            }
        } else null
    }

    val nextLesson = remember(daySchedule, currentTime) {
        if (!isWeekend) {
            daySchedule?.lessons?.values
                ?.filter { lesson -> lesson.startTime > currentTime }
                ?.minByOrNull { it.startTime }
        } else null
    }

    val remainingTime = remember(currentLesson, currentTime) {
        if (!isWeekend) {
            currentLesson?.let {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val endTime = timeFormat.parse(it.endTime) ?: return@let null
                val current = timeFormat.parse(currentTime) ?: return@let null
                val diff = endTime.time - current.time
                val minutes = diff / (1000 * 60)
                if (minutes > 0) "$minutes минут" else null
            }
        } else null
    }

    val timeUntilNext = remember(nextLesson, currentTime) {
        if (!isWeekend) {
            nextLesson?.let {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val nextTime = timeFormat.parse(it.startTime) ?: return@let null
                val current = timeFormat.parse(currentTime) ?: return@let null
                val diff = nextTime.time - current.time
                val minutes = diff / (1000 * 60)
                if (minutes > 0) "$minutes минут" else null
            }
        } else null
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Привет,",
                        fontSize = 16.sp,
                        color = grayTextColor
                    )
                    Text(
                        text = userData?.let { "${it.firstName} ${it.lastName}" } ?: "",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                }
                
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable(onClick = onProfileClick),
                    shape = CircleShape,
                    color = Color(0xFF8B82FF)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = userData?.let { "${it.firstName[0]}${it.lastName[0]}" } ?: "",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Сегодня, $currentDate",
                    fontSize = 16.sp,
                    color = grayTextColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!isWeekend) {
                AttendanceCard(daySchedule = daySchedule)
                
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!isSystemInDarkTheme()) Color(0xFFE3F2FD) else Color(0xFF1E1E1E)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (currentLesson != null) {
                            Text(
                                "ТЕКУЩИЙ УРОК", 
                                fontSize = 14.sp, 
                                color = if (!isSystemInDarkTheme()) Color.Gray else Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentLesson.subject,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isSystemInDarkTheme()) Color.Black else Color.White
                            )
                            Text(
                                text = "${currentLesson.number} урок ${currentLesson.startTime}-${currentLesson.endTime} к${currentLesson.cabinet}",
                                fontSize = 16.sp,
                                color = if (!isSystemInDarkTheme()) Color.Gray else Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            remainingTime?.let {
                                Text(
                                    text = "Осталось $it",
                                    fontSize = 16.sp,
                                    color = Color(0xFF2196F3)
                                )
                            }
                        } else if (nextLesson != null) {
                            Text(
                                "СЛЕДУЮЩИЙ УРОК", 
                                fontSize = 14.sp, 
                                color = if (!isSystemInDarkTheme()) Color.Gray else Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = nextLesson.subject,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isSystemInDarkTheme()) Color.Black else Color.White
                            )
                            Text(
                                text = "${nextLesson.number} урок ${nextLesson.startTime}-${nextLesson.endTime} к${nextLesson.cabinet}",
                                fontSize = 16.sp,
                                color = if (!isSystemInDarkTheme()) Color.Gray else Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            timeUntilNext?.let {
                                Text(
                                    text = "Начнется через $it",
                                    fontSize = 16.sp,
                                    color = Color(0xFF2196F3)
                                )
                            }
                        } else {
                            Text(
                                "РАСПИСАНИЕ", 
                                fontSize = 14.sp, 
                                color = if (!isSystemInDarkTheme()) Color.Gray else Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Уроки закончились",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isSystemInDarkTheme()) Color.Black else Color.White
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Сегодня выходной",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF8B82FF)
                        )
                        Text(
                            text = "Занятия не проводятся",
                            fontSize = 16.sp,
                            color = grayTextColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onScheduleClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    text = "Расписание",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onTasksClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    text = "Задания",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onGroupListClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.surface
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Text(
                    text = "Список группы",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun AttendanceCard(
    daySchedule: DaySchedule?
) {
}

@Composable
fun CurrentLessonCard(currentTime: Calendar) {
    if (currentTime.get(Calendar.DAY_OF_WEEK) !in listOf(Calendar.SATURDAY, Calendar.SUNDAY)) {
    }
}

private fun getCurrentDayOfWeek(): String {
    return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "monday"
        Calendar.TUESDAY -> "tuesday"
        Calendar.WEDNESDAY -> "wednesday"
        Calendar.THURSDAY -> "thursday"
        Calendar.FRIDAY -> "friday"
        else -> "monday"
    }
}