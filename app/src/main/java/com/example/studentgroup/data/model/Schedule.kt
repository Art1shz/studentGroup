package com.example.studentgroup.data.model

data class Lesson(
    val number: Int = 0,
    val subject: String = "",
    val cabinet: String = "",
    val startTime: String = "",
    val endTime: String = ""
)

data class DaySchedule(
    val lessons: Map<String, Lesson> = emptyMap()
)

data class WeekSchedule(
    val days: Map<String, DaySchedule> = emptyMap()
) 