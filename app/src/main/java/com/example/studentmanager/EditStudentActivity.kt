package com.example.studentmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_student)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = findViewById<EditText>(R.id.edt_name)
        val id = findViewById<EditText>(R.id.edt_id)

        val nameText = intent.getStringExtra("name")
        val idText = intent.getStringExtra("id")

        name.setText(nameText)
        id.setText(idText)

        findViewById<Button>(R.id.btn_edt_std).setOnClickListener {
            val newName = name.text.toString()
            val newId = id.text.toString()

            if (newName.isNotEmpty() && newId.isNotEmpty()) {
                val resultIntent = intent
                resultIntent.putExtra("name", newName)
                resultIntent.putExtra("id", newId)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                if (newName.isEmpty()) name.error = "Name cannot be empty"
                if (newId.isEmpty()) id.error = "ID cannot be empty"
            }
        }

        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}