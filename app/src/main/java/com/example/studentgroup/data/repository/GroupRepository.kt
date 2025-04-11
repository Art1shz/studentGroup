package com.example.studentgroup.data.repository

import com.example.studentgroup.data.model.Student
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class GroupRepository {
    private val database = FirebaseDatabase.getInstance()
    private val groupRef = database.getReference("group")

    suspend fun initializeGroupList() {
        val students = listOf(
            Student("1", "Алёшин", "Никита", "Андреевич"),
            Student("2", "Бессонов", "Артём", "Анатольевич"),
            Student("3", "Бундина", "Алена", "Денисовна"),
            Student("4", "Васильев", "Назар", "Анатольевич"),
            Student("5", "Горбунов", "Егор", "Максимович"),
            Student("6", "Ефимова", "Мария", "Дмитриевна"),
            Student("7", "Зюзь", "Кирилл", "Евгеньевич"),
            Student("8", "Иванов", "Илья", "Николаевич"),
            Student("9", "Кажурин", "Максим", "Григорьевич"),
            Student("10", "Князьков", "Эльдар", "Владимирович"),
            Student("11", "Косарева", "Дарья", "Сергеевна"),
            Student("12", "Левицкий", "Илья", "Дмитриевич"),
            Student("13", "Матвеев", "Максим", "Владиславович"),
            Student("14", "Митин", "Владислав", "Михайлович"),
            Student("15", "Петков", "Семен", "Васильевич"),
            Student("16", "Сазуров", "Глеб", "Сергеевич"),
            Student("17", "Симонешко", "Максим", "Владимирович"),
            Student("18", "Степанов", "Кирилл", "Дмитриевич"),
            Student("19", "Туранский", "Антон", "Евгеньевич"),
            Student("20", "Тутаев", "Никита", "Павлович"),
            Student("21", "Хайдарова", "Елена", "Фаридовна"),
            Student("22", "Шаньгина", "Алина", "Евгеньевна"),
            Student("23", "Шатковский", "Максим", "Юрьевич"),
            Student("24", "Яремчук", "Анна", "Максимовна"),
            Student("25", "Чулина", "Диана", "Витальевна")
        )

        val snapshot = groupRef.get().await()
        if (!snapshot.exists()) {
            students.forEach { student ->
                groupRef.child(student.id).setValue(student).await()
            }
        }
    }

    suspend fun getGroupList(): List<Student> {
        val snapshot = groupRef.get().await()
        val students = mutableListOf<Student>()
        
        snapshot.children.forEach { childSnapshot ->
            childSnapshot.getValue(Student::class.java)?.let { student ->
                students.add(student)
            }
        }
        
        return students.sortedBy { it.lastName }
    }
} 