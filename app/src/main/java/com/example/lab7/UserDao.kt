package com.example.lab7

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    suspend fun getAll(): List<User>

    @Insert
    suspend fun insert(user: User)

    @Query("DELETE FROM User WHERE uid = (SELECT uid FROM User ORDER BY uid DESC LIMIT 1)")
    suspend fun deleteLastUser()
}
