package com.example.list.view

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
  private val sharedPreferences: SharedPreferences =
    context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
  fun saveItems(items: List<String>) {
    sharedPreferences.edit()
      .putStringSet("items", items.toSet())
      .apply()
  }
  fun loadItems(): List<String> {
    return sharedPreferences.getStringSet("items", emptySet())?.toList() ?: emptyList()
  }
}