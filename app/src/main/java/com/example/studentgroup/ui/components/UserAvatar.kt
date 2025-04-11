package com.example.studentgroup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserAvatar(
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier
) {
    val initials = buildString {
        if (firstName.isNotEmpty()) append(firstName[0])
        if (lastName.isNotEmpty()) append(lastName[0])
    }.uppercase()

    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color(0xFF7259FF)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
} 