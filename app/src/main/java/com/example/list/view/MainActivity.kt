package com.example.list.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.list.R


class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    if (Build.VERSION.SDK_INT >= 33) {
      if (ContextCompat.checkSelfPermission(
          this,
          Manifest.permission.ACCESS_NOTIFICATION_POLICY
        ) == PackageManager.PERMISSION_GRANTED
      ) return
      val launcher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
      ) { _: Boolean? -> }
      launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    supportFragmentManager.beginTransaction()
      .add(R.id.container, ListFragment())
      .commit()
  }
}
