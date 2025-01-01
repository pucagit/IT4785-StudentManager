package com.example.studentmanager

import androidx.room.*

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStudent(student: StudentModel): Long

    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<StudentModel>

    @Update
    suspend fun updateStudent(student: StudentModel): Int

    @Query("DELETE FROM students WHERE id = :studentId")
    suspend fun deleteStudent(studentId: String): Int
}
