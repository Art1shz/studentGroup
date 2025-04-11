package com.example.studentgroup.data.repository

import android.util.Log
import com.example.studentgroup.data.model.DaySchedule
import com.example.studentgroup.data.model.Lesson
import com.example.studentgroup.data.model.WeekSchedule
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ScheduleRepository {
    private val database = FirebaseDatabase.getInstance()
    private val scheduleRef = database.getReference("schedule")

    suspend fun initializeWeekSchedule() {
        val mondayLessons = mapOf(
            "2" to Lesson(
                number = 2,
                subject = "МДК 02.01",
                cabinet = "145",
                startTime = "09:00",
                endTime = "09:45"
            ),
            "3" to Lesson(
                number = 3,
                subject = "Ин.яз",
                cabinet = "422",
                startTime = "09:55",
                endTime = "10:40"
            ),
            "4" to Lesson(
                number = 4,
                subject = "МДК 01.01",
                cabinet = "149",
                startTime = "11:00",
                endTime = "11:45"
            ),
            "5" to Lesson(
                number = 5,
                subject = "Экология",
                cabinet = "333",
                startTime = "12:05",
                endTime = "12:50"
            ),
            "6" to Lesson(
                number = 6,
                subject = "Курсовая работа",
                cabinet = "149",
                startTime = "13:10",
                endTime = "13:55"
            ),
            "7" to Lesson(
                number = 7,
                subject = "МДК 04.02",
                cabinet = "145",
                startTime = "14:05",
                endTime = "14:50"
            ),
            "8" to Lesson(
                number = 8,
                subject = "Право",
                cabinet = "315",
                startTime = "15:00",
                endTime = "15:45"
            )
        )

        val tuesdayLessons = mapOf(
            "1" to Lesson(
                number = 1,
                subject = "УП ПМ 01",
                cabinet = "149",
                startTime = "08:05",
                endTime = "08:50"
            ),
            "2" to Lesson(
                number = 2,
                subject = "УП ПМ 01",
                cabinet = "149",
                startTime = "09:00",
                endTime = "09:45"
            ),
            "3" to Lesson(
                number = 3,
                subject = "УП ПМ 01",
                cabinet = "149",
                startTime = "09:55",
                endTime = "10:40"
            ),
            "4" to Lesson(
                number = 4,
                subject = "УП ПМ 01",
                cabinet = "149",
                startTime = "11:00",
                endTime = "11:45"
            ),
            "5" to Lesson(
                number = 5,
                subject = "УП ПМ 01",
                cabinet = "149",
                startTime = "12:05",
                endTime = "12:50"
            ),
            "6" to Lesson(
                number = 6,
                subject = "УП ПМ 01",
                cabinet = "149",
                startTime = "13:10",
                endTime = "13:55"
            )
        )

        val wednesdayLessons = mapOf(
            "4" to Lesson(
                number = 4,
                subject = "Стандартизация",
                cabinet = "415",
                startTime = "11:00",
                endTime = "11:45"
            ),
            "5" to Lesson(
                number = 5,
                subject = "Менеджмент",
                cabinet = "345",
                startTime = "12:05",
                endTime = "12:50"
            ),
            "6" to Lesson(
                number = 6,
                subject = "МДК 02.02",
                cabinet = "333",
                startTime = "13:10",
                endTime = "13:55"
            ),
            "7" to Lesson(
                number = 7,
                subject = "МДК 01.03",
                cabinet = "149",
                startTime = "14:05",
                endTime = "14:50"
            ),
            "8" to Lesson(
                number = 8,
                subject = "МДК 01.04",
                cabinet = "147",
                startTime = "15:00",
                endTime = "15:45"
            ),
            "9" to Lesson(
                number = 9,
                subject = "МДК 01.04",
                cabinet = "147",
                startTime = "15:55",
                endTime = "16:40"
            )
        )

        val thursdayLessons = mapOf(
            "1" to Lesson(
                number = 1,
                subject = "МДК 02.02",
                cabinet = "149",
                startTime = "08:05",
                endTime = "08:50"
            ),
            "2" to Lesson(
                number = 2,
                subject = "МДК 04.01",
                cabinet = "145",
                startTime = "09:00",
                endTime = "09:45"
            ),
            "3" to Lesson(
                number = 3,
                subject = "МДК 01.01",
                cabinet = "149",
                startTime = "09:55",
                endTime = "10:40"
            ),
            "4" to Lesson(
                number = 4,
                subject = "Физическая культура",
                cabinet = "",
                startTime = "11:00",
                endTime = "11:45"
            ),
            "5" to Lesson(
                number = 5,
                subject = "Физическая культура",
                cabinet = "",
                startTime = "12:05",
                endTime = "12:50"
            ),
            "6" to Lesson(
                number = 6,
                subject = "Основы алгоритмизации",
                cabinet = "147",
                startTime = "13:10",
                endTime = "13:55"
            ),
            "7" to Lesson(
                number = 7,
                subject = "Основы алгоритмизации",
                cabinet = "147",
                startTime = "14:05",
                endTime = "14:50"
            ),
            "8" to Lesson(
                number = 8,
                subject = "МДК 02.01",
                cabinet = "145",
                startTime = "15:00",
                endTime = "15:45"
            )
        )

        val fridayLessons = mapOf(
            "1" to Lesson(
                number = 1,
                subject = "МДК 02.02",
                cabinet = "149",
                startTime = "08:05",
                endTime = "08:50"
            ),
            "2" to Lesson(
                number = 2,
                subject = "Комп. сети",
                cabinet = "149",
                startTime = "09:00",
                endTime = "09:45"
            ),
            "3" to Lesson(
                number = 3,
                subject = "МДК 01.02",
                cabinet = "149",
                startTime = "09:55",
                endTime = "10:40"
            ),
            "4" to Lesson(
                number = 4,
                subject = "МДК 01.01",
                cabinet = "149",
                startTime = "11:00",
                endTime = "11:45"
            ),
            "5" to Lesson(
                number = 5,
                subject = "Ин.яз",
                cabinet = "422",
                startTime = "12:05",
                endTime = "12:50"
            ),
            "6" to Lesson(
                number = 6,
                subject = "Комп. сети",
                cabinet = "149",
                startTime = "13:10",
                endTime = "13:55"
            ),
            "7" to Lesson(
                number = 7,
                subject = "МДК 01.02",
                cabinet = "149",
                startTime = "14:05",
                endTime = "14:50"
            ),
            "8" to Lesson(
                number = 8,
                subject = "МДК 01.03",
                cabinet = "149",
                startTime = "15:00",
                endTime = "15:45"
            )
        )

        val weekSchedule = mapOf(
            "monday" to DaySchedule(lessons = mondayLessons),
            "tuesday" to DaySchedule(lessons = tuesdayLessons),
            "wednesday" to DaySchedule(lessons = wednesdayLessons),
            "thursday" to DaySchedule(lessons = thursdayLessons),
            "friday" to DaySchedule(lessons = fridayLessons)
        )

        scheduleRef.setValue(weekSchedule).await()
    }

    suspend fun getDaySchedule(day: String): DaySchedule = suspendCancellableCoroutine { continuation ->
        Log.d("ScheduleRepository", "Loading schedule for day: $day")
        
        scheduleRef.child(day).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("ScheduleRepository", "Raw data from Firebase: ${snapshot.value}")
                Log.d("ScheduleRepository", "Data structure: ${snapshot.children.forEach { 
                    Log.d("ScheduleRepository", "Key: ${it.key}, Value: ${it.value}")
                }}")
                
                try {
                    val lessons = mutableMapOf<String, Lesson>()
                    
                    val lessonsSnapshot = if (snapshot.hasChild("lessons")) {
                        Log.d("ScheduleRepository", "Found lessons child node")
                        snapshot.child("lessons")
                    } else {
                        Log.d("ScheduleRepository", "No lessons child node, using snapshot directly")
                        snapshot
                    }
                    
                    lessonsSnapshot.children.forEach { lessonSnapshot ->
                        Log.d("ScheduleRepository", "Processing lesson with key: ${lessonSnapshot.key}")
                        val number = lessonSnapshot.child("number").getValue(Int::class.java)
                        val startTime = lessonSnapshot.child("startTime").getValue(String::class.java)
                        val endTime = lessonSnapshot.child("endTime").getValue(String::class.java)
                        val subject = lessonSnapshot.child("subject").getValue(String::class.java)
                        val cabinet = lessonSnapshot.child("cabinet").getValue(String::class.java)

                        Log.d("ScheduleRepository", "Parsed values - number: $number, startTime: $startTime, endTime: $endTime, subject: $subject, cabinet: $cabinet")

                        if (number != null && startTime != null && endTime != null && 
                            subject != null && cabinet != null) {
                            lessons[number.toString()] = Lesson(number, subject, cabinet, startTime, endTime)
                            Log.d("ScheduleRepository", "Successfully added lesson $number: ${lessons[number.toString()]}")
                        } else {
                            Log.w("ScheduleRepository", "Incomplete lesson data for number: $number")
                            Log.w("ScheduleRepository", "Raw lesson data: ${lessonSnapshot.value}")
                        }
                    }

                    if (lessons.isEmpty()) {
                        Log.w("ScheduleRepository", "No lessons found in the schedule for $day")
                    } else {
                        Log.d("ScheduleRepository", "Total lessons loaded: ${lessons.size}")
                        Log.d("ScheduleRepository", "Lessons map: $lessons")
                    }

                    continuation.resume(DaySchedule(lessons))
                } catch (e: Exception) {
                    Log.e("ScheduleRepository", "Error parsing schedule data", e)
                    continuation.resumeWithException(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ScheduleRepository", "Firebase error: ${error.message}", error.toException())
                continuation.resumeWithException(error.toException())
            }
        })
    }

    suspend fun getWeekSchedule(): Map<String, DaySchedule>? {
        return try {
            val snapshot = scheduleRef.get().await()
            val type = object : GenericTypeIndicator<Map<String, DaySchedule>>() {}
            snapshot.getValue(type)
        } catch (e: Exception) {
            null
        }
    }
} 