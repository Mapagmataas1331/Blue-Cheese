package com.example.list.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.list.R

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    supportFragmentManager.beginTransaction()
      .add(R.id.container, ListFragment())
      .commit()
  }
}
