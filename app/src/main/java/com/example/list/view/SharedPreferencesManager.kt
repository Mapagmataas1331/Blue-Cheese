package com.example.list.view

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class SharedPreferencesManager(context: Context) {
  private val sharedPreferences = context.getSharedPreferences(LIST_PREFS, Context.MODE_PRIVATE)

  fun saveItems(list: List<ListItemViewModel>) {
    val serializableSet = list.map {
      Json.encodeToString(it)
    }.toSet()
    sharedPreferences
      .edit()
      .putStringSet(LIST_KEY, serializableSet)
      .apply()
  }
    fun loadItems(): List<ListItemViewModel> = sharedPreferences.getStringSet(LIST_KEY, setOf())!!
      .map {
        Json.decodeFromString<ListItemViewModel>(it)
      }.toList()

//  fun saveItems(list: List<String>) {
//    sharedPreferences.edit()
//      .putStringSet(LIST_KEY, list.toSet())
//      .apply()
//  }
//  fun loadItems(): List<String> {
//    return sharedPreferences.getStringSet(LIST_KEY, emptySet())?.toList() ?: emptyList()
//  }

  companion object {
    private const val LIST_PREFS = "LIST_PREFS"
    private const val LIST_KEY = "LIST_KEY"
  }
}