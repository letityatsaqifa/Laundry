package com.letitya.laundryy

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class LaundryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_laundry)
        val textGreeting: TextView = findViewById(R.id.tvSelamat)
        val textDate: TextView = findViewById(R.id.tvTgl)

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when {
            hour in 5..11 -> "Selamat Pagi, Letitya"
            hour in 12..14 -> "Selamat Siang, Letitya"
            hour in 15..17 -> "Selamat Sore, Letitya"
            else -> "Selamat Malam, Letitya"
        }

        textGreeting.text = greeting

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val currentDate = dateFormat.format(calendar.time)

        textDate.text = currentDate
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}