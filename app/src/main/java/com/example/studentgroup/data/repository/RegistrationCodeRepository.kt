package com.example.studentgroup.data.repository

import com.example.studentgroup.data.model.RegistrationCode
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class RegistrationCodeRepository {
    private val database = FirebaseDatabase.getInstance()
    private val codesRef = database.getReference("registration_codes")

    private fun generateCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }

    suspend fun initializeRegistrationCodes() {
        val snapshot = codesRef.get().await()
        if (snapshot.exists()) {
            return
        }

        // Если кодов нет, создаем новые
        val students = listOf(
            "Алёшин Никита",
            "Бессонов Артём",
            "Бундина Алена",
            "Васильев Назар",
            "Горбунов Егор",
            "Ефимова Мария",
            "Зюзь Кирилл",
            "Иванов Илья",
            "Кажурин Максим",
            "Князьков Эльдар",
            "Косарева Дарья",
            "Левицкий Илья",
            "Матвеев Максим",
            "Митин Владислав",
            "Петков Семен",
            "Сазуров Глеб",
            "Симонешко Максим",
            "Степанов Кирилл",
            "Туранский Антон",
            "Тутаев Никита",
            "Хайдарова Елена",
            "Шаньгина Алина",
            "Шатковский Максим",
            "Яремчук Анна",
            "Чулина Диана",
            "Венедиктов Дмитрий",
            "Махов Александр",
            "Бундина Галина"
        )

        val codes = students.associate { fullName ->
            val names = fullName.split(" ")
            val code = generateCode()
            code to RegistrationCode(
                code = code,
                firstName = names[1],
                lastName = names[0],
                isUsed = false
            )
        }

        codesRef.setValue(codes).await()
    }

    suspend fun validateCode(code: String): RegistrationCode? {
        val snapshot = codesRef.child(code).get().await()
        return if (snapshot.exists()) {
            snapshot.getValue(RegistrationCode::class.java)
        } else {
            null
        }
    }

    suspend fun markCodeAsUsed(code: String, email: String) {
        codesRef.child(code).updateChildren(
            mapOf(
                "isUsed" to true,
                "usedBy" to email
            )
        ).await()
    }

    suspend fun getAllCodes(): Map<String, RegistrationCode> {
        val snapshot = codesRef.get().await()
        val codes = mutableMapOf<String, RegistrationCode>()
        
        snapshot.children.forEach { child ->
            child.getValue(RegistrationCode::class.java)?.let { code ->
                codes[child.key!!] = code
            }
        }
        
        return codes
    }
} 