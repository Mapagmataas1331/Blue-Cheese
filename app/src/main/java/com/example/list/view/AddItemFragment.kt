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
    floatBtnCreate.setOnClickListener {
      if (itemPosition == null) {
        setFragmentResult(
          ListFragment.ADD_ITEM_REQUEST_KEY,
          bundleOf(ListFragment.ADD_ITEM_TITLE_KEY to title.text.toString())
        )
        adapter?.add(title.text.toString()) // Add this line to save the new item
      } else {
        setFragmentResult(
          ListFragment.EDIT_ITEM_REQUEST_KEY,
          bundleOf(
            ListFragment.ADD_ITEM_TITLE_KEY to title.text.toString(),
            ListFragment.ADD_ITEM_POSITION_KEY to itemPosition
          )
        )
        itemPosition?.let { position ->
          adapter?.edit(position, title.text.toString()) // Add this line to save the edited item
        }
      }
      sharedPreferencesManager?.saveItems(adapter?.getData() ?: emptyList()) // Save items to SharedPreferences
      parentFragmentManager.popBackStack()
    }
  }.root

  companion object {
    const val POSITION_KEY = "POSITION_KEY"
    const val TITLE_KEY = "TITLE_KEY"

    fun editItemInstance(position: Int, title: String): Fragment {
      val fragment = AddItemFragment()
      val bundle = bundleOf(POSITION_KEY to position, TITLE_KEY to title)
      fragment.arguments = bundle
      return fragment
    }
  }
}
