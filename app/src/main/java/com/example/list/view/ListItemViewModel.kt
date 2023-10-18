package com.example.list.view

import kotlinx.serialization.Serializable

@Serializable
data class ListItemViewModel(
  val title: String,
  val description: String
)