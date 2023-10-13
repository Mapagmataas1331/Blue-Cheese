package com.example.list.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
