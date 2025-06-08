package com.letitya.laundryy

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.letitya.laundryy.akun.AkunActivity
import com.letitya.laundryy.cabang.DataCabangActivity
import com.letitya.laundryy.laporan.DataLaporanActivity
import com.letitya.laundryy.layanan.DataLayananActivity
import com.letitya.laundryy.modeldata.ModelLaporan
import com.letitya.laundryy.pegawai.DataPegawaiActivity
import com.letitya.laundryy.pelanggan.DataPelangganActivity
import com.letitya.laundryy.tambahan.DataTambahanActivity
import java.text.SimpleDateFormat
import java.util.*
import java.text.DecimalFormatSymbols

class LaundryActivity : AppCompatActivity() {
    private lateinit var tvHarga: TextView
    private lateinit var sharedPref: SharedPreferences
    private val database = Firebase.database
    private val laporanRef = database.getReference("laporan")
    private var totalIncome = 0
    private lateinit var currentDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_laundry)

        tvHarga = findViewById(R.id.tvHarga)
        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)

        if (sharedPref.getString("user_id", null) == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        init()
        setupTransactionListener()
        Reset()
    }

    private fun init() {
        val namaPegawai = sharedPref.getString("namaPegawai", "")
        val userRole = sharedPref.getString("user_role", "")
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

        textGreeting.text = if (namaPegawai.isNullOrEmpty()) {
            greeting
        } else {
            "$greeting, $namaPegawai"
        }

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        currentDate = dateFormat.format(calendar.time)
        textDate.text = currentDate

        if (userRole == "Pegawai" || userRole == "pegawai") {
            findViewById<androidx.cardview.widget.CardView>(R.id.Pegawaicv).visibility = View.GONE
            findViewById<androidx.cardview.widget.CardView>(R.id.Cabangcv).visibility = View.GONE
        } else {
            findViewById<ImageView>(R.id.ivPegawai).setOnClickListener {
                startActivity(Intent(this, DataPegawaiActivity::class.java))
            }
            findViewById<ImageView>(R.id.ivCabang).setOnClickListener {
                startActivity(Intent(this, DataCabangActivity::class.java))
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.ivPelanggan).setOnClickListener {
            startActivity(Intent(this, DataPelangganActivity::class.java))
        }
        findViewById<ImageView>(R.id.ivPegawai).setOnClickListener {
            startActivity(Intent(this, DataPegawaiActivity::class.java))
        }
        findViewById<ImageView>(R.id.ivLayanan).setOnClickListener {
            startActivity(Intent(this, DataLayananActivity::class.java))
        }
        findViewById<ImageView>(R.id.ivTambahan).setOnClickListener {
            startActivity(Intent(this, DataTambahanActivity::class.java))
        }
        findViewById<ImageView>(R.id.ivCabang).setOnClickListener {
            startActivity(Intent(this, DataCabangActivity::class.java))
        }
        findViewById<ImageView>(R.id.ivTransaksi).setOnClickListener {
            startActivity(Intent(this, TransaksiActivity::class.java))
        }
        findViewById<ImageView>(R.id.ivLaporan).setOnClickListener {
            startActivity(Intent(this, DataLaporanActivity::class.java))
        }
        findViewById<ImageView>(R.id.ivAkun).setOnClickListener {
            val userId = sharedPref.getString("user_id", "")
            if (userId.isNullOrEmpty()) {
                toast("Silakan login kembali")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, AkunActivity::class.java).apply {
                    putExtra("userId", userId)
                })
            }
        }
    }

    private fun setupTransactionListener() {
        laporanRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalIncome = 0
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                for (data in snapshot.children) {
                    val laporan = data.getValue(ModelLaporan::class.java)
                    laporan?.let {
                        if (it.tanggal.startsWith(today) && it.status == "Sudah Dibayar") {
                            try {
                                val amount = parseRupiah(it.totalBayar)
                                totalIncome += amount
                            } catch (e: Exception) {
                                Toast.makeText(this@LaundryActivity,
                                    "Error parsing: ${it.totalBayar}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                tvHarga.text = formatRupiah(totalIncome)
                sharedPref.edit().putInt("daily_income_$currentDate", totalIncome).apply()
            }

            override fun onCancelled(error: DatabaseError) {
                toast("Gagal memuat transaksi: ${error.message}")
            }
        })
    }

    private fun formatRupiah(amount: Int): String {
        return try {
            val format = java.text.DecimalFormat("#,###", DecimalFormatSymbols(Locale.getDefault()).apply {
                groupingSeparator = '.'
                decimalSeparator = ','
            })
            "ᴿᵖ${format.format(amount)},00"
        } catch (e: Exception) {
            "ᴿᵖ0,00"
        }
    }

    private fun parseRupiah(rupiahString: String): Int {
        try {
            val cleanString = rupiahString
                .replace("Rp", "")
                .replace(" ", "")
                .replace(".", "")
                .split(",")[0]
                .trim()

            return cleanString.toIntOrNull() ?: 0
        } catch (e: Exception) {
            throw Exception("Format mata uang tidak valid: $rupiahString")
        }
    }

    private fun Reset() {
        val lastAccessDate = sharedPref.getString("last_access_date", "")
        val today = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())

        if (lastAccessDate != today) {
            sharedPref.edit().apply {
                putString("last_access_date", today)
                putInt("daily_income_$today", 0)
                apply()
            }
            totalIncome = 0
            tvHarga.text = formatRupiah(0)
        } else {
            totalIncome = sharedPref.getInt("daily_income_$today", 0)
            tvHarga.text = formatRupiah(totalIncome)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}