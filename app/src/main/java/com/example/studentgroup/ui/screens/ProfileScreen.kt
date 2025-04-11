package com.example.studentgroup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentgroup.data.model.User
import com.example.studentgroup.data.auth.FirebaseAuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userData: User?,
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    onUserDataUpdate: () -> Unit
) {
    var showEmailDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val authRepository = remember { FirebaseAuthRepository() }
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Профиль",
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
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
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userData?.let { "${it.firstName} ${it.lastName}" } ?: "",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Email",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = userData?.email ?: "",
                            fontSize = 16.sp,
                            color = colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { showEmailDialog = true }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Изменить email",
                            tint = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Пароль",
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "••••••••",
                            fontSize = 16.sp,
                            color = colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { showPasswordDialog = true }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Изменить пароль",
                            tint = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = colorScheme.surface
                ),
                elevation = CardDefaults.outlinedCardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Тёмная тема",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onSurface
                    )
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onThemeChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorScheme.primary,
                            checkedTrackColor = colorScheme.primaryContainer,
                            uncheckedThumbColor = colorScheme.outline,
                            uncheckedTrackColor = colorScheme.surfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7259FF)
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    "Выйти",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            successMessage?.let { success ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = success,
                    color = Color.Green,
                    fontSize = 14.sp
                )
            }
        }

        if (showEmailDialog) {
            var newEmail by remember { mutableStateOf("") }
            var currentPassword by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showEmailDialog = false },
                title = { Text("Изменить email") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newEmail,
                            onValueChange = { newEmail = it },
                            label = { Text("Новый email") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Текущий пароль") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newEmail.isNotBlank() && currentPassword.isNotBlank()) {
                                scope.launch {
                                    try {
                                        authRepository.updateEmail(newEmail, currentPassword)
                                            .onSuccess {
                                                successMessage = "Email успешно обновлен"
                                                errorMessage = null
                                                showEmailDialog = false
                                                onUserDataUpdate()
                                            }
                                            .onFailure { e ->
                                                errorMessage = e.message
                                                successMessage = null
                                            }
                                    } catch (e: Exception) {
                                        errorMessage = "Ошибка: ${e.message}"
                                        successMessage = null
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Сохранить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEmailDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }

        if (showPasswordDialog) {
            var currentPassword by remember { mutableStateOf("") }
            var newPassword by remember { mutableStateOf("") }
            var confirmNewPassword by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                title = { Text("Изменить пароль") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Текущий пароль") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("Новый пароль") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                        OutlinedTextField(
                            value = confirmNewPassword,
                            onValueChange = { confirmNewPassword = it },
                            label = { Text("Подтвердите новый пароль") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newPassword == confirmNewPassword && newPassword.isNotBlank() && currentPassword.isNotBlank()) {
                                scope.launch {
                                    try {
                                        authRepository.updatePassword(currentPassword, newPassword)
                                            .onSuccess {
                                                successMessage = "Пароль успешно обновлен"
                                                errorMessage = null
                                                showPasswordDialog = false
                                            }
                                            .onFailure { e ->
                                                errorMessage = e.message
                                                successMessage = null
                                            }
                                    } catch (e: Exception) {
                                        errorMessage = "Ошибка: ${e.message}"
                                        successMessage = null
                                    }
                                }
                            } else {
                                errorMessage = "Пароли не совпадают"
                                successMessage = null
                            }
                        }
                    ) {
                        Text("Сохранить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPasswordDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
} 