package com.example.vehicam.db

data class Recording(
    val video_id: String,
    val video_title: String,
    val video_description: String,
    val video_path: String,
    val video_timestamp: String
)
