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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val students = mutableListOf<StudentModel>()
    private lateinit var adapter: StudentAdapter
    private lateinit var studentDao: StudentDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Room database and DAO
        val database = StudentDatabase.getInstance(this)
        studentDao = database.studentDao()

        // Load students from database
        lifecycleScope.launch {
            students.addAll(studentDao.getAllStudents())
            adapter.notifyDataSetChanged()
        }

        adapter = StudentAdapter(students, this)
        val listView: ListView = findViewById(R.id.list_view_std)
        listView.adapter = adapter

        registerForContextMenu(listView)
    }

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
                val newStudent = StudentModel(id, name)
                lifecycleScope.launch {
                    studentDao.addStudent(newStudent)
                    students.add(newStudent)
                    adapter.notifyDataSetChanged()
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

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val newId = result.data?.getStringExtra("id")
            val newName = result.data?.getStringExtra("name")

            if (!newName.isNullOrEmpty() && !newId.isNullOrEmpty()) {
                lifecycleScope.launch {
                    val updatedStudent = studentDao.getAllStudents().find { it.studentId == newId }
                    if (updatedStudent != null) {
                        // Update the students list
                        val position = students.indexOfFirst { it.studentId == newId }
                        if (position != -1) {
                            students[position] = updatedStudent
                            adapter.notifyDataSetChanged()
                        }
                    }
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
                lifecycleScope.launch {
                    studentDao.deleteStudent(studentId)
                    students.removeAt(pos)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this@MainActivity, "Student removed", Toast.LENGTH_LONG).show()
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}
