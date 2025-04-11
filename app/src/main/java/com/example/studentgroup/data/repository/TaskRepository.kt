package com.example.studentgroup.data.repository

import android.util.Log
import com.example.studentgroup.data.model.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TaskRepository {
    private val database = FirebaseDatabase.getInstance()
    private val tasksRef = database.getReference("tasks")

    fun observeTasks(): Flow<List<Task>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TaskRepository", "Data changed, children count: ${snapshot.childrenCount}")
                val tasks = mutableListOf<Task>()
                val currentTime = System.currentTimeMillis()

                snapshot.children.forEach { child ->
                    try {
                        val task = child.getValue(Task::class.java)
                        Log.d("TaskRepository", "Task loaded: ${task?.title}, status: ${task?.status}")
                        
                        if (task != null) {
                            val updatedTask = if (task.status == "active" && task.deadline < currentTime) {
                                Log.d("TaskRepository", "Task ${task.title} is expired, updating status")
                                val expiredTask = task.copy(status = "expired")
                                tasksRef.child(task.id).setValue(expiredTask)
                                expiredTask
                            } else {
                                task
                            }
                            tasks.add(updatedTask)
                        }
                    } catch (e: Exception) {
                        Log.e("TaskRepository", "Error converting snapshot to Task", e)
                    }
                }

                val sortedTasks = tasks.sortedByDescending { it.createdAt }
                Log.d("TaskRepository", "Emitting ${sortedTasks.size} tasks")
                trySend(sortedTasks)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TaskRepository", "Error observing tasks", error.toException())
            }
        }

        tasksRef.addValueEventListener(listener)
        awaitClose { tasksRef.removeEventListener(listener) }
    }

    suspend fun addTask(task: Task): Result<Task> {
        return try {
            Log.d("TaskRepository", "Adding new task: ${task.title}")
            val taskId = UUID.randomUUID().toString()
            val taskWithId = task.copy(id = taskId)
            tasksRef.child(taskId).setValue(taskWithId).await()
            Log.d("TaskRepository", "Task added successfully with id: $taskId")
            Result.success(taskWithId)
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error adding task", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String) {
        try {
            tasksRef.child(taskId).removeValue().await()
        } catch (e: Exception) {
            throw e
        }
    }
} 