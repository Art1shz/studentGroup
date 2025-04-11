package com.example.studentgroup.data.auth

import com.example.studentgroup.data.model.User
import com.example.studentgroup.data.model.RegistrationCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.google.firebase.auth.EmailAuthProvider
import android.util.Log

class FirebaseAuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")
    private val codesRef = database.getReference("registration_codes")

    suspend fun registerUser(
        email: String,
        password: String,
        registrationCode: String
    ): Result<FirebaseUser> {
        return try {
            val codeSnapshot = codesRef.child(registrationCode).get().await()
            val code = codeSnapshot.getValue(RegistrationCode::class.java)

            if (code == null) {
                return Result.failure(Exception("Неверный код регистрации"))
            }

            if (code.isUsed) {
                return Result.failure(Exception("Этот код уже использован"))
            }

            val result = auth.createUserWithEmailAndPassword(email, password).await()
            
            result.user?.let { firebaseUser ->
                val user = User(
                    uid = firebaseUser.uid,
                    email = email,
                    firstName = code.firstName,
                    lastName = code.lastName,
                    registrationCode = registrationCode
                )

                usersRef.child(firebaseUser.uid).setValue(user).await()

                codesRef.child(registrationCode).updateChildren(
                    mapOf(
                        "isUsed" to true,
                        "usedBy" to email
                    )
                ).await()

                Result.success(firebaseUser)
            } ?: Result.failure(Exception("Ошибка при регистрации"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Ошибка при входе"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun getUserData(uid: String): User? {
        val snapshot = usersRef.child(uid).get().await()
        return snapshot.getValue(User::class.java)
    }

    fun logout() {
        auth.signOut()
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun updateEmail(newEmail: String, currentPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext Result.failure(Exception("Пользователь не авторизован"))
            
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()
            
            user.updateEmail(newEmail).await()
            
            database.getReference("users/${user.uid}/email").setValue(newEmail).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepository", "Error updating email: ${e.message}", e)
            if (e.message?.contains("This operation is not allowed") == true) {
                Result.failure(Exception("Для смены email необходимо включить эту функцию в Firebase Console. Пожалуйста, обратитесь к администратору."))
            } else {
                Result.failure(Exception("Ошибка при обновлении email: ${e.message}"))
            }
        }
    }

    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext Result.failure(Exception("Пользователь не авторизован"))
            
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()
            
            user.updatePassword(newPassword).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuthRepository", "Error updating password: ${e.message}", e)
            Result.failure(Exception("Ошибка при обновлении пароля: ${e.message}"))
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 