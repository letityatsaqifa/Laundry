package com.letitya.laundryy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.letitya.laundryy.layanan.DataLayananActivity
import com.letitya.laundryy.pegawai.DataPegawaiActivity
import com.letitya.laundryy.pelanggan.DataPelangganActivity
import java.text.SimpleDateFormat
import java.util.*

class LaundryActivity : AppCompatActivity() {
    lateinit var Pelanggan : ImageView
    lateinit var Pegawai : ImageView
    lateinit var Layanan : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_laundry)
        val textGreeting: TextView = findViewById(R.id.tvSelamat)
        val textDate: TextView = findViewById(R.id.tvTgl)

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when {
            hour in 5..11 -> getString(R.string.selamatPagi)
            hour in 12..14 -> getString(R.string.selamatSiang)
            hour in 15..17 -> getString(R.string.selamatSore)
            else -> getString(R.string.selamatMalam)
        }

        textGreeting.text = greeting

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)
        textDate.text = currentDate

        textDate.text = currentDate
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Pelanggan=findViewById(R.id.ivPelanggan)
        Pegawai=findViewById(R.id.ivPegawai)
        Layanan=findViewById(R.id.ivLayanan)

        Pelanggan.setOnClickListener {
            val intent = Intent(this,DataPelangganActivity::class.java)
            startActivity(intent)
        }

        Pegawai.setOnClickListener {
            val intent = Intent(this,DataPegawaiActivity::class.java)
            startActivity(intent)
        }

        Layanan.setOnClickListener {
            val intent = Intent(this,DataLayananActivity::class.java)
            startActivity(intent)
        }

    }
}