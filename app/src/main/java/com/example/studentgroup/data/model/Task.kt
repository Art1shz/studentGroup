package com.example.studentgroup.data.model

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val subject: String = "",
    val teacherUid: String = "",
    val teacherFullName: String = "",
    val deadline: Long = 0,
    val status: String = "active",
    val createdAt: Long = System.currentTimeMillis()
) 