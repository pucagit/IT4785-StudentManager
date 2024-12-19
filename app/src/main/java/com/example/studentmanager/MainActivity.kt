package com.example.studentmanager

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
//    private val students = mutableListOf(
//        StudentModel("Nguyễn Văn An", "SV001"),
//        StudentModel("Trần Thị Bảo", "SV002"),
//        StudentModel("Lê Hoàng Cường", "SV003"),
//        StudentModel("Phạm Thị Dung", "SV004"),
//        StudentModel("Đỗ Minh Đức", "SV005"),
//        StudentModel("Vũ Thị Hoa", "SV006"),
//        StudentModel("Hoàng Văn Hải", "SV007"),
//        StudentModel("Bùi Thị Hạnh", "SV008"),
//        StudentModel("Đinh Văn Hùng", "SV009"),
//        StudentModel("Nguyễn Thị Linh", "SV010"),
//        StudentModel("Phạm Văn Long", "SV011"),
//        StudentModel("Trần Thị Mai", "SV012"),
//        StudentModel("Lê Thị Ngọc", "SV013"),
//        StudentModel("Vũ Văn Nam", "SV014"),
//        StudentModel("Hoàng Thị Phương", "SV015"),
//        StudentModel("Đỗ Văn Quân", "SV016"),
//        StudentModel("Nguyễn Thị Thu", "SV017"),
//        StudentModel("Trần Văn Tài", "SV018"),
//        StudentModel("Phạm Thị Tuyết", "SV019"),
//        StudentModel("Lê Văn Vũ", "SV020")
//    )

    private val students = mutableListOf<StudentModel>()
    private lateinit var adapter: StudentAdapter
    private lateinit var dbHelper: StudentDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = StudentDatabaseHelper(this)
        students.addAll(dbHelper.getAllStudents())
        adapter = StudentAdapter(students, this)

        val listView: ListView = findViewById(R.id.list_view_std)
        listView.adapter = adapter

        registerForContextMenu(listView)
    }


//    Option Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val name = result.data?.getStringExtra("name")
            val id = result.data?.getStringExtra("id")

            if (!name.isNullOrEmpty() && !id.isNullOrEmpty()) {
                val newStudent = StudentModel(name, id)
                if (dbHelper.addStudent(newStudent)) {
                    students.add(newStudent)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Error adding student to database", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_add_std -> {
                val intent = Intent(this, AddStudentActivity::class.java)
                launcher.launch(intent)
                true
            }
            R.id.to_next_activity -> {
                val intent = Intent(this, PopupMenuActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
    }

//    Context Menu
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val newName = result.data?.getStringExtra("name")
            val newId = result.data?.getStringExtra("id")
            val position = result.data?.getIntExtra("position", -1)
            val originalId = result.data?.getStringExtra("originalId")

            if (!newName.isNullOrEmpty() && !newId.isNullOrEmpty() && position != null && position >= 0) {
                val updatedStudent = StudentModel(newName, newId)
                if (dbHelper.updateStudent(updatedStudent, originalId)) {
                    students[position] = updatedStudent
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Error updating student in database", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val pos = (item.menuInfo as AdapterContextMenuInfo).position
        return when (item.itemId) {
            R.id.item_edit -> {
                val intent = Intent(this, EditStudentActivity::class.java)
                intent.putExtra("name", students[pos].studentName)
                intent.putExtra("id", students[pos].studentId)
                intent.putExtra("position", pos)
                intent.putExtra("originalId", students[pos].studentId) // Pass original ID
                editLauncher.launch(intent)
                true
            }
            R.id.item_remove -> {
                val studentId = students[pos].studentId
                if (dbHelper.deleteStudent(studentId)) {
                    students.removeAt(pos)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Student removed", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error deleting student from database", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}