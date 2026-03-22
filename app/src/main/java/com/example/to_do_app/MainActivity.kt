package com.example.to_do_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Simple To-Do app: add tasks with EditText + Add, select multiple tasks with checkboxes,
 * delete all selected tasks with the button above the list.
 */
class MainActivity : ComponentActivity() {

    /** Task text shown in the list (order matches RecyclerView positions). */
    private val tasks = mutableListOf<String>()

    /** Positions of tasks that are selected (checked) for bulk delete. */
    private val selectedPositions = mutableSetOf<Int>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTask = findViewById<EditText>(R.id.editTextTask)
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        recyclerView = findViewById(R.id.recyclerViewTasks)

        adapter = TaskAdapter(tasks = tasks, selectedPositions = selectedPositions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonAdd.setOnClickListener {
            val text = editTask.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            tasks.add(text)
            editTask.text.clear()
            adapter.notifyItemInserted(tasks.size - 1)
        }

        buttonDelete.setOnClickListener {
            if (selectedPositions.isEmpty()) {
                Toast.makeText(this, "No task selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Remove from highest index down so remaining indices stay valid.
            val sorted = selectedPositions.toList().sortedDescending()
            for (index in sorted) {
                if (index in tasks.indices) {
                    tasks.removeAt(index)
                }
            }
            selectedPositions.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private inner class TaskAdapter(
        private val tasks: List<String>,
        private val selectedPositions: MutableSet<Int>
    ) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

        private val colorNormal = ContextCompat.getColor(this@MainActivity, R.color.task_card_normal)
        private val colorSelected = ContextCompat.getColor(this@MainActivity, R.color.task_card_selected)

        inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val card: CardView = itemView.findViewById(R.id.cardTask)
            val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxSelect)
            val textTitle: TextView = itemView.findViewById(R.id.textTaskTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, parent, false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val taskText = tasks[position]
            val isSelected = selectedPositions.contains(position)

            holder.textTitle.text = taskText
            holder.card.setCardBackgroundColor(if (isSelected) colorSelected else colorNormal)

            holder.checkBox.setOnCheckedChangeListener(null)
            holder.checkBox.isChecked = isSelected
            holder.checkBox.setOnCheckedChangeListener { _, checked ->
                val pos = holder.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnCheckedChangeListener
                if (checked) {
                    selectedPositions.add(pos)
                } else {
                    selectedPositions.remove(pos)
                }
                notifyItemChanged(pos)
            }
        }

        override fun getItemCount(): Int = tasks.size
    }
}
