package com.example.studentgroup.ui.screens

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

private val robotoRegular = FontFamily(Font(R.font.roboto_regular))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Text(
            text = "Авторизация",
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onForgotPasswordClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Забыли пароль?",
                        color = Color(0xFF31BAFF),
                        fontSize = 14.sp,
                        fontFamily = robotoRegular
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    isLoading = true
                    onLoginClick(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7259FF),
                    disabledContainerColor = Color(0xFF7259FF).copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(30.dp),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Войти",
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
                    "Еще не зарегистрированы? ",
                    color = colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontFamily = robotoRegular
                )
                TextButton(
                    onClick = onRegisterClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        "Зарегистрироваться",
                        color = Color(0xFF31BAFF),
                        fontSize = 14.sp,
                        fontFamily = robotoRegular
                    )
                }
            }
        }
    }
}