package com.example.list.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.list.databinding.AddItemFragmentBinding

class AddItemFragment : Fragment() {
  private val itemPosition: Int?
    get() = arguments?.getInt(POSITION_KEY)

  private val itemTitle: String?
    get() = arguments?.getString(TITLE_KEY)

  private val itemDescription: String?
    get() = arguments?.getString(DESCRIPTION_KEY)


  private var adapter: ListAdapter? = null
  private var sharedPreferencesManager: SharedPreferencesManager? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is ListFragment.ListFragmentListener) {
      adapter = context.getListAdapter()
      sharedPreferencesManager = context.getSharedPreferencesManager()
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View = AddItemFragmentBinding.inflate(inflater, container, false).apply {
    title.setText(itemTitle ?: "")
    description.setText(itemDescription ?: "")
    floatBtnCreate.setOnClickListener {
      val newItem = ListItemDataModel(
        title = title.text.toString(),
        description = description.text.toString()
      )

      if (itemPosition == null) {
        setFragmentResult(
          ListFragment.ADD_ITEM_REQUEST_KEY,
          bundleOf(
            ListFragment.ADD_ITEM_TITLE_KEY to newItem.title,
            ListFragment.ADD_ITEM_DESCRIPTION_KEY to newItem.description
          )
        )
        adapter?.add(newItem)
      } else {
        setFragmentResult(
          ListFragment.EDIT_ITEM_REQUEST_KEY,
          bundleOf(
            ListFragment.ADD_ITEM_TITLE_KEY to newItem.title,
            ListFragment.ADD_ITEM_DESCRIPTION_KEY to newItem.description,
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
  }.root

  companion object {
    const val POSITION_KEY = "POSITION_KEY"
    const val TITLE_KEY = "TITLE_KEY"
    const val DESCRIPTION_KEY = "DESCRIPTION_KEY"

    fun editItemInstance(position: Int, title: String, description: String): AddItemFragment {
      val fragment = AddItemFragment()
      val bundle = bundleOf(
        POSITION_KEY to position,
        TITLE_KEY to title,
        DESCRIPTION_KEY to description
      )
      fragment.arguments = bundle
      return fragment
    }
  }
}
