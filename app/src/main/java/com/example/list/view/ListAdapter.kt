package com.example.list.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.list.R
import android.content.Context

class ListAdapter(
  initialDataSet: List<String>,
  private val context: Context,
  private val sharedPreferencesManager: SharedPreferencesManager,
  private val onButtonClick: (position: Int, title: String) -> Unit
) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
  private val dataSet = initialDataSet.toMutableList()

  inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.item_text)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
    val binding = LayoutInflater.from(parent.context)
      .inflate(R.layout.list_item, parent, false)
    return ListViewHolder(binding)
  }

  override fun getItemCount(): Int = dataSet.size

  override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
    val itemTitle = dataSet[position]
    holder.textView.text = itemTitle

    holder.itemView.setOnClickListener {
      onButtonClick(position, itemTitle)
    }

    holder.itemView.setOnLongClickListener {
      showDeleteDialog(position, itemTitle)
      true
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  private fun showDeleteDialog(position: Int, itemTitle: String) {
    AlertDialog.Builder(context)
      .setTitle(context.getString(R.string.delete_item_title))
      .setMessage(context.getString(R.string.delete_item_message, itemTitle))
      .setPositiveButton(context.getString(R.string.delete_item_button_delete)) { _, _ ->
        remove(position)
      }
      .setNegativeButton(context.getString(R.string.delete_item_button_cancel)) { _, _ ->
        notifyDataSetChanged()
      }
      .show()
  }

  fun add(item: String) {
    dataSet.add(item)
    notifyItemInserted(dataSet.lastIndex)
  }

  fun remove(position: Int) {
    dataSet.removeAt(position)
    notifyItemRemoved(position)
    sharedPreferencesManager.saveItems(dataSet)
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