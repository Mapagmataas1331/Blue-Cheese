package com.example.list.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.list.R
import kotlinx.serialization.Serializable
import org.joda.time.DateTime
import org.joda.time.Days

class ListAdapter(
  initialDataSet: List<ListItemDataModel>,
  private val context: Context,
  private val sharedPreferencesManager: SharedPreferencesManager,
  private val onItemClick: (position: Int, item: ListItemDataModel) -> Unit
) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
  private val dataSet = initialDataSet.toMutableList()
  private val dateFormat = org.joda.time.format.DateTimeFormat.forPattern("dd MM yyyy")

  inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.item_text)
    val editButton: AppCompatButton = view.findViewById(R.id.item_edit)
    val deleteButton: AppCompatButton = view.findViewById(R.id.item_delete)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
    val binding = LayoutInflater.from(parent.context)
      .inflate(R.layout.list_item, parent, false)
    return ListViewHolder(binding)
  }
  override fun getItemCount(): Int = dataSet.size

  override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
    val item = dataSet[position]
    holder.textView.text = getItemTitleWithTimeDifference(item)

    holder.editButton.setOnClickListener {
      onItemClick(position, item)
    }

    holder.itemView.setOnClickListener {
      onItemClick(position, item)
    }

    holder.deleteButton.setOnClickListener {
      showDeleteDialog(position, item.title)
    }

    holder.itemView.setOnLongClickListener {
      showDeleteDialog(position, item.title)
      true
    }
  }

  private fun getItemTitleWithTimeDifference(item: ListItemDataModel): String {
    if (item.date.isEmpty()) return item.title
    val itemDate = dateFormat.parseDateTime(item.date) ?: DateTime()
    val currentDate = DateTime()
    var timeDifference = Days.daysBetween(currentDate, itemDate).days
    if (currentDate.isBefore(itemDate)) timeDifference++

    return "${item.title} ($timeDifference)"
  }

  private fun showDeleteDialog(position: Int, itemTitle: String) {
    AlertDialog.Builder(context)
      .setTitle(context.getString(R.string.delete_item_title))
      .setMessage(context.getString(R.string.delete_item_message, itemTitle))
      .setPositiveButton(context.getString(R.string.delete_item_button_delete)) { _, _ ->
        remove(position)
      }
      .setNegativeButton(context.getString(R.string.delete_item_button_cancel), null)
      .show()
  }

  fun getData(): List<ListItemDataModel> = dataSet

  fun add(item: ListItemDataModel) {
    dataSet.add(item)
    notifyItemInserted(dataSet.lastIndex)
  }

  fun edit(position: Int, newItem: ListItemDataModel) {
    dataSet[position] = newItem
    notifyItemChanged(position)
  }

  fun remove(position: Int) {
    dataSet.removeAt(position)
    notifyItemRemoved(position)
    notifyItemRangeChanged(position, dataSet.size)
    sharedPreferencesManager.saveItems(dataSet)
  }

  fun getDataAtPosition(position: Int): ListItemDataModel {
    return dataSet[position]
  }
}
@Serializable
data class ListItemDataModel(
  val title: String,
  val description: String,
  val date: String
)
