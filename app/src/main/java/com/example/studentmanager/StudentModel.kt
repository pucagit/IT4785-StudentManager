package com.example.studentmanager
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentModel(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val studentId: String,

    @ColumnInfo(name = "name")
    val studentName: String
)