package com.example.list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.list.R
import com.example.list.databinding.ListFragmentBinding

class ListFragment : Fragment() {
  private lateinit var adapter: ListAdapter
  private lateinit var sharedPreferencesManager: SharedPreferencesManager

  private var simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
  ) {
    override fun onMove(
      recyclerView: RecyclerView,
      viewHolder: RecyclerView.ViewHolder,
      target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
      val position = viewHolder.adapterPosition
      val title = adapter.getDataAtPosition(position)

      if (swipeDir == ItemTouchHelper.LEFT) {
        AlertDialog.Builder(requireContext())
          .setTitle(getString(R.string.delete_item_title))
          .setMessage(getString(R.string.delete_item_message, title))
          .setPositiveButton(getString(R.string.delete_item_button_delete)) { dialog, _ ->
            adapter.remove(position)
            dialog.dismiss()
          }
          .setNegativeButton(getString(R.string.delete_item_button_cancel)) { dialog, _ ->
            adapter.notifyItemChanged(position)
            dialog.dismiss()
          }
          .show()
      } else if (swipeDir == ItemTouchHelper.RIGHT) {
        parentFragmentManager
          .beginTransaction()
          .replace(R.id.container, AddItemFragment.editItemInstance(position, title))
          .addToBackStack(AddItemFragment::class.java.canonicalName)
          .commit()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    sharedPreferencesManager = SharedPreferencesManager(requireContext())
    val savedItems = sharedPreferencesManager.loadItems()
    adapter = ListAdapter(savedItems) { position, title ->
      parentFragmentManager
        .beginTransaction()
        .replace(R.id.container, AddItemFragment.editItemInstance(position, title))
        .addToBackStack(AddItemFragment::class.java.canonicalName)
        .commit()
    }
    adapter.setSharedPreferencesManager(sharedPreferencesManager)

    setFragmentResultListener(ADD_ITEM_REQUEST_KEY) { _, bundle ->
      val newItem = bundle.getString(ADD_ITEM_TITLE_KEY, "")
      adapter.add(newItem)
      sharedPreferencesManager.saveItems(adapter.getData())
    }

    setFragmentResultListener(EDIT_ITEM_REQUEST_KEY) { _, bundle ->
      val position = bundle.getInt(ADD_ITEM_POSITION_KEY)
      val editItem = bundle.getString(ADD_ITEM_TITLE_KEY, "")
      adapter.edit(position, editItem)
      sharedPreferencesManager.saveItems(adapter.getData())
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding = ListFragmentBinding.inflate(inflater, container, false).apply {
      list.layoutManager = LinearLayoutManager(requireContext())
      list.adapter = adapter
      list.addItemDecoration(
        DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
          setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.item_separator, requireContext().theme)!!)
        }
      )
      ItemTouchHelper(simpleItemTouchCallback).attachToRecyclerView(list)

      floatBtnAdd.setOnClickListener {
        parentFragmentManager
          .beginTransaction()
          .replace(R.id.container, AddItemFragment())
          .addToBackStack(AddItemFragment::class.java.canonicalName)
          .commit()
      }
    }
    return binding.root
  }

  interface ListFragmentListener {
    fun getListAdapter(): ListAdapter
    fun getSharedPreferencesManager(): SharedPreferencesManager
  }

  companion object {
    const val ADD_ITEM_REQUEST_KEY = "ADD_ITEM_REQUEST_KEY"
    const val EDIT_ITEM_REQUEST_KEY = "EDIT_ITEM_REQUEST_KEY"

    const val ADD_ITEM_TITLE_KEY = "ADD_ITEM_TITLE_KEY"
    const val ADD_ITEM_POSITION_KEY = "ADD_ITEM_EXTRA_KEY"
  }
}