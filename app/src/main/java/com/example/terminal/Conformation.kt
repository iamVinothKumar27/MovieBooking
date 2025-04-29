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

        val textViewConfirmation = findViewById<TextView>(R.id.textViewConfirmation)
        val imagePosterConfirm = findViewById<ImageView>(R.id.imagePosterConfirm)

        if (booking != null) {
            textViewConfirmation.text = "🎬 Movie: ${booking.movie}\n🕓 Timing: ${booking.timing}\n🎟️ Tickets: ${booking.tickets}\n📍 Theatre: ${booking.theatre}"
            imagePosterConfirm.setImageResource(booking.poster)
        } else {
            Toast.makeText(this, "No booking found", Toast.LENGTH_SHORT).show()
        }
    }
}
