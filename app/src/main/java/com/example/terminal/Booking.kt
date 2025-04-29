package com.example.terminal

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
    private val REQUEST_NOTIFICATION_PERMISSION = 1
    private val REQUEST_SMS_PERMISSION = 2

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var selectedTheatre: String
    private lateinit var ticketsSelected: String
    private lateinit var movieName: String

    private val SMS_SENT = "SMS_SENT"

    private val smsSentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (resultCode) {
                RESULT_OK -> Toast.makeText(this@Booking, "SMS Sent Successfully", Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(this@Booking, "Generic Failure", Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(this@Booking, "No Service", Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(this@Booking, "Null PDU", Toast.LENGTH_SHORT).show()
                SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(this@Booking, "Radio Off", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val prefs = getSharedPreferences("bookingData", MODE_PRIVATE)
        movieName = prefs.getString("movie", "Movie") ?: "Movie"
        ticketsSelected = prefs.getString("tickets", "0") ?: "0"
        selectedTheatre = prefs.getString("theatre", "Theatre") ?: "Theatre"
        val timing = prefs.getString("timing", "Timing") ?: "Timing"
        val poster = prefs.getInt("poster", R.drawable.ic_launcher_foreground)

        val textDetails = findViewById<TextView>(R.id.textDetails)
        val buttonConfirm = findViewById<Button>(R.id.buttonConfirm)
        val buttonDirections = findViewById<Button>(R.id.buttonDirections)
        val imagePoster = findViewById<ImageView>(R.id.imagePoster)

        textDetails.text = "Movie: $movieName\nShow: $timing\nTickets: $ticketsSelected\nTheatre: $selectedTheatre"
        imagePoster.setImageResource(poster)

        createNotificationChannel()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        buttonConfirm.setOnClickListener {
            sendNotification()
            sendSms()
        }

        buttonDirections.setOnClickListener {
            val mapsIntent = Intent(Intent.ACTION_VIEW)
            mapsIntent.data = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(selectedTheatre + ", Madurai"))
            startActivity(mapsIntent)
        }
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

            val sentIntent = PendingIntent.getBroadcast(this, 0, Intent(SMS_SENT), PendingIntent.FLAG_IMMUTABLE)
            smsManager.sendTextMessage(phoneNumber, null, message, sentIntent, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Booking Channel", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Channel for booking confirmations"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        registerReceiver(smsSentReceiver, IntentFilter(SMS_SENT), RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsSentReceiver)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSms()
            } else {
                Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
