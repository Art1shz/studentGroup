package com.example.studentgroup

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studentgroup.data.auth.FirebaseAuthRepository
import com.example.studentgroup.data.model.User
import com.example.studentgroup.data.repository.ScheduleRepository
import com.example.studentgroup.data.repository.RegistrationCodeRepository
import com.example.studentgroup.data.repository.GroupRepository
import com.example.studentgroup.data.preferences.ThemePreference
import com.example.studentgroup.ui.navigation.Screen
import com.example.studentgroup.ui.screens.*
import com.example.studentgroup.ui.theme.StudentGroupTheme
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.runtime.*

class MainActivity : ComponentActivity() {
    private val authRepository = FirebaseAuthRepository()
    private val scheduleRepository = ScheduleRepository()
    private val registrationCodeRepository = RegistrationCodeRepository()
    private val groupRepository = GroupRepository()
    private lateinit var themePreference: ThemePreference

    private fun isValidEmail(email: String): Boolean {
        val isValid = email.isNotEmpty() && email.contains("@") && email.contains(".")
        Log.d("EmailValidation", "Email: $email, isValid: $isValid")
        return isValid
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themePreference = ThemePreference(this)

        // Инициализация данных
        lifecycleScope.launch {
            try {
                // Инициализация кодов регистрации
                Log.d("MainActivity", "Checking registration codes...")
                registrationCodeRepository.initializeRegistrationCodes()
                val codes = registrationCodeRepository.getAllCodes()
                Log.d("MainActivity", "Registration codes available: ${codes.size}")
                
                // Инициализация расписания
                Log.d("MainActivity", "Initializing schedule...")
                scheduleRepository.initializeWeekSchedule()
                
                // Инициализация списка группы
                Log.d("MainActivity", "Initializing group list...")
                groupRepository.initializeGroupList()

                Toast.makeText(
                    this@MainActivity,
                    "Данные успешно загружены",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error initializing data", e)
                Toast.makeText(
                    this@MainActivity,
                    "Ошибка при загрузке данных: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        setContent {
            val isDarkTheme = remember { mutableStateOf(themePreference.isDarkTheme) }
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
            var userData by remember { mutableStateOf<User?>(null) }
            val authRepository = remember { FirebaseAuthRepository() }

            LaunchedEffect(Unit) {
                authRepository.getCurrentUser()?.let { user ->
                    try {
                        userData = authRepository.getUserData(user.uid)
                        currentScreen = Screen.Main
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error fetching user data", e)
                    }
                }
            }

            StudentGroupTheme(darkTheme = isDarkTheme.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        is Screen.Login -> {
                            LoginScreen(
                                onLoginClick = { email, password ->
                                    lifecycleScope.launch {
                                        try {
                                            val result = authRepository.loginUser(email, password)
                                            result.onSuccess { firebaseUser ->
                                                userData = authRepository.getUserData(firebaseUser.uid)
                                                currentScreen = Screen.Main
                                            }.onFailure { exception ->
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Ошибка входа: ${exception.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Ошибка: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },
                                onRegisterClick = {
                                    currentScreen = Screen.Registration
                                },
                                onForgotPasswordClick = {
                                    currentScreen = Screen.ForgotPassword
                                }
                            )
                        }
                        is Screen.Registration -> {
                            RegistrationScreen(
                                onRegistrationSuccess = { user ->
                                    userData = user
                                    currentScreen = Screen.Main
                                },
                                onBackToLogin = { currentScreen = Screen.Login }
                            )
                        }
                        is Screen.Main -> {
                            MainScreen(
                                userData = userData,
                                onScheduleClick = { currentScreen = Screen.Schedule },
                                onTasksClick = { currentScreen = Screen.Tasks },
                                onGroupListClick = { currentScreen = Screen.GroupList },
                                onProfileClick = { currentScreen = Screen.Profile },
                                onAttendanceClick = { /* Implement if needed */ }
                            )
                        }
                        is Screen.Schedule -> {
                            ScheduleScreen(
                                onBackClick = { currentScreen = Screen.Main }
                            )
                        }
                        is Screen.Tasks -> {
                            TasksScreen(
                                userData = userData,
                                onBackClick = { currentScreen = Screen.Main }
                            )
                        }
                        is Screen.GroupList -> {
                            GroupListScreen(
                                onBackClick = { currentScreen = Screen.Main }
                            )
                        }
                        is Screen.Profile -> {
                            ProfileScreen(
                                userData = userData,
                                isDarkTheme = isDarkTheme.value,
                                onThemeChanged = { newValue ->
                                    isDarkTheme.value = newValue
                                    themePreference.isDarkTheme = newValue
                                },
                                onBackClick = { currentScreen = Screen.Main },
                                onLogout = {
                                    authRepository.signOut()
                                    currentScreen = Screen.Login
                                    userData = null
                                },
                                onUserDataUpdate = {
                                    lifecycleScope.launch {
                                        authRepository.getCurrentUser()?.let { user ->
                                            userData = authRepository.getUserData(user.uid)
                                        }
                                    }
                                }
                            )
                        }
                        is Screen.ForgotPassword -> {
                            ForgotPasswordScreen(
                                onBackToLogin = {
                                    currentScreen = Screen.Login
                                },
                                onResetSuccess = {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Инструкции по сбросу пароля отправлены на ваш email",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    currentScreen = Screen.Login
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}