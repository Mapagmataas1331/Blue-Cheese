package com.example.list.view

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SharedPreferencesManager(context: Context) {
  private val sharedPreferences = context.getSharedPreferences(LIST_PREFS, Context.MODE_PRIVATE)

  fun saveItems(list: List<ListItemDataModel>) {
    val serializedList = Json.encodeToString(list)
    sharedPreferences.edit().putString(LIST_KEY, serializedList).apply()
  }

  fun loadItems(): List<ListItemDataModel> {
    val serializedList = sharedPreferences.getString(LIST_KEY, null)
    return if (serializedList != null) {
      Json.decodeFromString(serializedList)
    } else {
      emptyList()
    }
  }

  companion object {
    private const val LIST_PREFS = "LIST_PREFS"
    private const val LIST_KEY = "LIST_KEY"
  }
}