package com.letitya.laundryy

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.letitya.laundryy.adapter.PilihLayananTambahanAdapter
import com.letitya.laundryy.modeldata.ModelTambahan

class TransaksiActivity : AppCompatActivity() {

    private lateinit var btnTransaksi_PilihPelanggan: Button
    private lateinit var btnTransaksi_PilihLayanan: Button
    private lateinit var btnTambahan: Button
    private lateinit var btnProses: Button
    private lateinit var tvPelangganNama: TextView
    private lateinit var tvPelangganNoHP: TextView
    private lateinit var tvLayananNama: TextView
    private lateinit var tvLayananHarga: TextView
    private lateinit var tvLayananTambahan: TextView
    private lateinit var rvLayananTambahan: RecyclerView
    private val dataList = mutableListOf<ModelTambahan>()

    private val pilihPelanggan = 1
    private val pilihLayanan = 2
    private val pilihLayananTambahan = 3

    private var idPelanggan = ""
    private var idCabang = ""
    private var namaPelanggan = ""
    private var noHP = ""
    private var idLayanan = ""
    private var namaLayanan = ""
    private var hargaLayanan = ""
    private var idPegawai = ""
    private lateinit var sharedPref: SharedPreferences
    private lateinit var adapter: PilihLayananTambahanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaksi)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        val namaPegawai = sharedPref.getString("namaPegawai", "") ?: ""
        idCabang = sharedPref.getString("idCabang", "") ?: ""
        idPegawai = sharedPref.getString("idPegawai", "") ?: ""

        initView()
        FirebaseApp.initializeApp(this)

        adapter = PilihLayananTambahanAdapter(ArrayList(dataList)).apply {
            setOnItemClickListener { _, position ->
                dataList.removeAt(position)
                updatedata(dataList)
            }
        }

        rvLayananTambahan.adapter = adapter

        rvLayananTambahan.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = false
            reverseLayout = false
        }
        rvLayananTambahan.setHasFixedSize(true)

        btnTransaksi_PilihPelanggan.setOnClickListener {
            startActivityForResult(Intent(this, PilihPelangganActivity::class.java), pilihPelanggan)
        }

        btnTransaksi_PilihLayanan.setOnClickListener {
            startActivityForResult(Intent(this, PilihLayananActivity::class.java), pilihLayanan)
        }

        btnTambahan.setOnClickListener {
            startActivityForResult(Intent(this, PilihLayananTambahanActivity::class.java), pilihLayananTambahan)
        }

        btnProses.setOnClickListener {
            if (namaPelanggan.isNotEmpty() && noHP.isNotEmpty() && namaLayanan.isNotEmpty()) {
                val intent = Intent(this, ProsesActivity::class.java)
                intent.putExtra("namaPegawai", namaPegawai)
                intent.putExtra("idPelanggan", idPelanggan)
                intent.putExtra("namaPelanggan", namaPelanggan)
                intent.putExtra("noHP", noHP)
                intent.putExtra("idLayanan", idLayanan)
                intent.putExtra("namaLayanan", namaLayanan)
                intent.putExtra("hargaLayanan", hargaLayanan)
                intent.putExtra("idPegawai", idPegawai)
                intent.putExtra("idCabang", idCabang)
                intent.putExtra("dataTambahan", ArrayList(dataList))
                startActivity(intent)
            } else {
                if (namaPelanggan.isEmpty()) {
                    Toast.makeText(this, getString(R.string.pilih_pelanggan), Toast.LENGTH_SHORT).show()
                    startActivityForResult(Intent(this, PilihPelangganActivity::class.java), pilihPelanggan)
                }
                if (namaLayanan.isEmpty()) {
                    Toast.makeText(this, getString(R.string.pilih_layanan), Toast.LENGTH_SHORT).show()
                    startActivityForResult(Intent(this, PilihLayananActivity::class.java), pilihLayanan)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initView() {
        btnTransaksi_PilihPelanggan = findViewById(R.id.btnTransaksi_PilihPelanggan)
        btnTransaksi_PilihLayanan = findViewById(R.id.btnTransaksi_PilihLayanan)
        btnTambahan = findViewById(R.id.btnTambahan)
        btnProses = findViewById(R.id.btnProses)
        tvPelangganNama = findViewById(R.id.tvTransaksi_NamaPelanggan)
        tvPelangganNoHP = findViewById(R.id.tvTransaksi_no)
        tvLayananNama = findViewById(R.id.tvTransaksi_NamaLayanan)
        tvLayananHarga = findViewById(R.id.tvTransaksi_Harga)
        tvLayananTambahan = findViewById(R.id.tvTransaksi_LayananTambahan)
        rvLayananTambahan = findViewById(R.id.rvTransaksi_LayananTambahan)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pilihPelanggan) {
            if (resultCode == RESULT_OK && data != null) {
                idPelanggan = data.getStringExtra("idPelanggan").toString()
                val nama = data.getStringExtra("namaPelanggan")
                val nomorHP = data.getStringExtra("noHPPelanggan")

                tvPelangganNama.text = "${getString(R.string.namapelanggan)} : $nama"
                tvPelangganNoHP.text = "${getString(R.string.noPelanggan)} : $nomorHP"
                namaPelanggan = nama.toString()
                noHP = nomorHP.toString()
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.batal_memilih_pelanggan), Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == pilihLayanan) {
            if (resultCode == RESULT_OK && data != null) {
                idLayanan = data.getStringExtra("idLayanan").toString()
                val nama = data.getStringExtra("nama")
                val harga = data.getStringExtra("harga")

                tvLayananNama.text = "${getString(R.string.namaLayanan)} : $nama"
                tvLayananHarga.text = "${getString(R.string.hargaLayanan)} : $harga"
                namaLayanan = nama.toString()
                hargaLayanan = harga.toString()
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.batal_memilih_layanan), Toast.LENGTH_SHORT).show()
            }
        }
        // Di dalam onActivityResult:
        if (requestCode == pilihLayananTambahan) {
            if (resultCode == RESULT_OK && data != null) {
                val idTambahan = data.getStringExtra("idTambahan").orEmpty()
                val nama = data.getStringExtra("namaTambahan").orEmpty()
                val harga = data.getStringExtra("harga").orEmpty()

                dataList.add(
                    ModelTambahan(
                        idTambahan = idTambahan,
                        namaTambahan = nama,
                        harga = harga
                    )
                )

                adapter.updatedata(dataList) // <- Pastikan ini dipanggil
            }
        }
    }
}