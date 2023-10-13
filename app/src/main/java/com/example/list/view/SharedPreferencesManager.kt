package com.example.list.view

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
  private val sharedPreferences: SharedPreferences =
    context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

  fun saveItems(items: List<String>) {
    val editor = sharedPreferences.edit()
    editor.putStringSet("items", items.toSet())
    editor.apply()
  }

  fun loadItems(): List<String> {
    val savedItems = sharedPreferences.getStringSet("items", setOf())
    return savedItems?.toList() ?: emptyList()
  }
}
