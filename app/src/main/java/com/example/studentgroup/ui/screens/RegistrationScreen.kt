package com.example.studentgroup.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentgroup.R
import com.example.studentgroup.data.auth.FirebaseAuthRepository
import com.example.studentgroup.data.model.User
import kotlinx.coroutines.launch

private val robotoRegular = FontFamily(Font(R.font.roboto_regular))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onRegistrationSuccess: (User) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var registrationCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    val authRepository = remember { FirebaseAuthRepository() }
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Text(
            text = "Регистрация",
            fontSize = 24.sp,
            fontFamily = robotoRegular,
            color = colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 38.dp)
                .align(Alignment.Center)
        ) {
            Text(
                text = "Код регистрации",
                fontSize = 16.sp,
                fontFamily = robotoRegular,
                color = colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = registrationCode,
                onValueChange = { registrationCode = it.uppercase() },
                placeholder = {
                    Text(
                        "XXXXXX",
                        color = colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        fontFamily = robotoRegular
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = robotoRegular,
                    color = colorScheme.onBackground
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = colorScheme.surface,
                    focusedContainerColor = colorScheme.surface,
                    unfocusedBorderColor = colorScheme.outline,
                    focusedBorderColor = Color(0xFF31BAFF),
                    cursorColor = Color(0xFF31BAFF),
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface
                ),
                shape = RoundedCornerShape(30.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Email",
                fontSize = 16.sp,
                fontFamily = robotoRegular,
                color = colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        "xxxxxx@example.com",
                        color = colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        fontFamily = robotoRegular
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = robotoRegular,
                    color = colorScheme.onBackground
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = colorScheme.surface,
                    focusedContainerColor = colorScheme.surface,
                    unfocusedBorderColor = colorScheme.outline,
                    focusedBorderColor = Color(0xFF31BAFF),
                    cursorColor = Color(0xFF31BAFF),
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface
                ),
                shape = RoundedCornerShape(30.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Пароль",
                fontSize = 16.sp,
                fontFamily = robotoRegular,
                color = colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        "********",
                        color = colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        fontFamily = robotoRegular
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = robotoRegular,
                    color = colorScheme.onBackground
                ),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = colorScheme.surface,
                    focusedContainerColor = colorScheme.surface,
                    unfocusedBorderColor = colorScheme.outline,
                    focusedBorderColor = Color(0xFF31BAFF),
                    cursorColor = Color(0xFF31BAFF),
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface
                ),
                shape = RoundedCornerShape(30.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Подтвердите пароль",
                fontSize = 16.sp,
                fontFamily = robotoRegular,
                color = colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = {
                    Text(
                        "********",
                        color = colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        fontFamily = robotoRegular
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = robotoRegular,
                    color = colorScheme.onBackground
                ),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = colorScheme.surface,
                    focusedContainerColor = colorScheme.surface,
                    unfocusedBorderColor = colorScheme.outline,
                    focusedBorderColor = Color(0xFF31BAFF),
                    cursorColor = Color(0xFF31BAFF),
                    focusedTextColor = colorScheme.onSurface,
                    unfocusedTextColor = colorScheme.onSurface
                ),
                shape = RoundedCornerShape(30.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null

                        if (registrationCode.isEmpty()) {
                            errorMessage = "Введите код регистрации"
                            isLoading = false
                            return@launch
                        }

                        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                            errorMessage = "Введите корректный email"
                            isLoading = false
                            return@launch
                        }

                        if (password.length < 6) {
                            errorMessage = "Пароль должен содержать минимум 6 символов"
                            isLoading = false
                            return@launch
                        }

                        if (password != confirmPassword) {
                            errorMessage = "Пароли не совпадают"
                            isLoading = false
                            return@launch
                        }

                        try {
                            val result = authRepository.registerUser(email, password, registrationCode)
                            result.onSuccess { firebaseUser ->
                                Log.d("Registration", "Success: ${firebaseUser.email}")
                                // Get the user data after successful registration
                                scope.launch {
                                    try {
                                        val userData = authRepository.getUserData(firebaseUser.uid)
                                        userData?.let {
                                            onRegistrationSuccess(it)
                                        } ?: run {
                                            errorMessage = "Ошибка получения данных пользователя"
                                        }
                                    } catch (e: Exception) {
                                        Log.e("Registration", "Error getting user data: ${e.message}")
                                        errorMessage = "Ошибка получения данных пользователя"
                                    }
                                }
                            }.onFailure { e ->
                                Log.e("Registration", "Error: ${e.message}")
                                errorMessage = e.message ?: "Ошибка регистрации"
                            }
                        } catch (e: Exception) {
                            Log.e("Registration", "Exception: ${e.message}")
                            errorMessage = e.message ?: "Неизвестная ошибка"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7259FF)
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                Text(
                    "Зарегистрироваться",
                    fontSize = 16.sp,
                    fontFamily = robotoRegular,
                    color = Color.White
                )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 51.dp)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Уже есть аккаунт? ",
                    color = colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontFamily = robotoRegular
                )
                TextButton(
                    onClick = onBackToLogin,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "Войти",
                        color = Color(0xFF31BAFF),
                        fontSize = 14.sp,
                        fontFamily = robotoRegular
                    )
                }
            }
        }
    }
}