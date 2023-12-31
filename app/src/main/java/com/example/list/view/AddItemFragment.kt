package com.example.list.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.list.databinding.AddItemFragmentBinding
import org.joda.time.DateTime
import java.util.Calendar


class AddItemFragment : Fragment() {
  private val itemPosition: Int?
    get() = arguments?.getInt(POSITION_KEY)

  private val itemTitle: String?
    get() = arguments?.getString(TITLE_KEY)

  private val itemDescription: String?
    get() = arguments?.getString(DESCRIPTION_KEY)

  private val itemDate: String?
    get() = arguments?.getString(DATE_KEY)

  private var adapter: ListAdapter? = null
  private var sharedPreferencesManager: SharedPreferencesManager? = null

  private fun scheduleNotification(item: ListItemDataModel, days: Int) {
    if (item.date.isEmpty() or item.title.isEmpty()) {
      return
    }
    val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(requireContext(), NotificationReceiver::class.java)
    intent.putExtra("title", item.title)
    intent.putExtra("description", item.description)
    intent.putExtra("date", item.date)
    val pendingIntent = PendingIntent.getBroadcast(
      requireContext(),
      0,
      intent,
      PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
    )

    val itemDate = org.joda.time.format.DateTimeFormat.forPattern("dd MM yyyy").parseDateTime(item.date) ?: DateTime()
    val notificationTime = DateTime.now().millis + 5000 //itemDate.minusDays(days).millis
    Log.d("notifications","Now: ${DateTime.now().millis}")
    Log.d("notifications","notificationTime: $notificationTime")
    alarmManager.set(
      AlarmManager.RTC,
      notificationTime,
      pendingIntent
    )
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is ListFragment.ListFragmentListener) {
      adapter = context.getListAdapter()
      sharedPreferencesManager = context.getSharedPreferencesManager()
    }
  }

  @SuppressLint("SetTextI18n")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = AddItemFragmentBinding.inflate(inflater, container, false).apply {
    title.setText(itemTitle ?: "")
    description.setText(itemDescription ?: "")
    dateText.setText(itemDate ?: "")
    datePicker.setOnClickListener {
      val calendar = Calendar.getInstance()
      val currentYear = calendar.get(Calendar.YEAR)
      val currentMonth = calendar.get(Calendar.MONTH)
      val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

      val datePickerDialog = DatePickerDialog(
        requireContext(),
        { _, year, month, dayOfMonth ->
          dateText.setText("$dayOfMonth ${month + 1} $year")
        },
        currentYear,
        currentMonth,
        currentDay
      )
      datePickerDialog.show()
    }

    floatBtnCreate.setOnClickListener {
      val newItem = ListItemDataModel(
        title = title.text.toString(),
        description = description.text.toString(),
        date = dateText.text.toString(),
      )

      if (itemPosition == null) {
        setFragmentResult(
          ListFragment.ADD_ITEM_REQUEST_KEY,
          bundleOf(
            ListFragment.ADD_ITEM_TITLE_KEY to newItem.title,
            ListFragment.ADD_ITEM_DESCRIPTION_KEY to newItem.description,
            ListFragment.ADD_ITEM_DATE_KEY to newItem.date
          )
        )
        adapter?.add(newItem)
        scheduleNotification(newItem, 1)
      } else {
        setFragmentResult(
          ListFragment.EDIT_ITEM_REQUEST_KEY,
          bundleOf(
            ListFragment.ADD_ITEM_TITLE_KEY to newItem.title,
            ListFragment.ADD_ITEM_DESCRIPTION_KEY to newItem.description,
            ListFragment.ADD_ITEM_DATE_KEY to newItem.date,
            ListFragment.ADD_ITEM_POSITION_KEY to itemPosition
          )
        )
        itemPosition?.let { position ->
          adapter?.edit(position, newItem)
        }
      }
      sharedPreferencesManager?.saveItems(adapter?.getData() ?: emptyList())
      parentFragmentManager.popBackStack()
    }
    floatBtnCancel.setOnClickListener {
      parentFragmentManager.popBackStack()
    }
  }.root

  companion object {
    const val POSITION_KEY = "POSITION_KEY"
    const val TITLE_KEY = "TITLE_KEY"
    const val DESCRIPTION_KEY = "DESCRIPTION_KEY"
    const val DATE_KEY = "DATE_KEY"

    fun editItemInstance(position: Int, title: String, description: String, date: String): AddItemFragment {
      val fragment = AddItemFragment()
      val bundle = bundleOf(
        POSITION_KEY to position,
        TITLE_KEY to title,
        DESCRIPTION_KEY to description,
        DATE_KEY to date
      )
      fragment.arguments = bundle
      return fragment
    }
  }
}
