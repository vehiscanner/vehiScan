package com.example.vehicam.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.vehicam.db.Recording

class DBHelper (context: Context) : SQLiteOpenHelper(context, "vehiscan.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
       db?.execSQL("""
           CREATE TABLE recordings (
            video_id VARCHAR(100) NOT NULL PRIMARY KEY,
            video_title VARCHAR(100) NOT NULL,
            video_description VARCHAR(250) NOT NULL,
            video_path VARCHAR(250) NOT NULL,
            video_timestamp DATETIME
        )
       """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS recordings")
        onCreate(db)
    }

    fun addRecording(recording: Recording): Long {
        val db = this.writableDatabase
        val values = ContentValues()
            values.put("video_id", recording.video_id)
            values.put("video_title", recording.video_title)
            values.put("video_description", recording.video_description)
            values.put("video_path", recording.video_path)
            values.put("video_timestamp", recording.video_timestamp)

        val id = db.insert("recordings", null, values)
        db.close()
        return id
    }
}