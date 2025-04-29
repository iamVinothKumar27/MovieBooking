package com.example.terminal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookingDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "booking.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE bookings (id INTEGER PRIMARY KEY AUTOINCREMENT, movie TEXT, timing TEXT, tickets TEXT, theatre TEXT, poster INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS bookings")
        onCreate(db)
    }

    fun insertBooking(movie: String, timing: String, tickets: String, theatre: String, poster: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("movie", movie)
            put("timing", timing)
            put("tickets", tickets)
            put("theatre", theatre)
            put("poster", poster)
        }
        db.insert("bookings", null, values)
        db.close()
    }

    fun getLastBooking(): BookingModel? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM bookings ORDER BY id DESC LIMIT 1", null)
        var booking: BookingModel? = null
        if (cursor.moveToFirst()) {
            booking = BookingModel(
                movie = cursor.getString(cursor.getColumnIndexOrThrow("movie")),
                timing = cursor.getString(cursor.getColumnIndexOrThrow("timing")),
                tickets = cursor.getString(cursor.getColumnIndexOrThrow("tickets")),
                theatre = cursor.getString(cursor.getColumnIndexOrThrow("theatre")),
                poster = cursor.getInt(cursor.getColumnIndexOrThrow("poster"))
            )
        }
        cursor.close()
        db.close()
        return booking
    }
}

data class BookingModel(
    val movie: String,
    val timing: String,
    val tickets: String,
    val theatre: String,
    val poster: Int
)
