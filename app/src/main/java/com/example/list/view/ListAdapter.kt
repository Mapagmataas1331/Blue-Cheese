package com.example.list.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.list.R

class ListAdapter(
  initialDataSet: List<String>,
  private val onButtonClick: (position: Int, title: String) -> Unit
): RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
  private val dataSet = initialDataSet.toMutableList()
  private var sharedPreferencesManager: SharedPreferencesManager? = null

  inner class ListViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.item_text)
    private val editButton: AppCompatButton = view.findViewById(R.id.item_edit)
    private val deleteButton: AppCompatButton = view.findViewById(R.id.item_delete)

    init {
      editButton.setOnClickListener {
        onButtonClick(adapterPosition, textView.text.toString())
      }
      deleteButton.setOnClickListener {
        val position = adapterPosition
        val itemTitle = dataSet[position]

        AlertDialog.Builder(deleteButton.context)
          .setTitle(deleteButton.context.getString(R.string.delete_item_title))
          .setMessage(deleteButton.context.getString(R.string.delete_item_message, itemTitle))
          .setPositiveButton(deleteButton.context.getString(R.string.delete_item_button_delete)) { dialog, _ ->
            dataSet.removeAt(position)
            notifyItemRemoved(position)
            dialog.dismiss()
          }
          .setNegativeButton(deleteButton.context.getString(R.string.delete_item_button_cancel)) { dialog, _ ->
            dialog.dismiss()
          }
          .show()
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
    val binding = LayoutInflater.from(parent.context)
      .inflate(R.layout.list_item, parent, false)
    return ListViewHolder(binding.rootView)
  }

  override fun getItemCount(): Int = dataSet.size

  override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
    holder.textView.text = dataSet[position]
  }

  fun setSharedPreferencesManager(manager: SharedPreferencesManager) {
    sharedPreferencesManager = manager
  }

  fun add(item: String) {
    dataSet.add(item)
    notifyItemInserted(dataSet.lastIndex)
  }

  fun remove(position: Int) {
    dataSet.removeAt(position)
    notifyItemRemoved(position)
    sharedPreferencesManager?.saveItems(dataSet)
  }

  fun edit(position: Int, newTitle: String) {
    dataSet[position] = newTitle
    notifyItemChanged(position)
  }

  fun getDataAtPosition(position: Int): String {
    return dataSet[position]
  }

  fun getData(): List<String> {
    return dataSet.toList()
  }
}
