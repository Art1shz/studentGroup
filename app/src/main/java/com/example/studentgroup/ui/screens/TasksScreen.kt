package com.example.studentgroup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentgroup.data.model.Task
import com.example.studentgroup.data.model.User
import com.example.studentgroup.data.repository.TaskRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    userData: User?,
    onBackClick: () -> Unit
) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val taskRepository = remember { TaskRepository() }
    val colorScheme = MaterialTheme.colorScheme

    val isTeacher = userData?.let { user ->
        (user.firstName == "Александр" && user.lastName == "Махов") ||
        (user.firstName == "Дмитрий" && user.lastName == "Венедиктов") ||
        (user.firstName == "Галина" && user.lastName == "Бундина")
    } ?: false

    LaunchedEffect(Unit) {
        taskRepository.observeTasks().collect { updatedTasks ->
            tasks = updatedTasks
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Задания",
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
        },
        floatingActionButton = {
            if (isTeacher) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF8B82FF),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить задание")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                TaskCard(task = task)
            }
        }

        if (showAddDialog) {
            AddTaskDialog(
                onDismiss = { showAddDialog = false },
                onTaskAdded = { task ->
                    scope.launch {
                        taskRepository.addTask(task).onSuccess {
                            showAddDialog = false
                        }
                    }
                },
                currentUser = userData
            )
        }
    }
}

@Composable
fun TaskCard(task: Task) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val deadlineDate = Date(task.deadline)
    val isExpired = task.status == "expired"
    val colorScheme = MaterialTheme.colorScheme
    
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF8B82FF),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = task.teacherFullName.split(" ")
                                    .map { it.firstOrNull()?.toString() ?: "" }
                                    .joinToString(""),
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Column {
                        Text(
                            text = task.teacherFullName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onSurface
                        )
                        Text(
                            text = task.subject,
                            fontSize = 14.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = dateFormat.format(deadlineDate),
                    fontSize = 14.sp,
                    color = if (isExpired) colorScheme.error else colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = task.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
            Text(
                text = task.description,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariant
            )
            
            // Status indicator
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when(task.status) {
                    "completed" -> "Выполнено"
                    "expired" -> "Просрочено"
                    else -> "Активно"
                },
                fontSize = 14.sp,
                color = when(task.status) {
                    "completed" -> colorScheme.primary
                    "expired" -> colorScheme.error
                    else -> Color(0xFF8B82FF)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (Task) -> Unit,
    currentUser: User?
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить задание") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Предмет") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Выбрать дату")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        selectedDate?.let { 
                            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(it))
                        } ?: "Выберите срок сдачи"
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && subject.isNotBlank() && 
                        selectedDate != null && currentUser != null) {
                        val task = Task(
                            title = title,
                            description = description,
                            subject = subject,
                            teacherUid = currentUser.uid,
                            teacherFullName = "${currentUser.firstName} ${currentUser.lastName}",
                            deadline = selectedDate!!
                        )
                        onTaskAdded(task)
                    }
                },
                enabled = title.isNotBlank() && description.isNotBlank() && 
                          subject.isNotBlank() && selectedDate != null && currentUser != null
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
} 