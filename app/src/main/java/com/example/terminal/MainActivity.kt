package com.example.terminal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val theatres = arrayOf(
        "INOX Madurai", "Vetri Cinemas Villapuram", "Vetri Cinemas Mattuthavani",
        "Gopuram Cinemas", "Priya Complex", "Ganesh Theatre",
        "Jazz & Arsh Cinemas", "Ritz Banu Cinemas", "Shanmuga Theatre", "Thanga Regal"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val spinnerShowTimings = findViewById<Spinner>(R.id.spinnerShowTimings)
        val spinnerTickets = findViewById<Spinner>(R.id.spinnerTickets)
        val spinnerTheatres = findViewById<Spinner>(R.id.spinnerTheatres)

        val spinnerShowTimings2 = findViewById<Spinner>(R.id.spinnerShowTimings2)
        val spinnerTickets2 = findViewById<Spinner>(R.id.spinnerTickets2)
        val spinnerTheatres2 = findViewById<Spinner>(R.id.spinnerTheatres2)

        val timings = arrayOf("10:00 AM", "1:00 PM", "4:00 PM", "7:00 PM", "10:00 PM")
        val tickets = (1..10).map { it.toString() }.toTypedArray()

        spinnerShowTimings.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timings)
        spinnerTickets.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tickets)
        spinnerTheatres.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, theatres)

        spinnerShowTimings2.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timings)
        spinnerTickets2.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tickets)
        spinnerTheatres2.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, theatres)

        val textView2 = findViewById<TextView>(R.id.textView2)
        val textView3 = findViewById<TextView>(R.id.textView3)

        findViewById<Button>(R.id.buttonBook).setOnClickListener {
            saveBooking(textView2.text.toString(), spinnerShowTimings, spinnerTickets, spinnerTheatres, R.drawable.d)
        }

        findViewById<Button>(R.id.buttonBook2).setOnClickListener {
            saveBooking(textView3.text.toString(), spinnerShowTimings2, spinnerTickets2, spinnerTheatres2, R.drawable.goat)
        }
    }

    private fun saveBooking(movie: String, timingSpinner: Spinner, ticketsSpinner: Spinner, theatreSpinner: Spinner, poster: Int) {
        val timing = timingSpinner.selectedItem.toString()
        val tickets = ticketsSpinner.selectedItem.toString()
        val theatre = theatreSpinner.selectedItem.toString()

        // Save to SharedPreferences
        val prefs = getSharedPreferences("bookingData", MODE_PRIVATE)
        with(prefs.edit()) {
            putString("movie", movie)
            putString("timing", timing)
            putString("tickets", tickets)
            putString("theatre", theatre)
            putInt("poster", poster)
            apply()
        }

        val dbHelper = BookingDatabaseHelper(this)
        dbHelper.insertBooking(movie, timing, tickets, theatre, poster)

        startActivity(Intent(this, Booking::class.java))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_exit -> {
                finishAffinity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
