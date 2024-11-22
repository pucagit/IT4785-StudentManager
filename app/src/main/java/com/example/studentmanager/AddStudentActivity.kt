package com.example.studentmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_student)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = findViewById<EditText>(R.id.edt_name)
        val id = findViewById<EditText>(R.id.edt_id)

        findViewById<Button>(R.id.btn_add_std).setOnClickListener {
            val nameStr = name.text.toString()
            val idStr = id.text.toString()

            if (nameStr.isNotEmpty() && idStr.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("name", nameStr)
                resultIntent.putExtra("id", idStr)

                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                if (nameStr.isEmpty()) {
                    name.error = "Name cannot be empty"
                }
                if (idStr.isEmpty()) {
                    id.error = "ID cannot be empty"
                }
            }
        }

        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
