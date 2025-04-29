package com.example.terminal

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class Booking : AppCompatActivity() {

    private val CHANNEL_ID = "booking_channel"
    private val NOTIFICATION_ID = 101

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var selectedTheatre: String
    private lateinit var ticketsSelected: String
    private lateinit var movieName: String
    private lateinit var timing: String
    private var poster: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val prefs = getSharedPreferences("bookingData", MODE_PRIVATE)
        movieName = prefs.getString("movie", "Movie") ?: "Movie"
        ticketsSelected = prefs.getString("tickets", "0") ?: "0"
        selectedTheatre = prefs.getString("theatre", "Theatre") ?: "Theatre"
        timing = prefs.getString("timing", "Timing") ?: "Timing"
        poster = prefs.getInt("poster", R.drawable.ic_launcher_foreground)

        val textDetails = findViewById<TextView>(R.id.textDetails)
        val buttonConfirm = findViewById<Button>(R.id.buttonConfirm)
        val buttonDirections = findViewById<Button>(R.id.buttonDirections)
        val buttonSeeConfirmation = findViewById<Button>(R.id.buttonSeeConfirmation)
        val imagePoster = findViewById<ImageView>(R.id.imagePoster)

        textDetails.text = "Movie: $movieName\nShow: $timing\nTickets: $ticketsSelected\nTheatre: $selectedTheatre"
        imagePoster.setImageResource(poster)

        createNotificationChannel()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        buttonConfirm.setOnClickListener {
            saveToDatabase()
            sendNotification()
            sendSms()
            Toast.makeText(this, "Booking Confirmed and Saved!", Toast.LENGTH_SHORT).show()
        }

        buttonDirections.setOnClickListener {
            val mapsIntent = Intent(Intent.ACTION_VIEW)
            mapsIntent.data = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(selectedTheatre + ", Madurai"))
            startActivity(mapsIntent)
        }

        buttonSeeConfirmation.setOnClickListener {
            startActivity(Intent(this, Conformation::class.java))
        }
    }

    private fun saveToDatabase() {
        val dbHelper = BookingDatabaseHelper(this)
        dbHelper.insertBooking(movieName, timing, ticketsSelected, selectedTheatre, poster)
    }

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Booking Confirmed")
            .setContentText("$ticketsSelected ticket(s) booked at $selectedTheatre")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun sendSms() {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            val message = "$ticketsSelected ticket(s) booked for $movieName at $selectedTheatre.\nDirections: https://www.google.com/maps/search/?api=1&query=" + Uri.encode(selectedTheatre + ", Madurai")
            val phoneNumber = "6380382880" // replace with your number

            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Booking Channel", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
