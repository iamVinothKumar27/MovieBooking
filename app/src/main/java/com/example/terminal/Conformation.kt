package com.example.terminal

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Conformation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conformation)

        val dbHelper = BookingDatabaseHelper(this)
        val booking = dbHelper.getLastBooking()

        val textMovie = findViewById<TextView>(R.id.textMovie)
        val textTiming = findViewById<TextView>(R.id.textTiming)
        val textTickets = findViewById<TextView>(R.id.textTickets)
        val textTheatre = findViewById<TextView>(R.id.textTheatre)
        val imagePosterConfirm = findViewById<ImageView>(R.id.imagePosterConfirm)

        if (booking != null) {
            textMovie.text = "Movie: ${booking.movie}"
            textTiming.text = "Timing: ${booking.timing}"
            textTickets.text = "Tickets: ${booking.tickets}"
            textTheatre.text = "Theatre: ${booking.theatre}"
            imagePosterConfirm.setImageResource(booking.poster)
        } else {
            Toast.makeText(this, "No booking found", Toast.LENGTH_SHORT).show()
        }
    }
}
