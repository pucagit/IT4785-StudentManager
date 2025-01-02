package com.example.studentmanager
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentModel(
    @PrimaryKey(autoGenerate = true)
    val _id: Int = 0,
    val studentId: String,
    val studentName: String
)