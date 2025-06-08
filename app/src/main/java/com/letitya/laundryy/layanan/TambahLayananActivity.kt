package com.letitya.laundryy.layanan

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.letitya.laundryy.R
import com.letitya.laundryy.modeldata.ModelLayanan

class TambahLayananActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etHarga: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button

    var idLayanan: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_layanan)
        tvJudul = findViewById(R.id.tvJudul)
        etNama = findViewById(R.id.etNama)
        etHarga = findViewById(R.id.etHarga)
        etCabang = findViewById(R.id.etCabang)
        btSimpan = findViewById(R.id.btnSimpan)

        getData()

        btSimpan.setOnClickListener {
            cekValidasi()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val harga = etHarga.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_pelanggan)
            Toast.makeText(this@TambahLayananActivity, this.getString(R.string.validasi_nama_pelanggan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (harga.isEmpty()) {
            etHarga.error = this.getString(R.string.validasi_harga)
            Toast.makeText(this@TambahLayananActivity, this.getString(R.string.validasi_alamat_pelanggan), Toast.LENGTH_SHORT).show()
            etHarga.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_pelanggan)
            Toast.makeText(this@TambahLayananActivity, this.getString(R.string.validasi_cabang_pelanggan), Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }

        if (btSimpan.text.equals(this.getString(R.string.simpan))){
            val layananBaru = myRef.push()
            val layananId = layananBaru.key
            val data = ModelLayanan(
                layananId.toString(),
                etNama.text.toString(),
                etHarga.text.toString(),
                etCabang.text.toString(),
            )

            layananBaru.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(this, this.getString(R.string.sukse_simpan_pelanggan), Toast .LENGTH_SHORT ). show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, this.getString(R.string.gagal_simpan), Toast .LENGTH_SHORT ). show()
                }
        } else if (btSimpan.text.equals(this.getString(R.string.sunting))){
            hidup()
            etNama.requestFocus()
            btSimpan.text = this.getString(R.string.perbarui)
        } else if (btSimpan.text.equals(this.getString(R.string.perbarui))){
            update()
        }
    }

    fun getData(){
        idLayanan = intent.getStringExtra("idLayanan").toString()
        val judul = intent.getStringExtra("judul")
        val nama = intent.getStringExtra("namaLayanan")
        val harga = intent.getStringExtra("hargaLayanan")
        val cabang = intent.getStringExtra("cabangLayanan")
        tvJudul.text = judul
        etNama.setText(nama)
        etHarga.setText(harga)
        etCabang.setText(cabang)
        if(!tvJudul.text.equals(this.getString(R.string.layanan_tambah))){
            if(judul.equals("Edit Layanan")){
                mati()
                btSimpan.text = getString(R.string.sunting)
            }
        } else {
            hidup()
            etNama.requestFocus()
            btSimpan.text = getString(R.string.simpan)
        }
    }

    fun mati(){
        etNama.isEnabled = false
        etHarga.isEnabled = false
        etCabang.isEnabled = false
    }

    fun hidup(){
        etNama.isEnabled = true
        etHarga.isEnabled = true
        etCabang.isEnabled = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(){
        val layananRef = database.getReference("layanan").child(idLayanan)
        val data = ModelLayanan(
            idLayanan,
            etNama.text.toString(),
            etHarga.text.toString(),
            etCabang.text.toString()
        )

        val updateData = mutableMapOf<String, Any>()
        updateData["namaLayanan"] = data.namaLayanan.toString()
        updateData["hargaLayanan"] = data.hargaLayanan.toString()
        updateData["cabangLayanan"] = data.cabangLayanan.toString()

        layananRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(this@TambahLayananActivity, "Data Layanan Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@TambahLayananActivity, "Data Layanan Gagal Diperbarui", Toast.LENGTH_SHORT).show()
            }
    }
}