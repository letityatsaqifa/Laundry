package com.letitya.laundryy.pelanggan

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
import com.letitya.laundryy.modeldata.ModelPelanggan

class TambahPelangganActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pelanggan")
    private lateinit var tvJudul: TextView
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNo: EditText
    private lateinit var etCabang: EditText
    private lateinit var btSimpan: Button

    private var idPelanggan: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pelanggan)

        tvJudul = findViewById(R.id.tvJudul)
        etNama = findViewById(R.id.etNama)
        etAlamat = findViewById(R.id.etAlamat)
        etNo = findViewById(R.id.etNo)
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

    private fun cekValidasi() {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val no = etNo.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = getString(R.string.validasi_nama_pelanggan)
            Toast.makeText(this, getString(R.string.validasi_nama_pelanggan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = getString(R.string.validasi_alamat_pelanggan)
            Toast.makeText(this, getString(R.string.validasi_alamat_pelanggan), Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (no.isEmpty()) {
            etNo.error = getString(R.string.validasi_no_pelanggan)
            Toast.makeText(this, getString(R.string.validasi_no_pelanggan), Toast.LENGTH_SHORT).show()
            etNo.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = getString(R.string.validasi_cabang_pelanggan)
            Toast.makeText(this, getString(R.string.validasi_cabang_pelanggan), Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }

        if (btSimpan.text.equals(getString(R.string.simpan))) {
            val pelangganBaru = myRef.push()
            val pelangganId = pelangganBaru.key
            val data = ModelPelanggan(
                pelangganId.toString(),
                etNama.text.toString(),
                etAlamat.text.toString(),
                etNo.text.toString(),
                timestamp = System.currentTimeMillis(),
                etCabang.text.toString(),
            )

            pelangganBaru.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        getString(R.string.sukse_simpan_pelanggan),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, getString(R.string.gagal_simpan), Toast.LENGTH_SHORT)
                        .show()
                }
        } else if (btSimpan.text.equals(getString(R.string.sunting))) {
            hidup()
            etNama.requestFocus()
            btSimpan.text = getString(R.string.perbarui)
        } else if (btSimpan.text.equals(getString(R.string.perbarui))) {
            update()
        }
    }

    private fun getData() {
        idPelanggan = intent.getStringExtra("idPelanggan") ?: ""
        val judul = intent.getStringExtra("judul")
        val nama = intent.getStringExtra("namaPelanggan")
        val alamat = intent.getStringExtra("alamatPelanggan")
        val hp = intent.getStringExtra("noHPPelanggan")
        val cabang = intent.getStringExtra("cabang")
        tvJudul.text = judul
        etNama.setText(nama)
        etAlamat.setText(alamat)
        etNo.setText(hp)
        etCabang.setText(cabang)
        if (tvJudul.text != getString(R.string.pelanggan_tambah)) {
            if (judul == "Edit Pelanggan") {
                mati()
                btSimpan.text = getString(R.string.sunting)
            }
        } else {
            hidup()
            etNama.requestFocus()
            btSimpan.text = getString(R.string.simpan)
        }
    }

    private fun mati() {
        etNama.isEnabled = false
        etAlamat.isEnabled = false
        etNo.isEnabled = false
        etCabang.isEnabled = false
    }

    private fun hidup() {
        etNama.isEnabled = true
        etAlamat.isEnabled = true
        etNo.isEnabled = true
        etCabang.isEnabled = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun update() {
        val pelangganRef = database.getReference("pelanggan").child(idPelanggan)
        val data = ModelPelanggan(
            idPelanggan,
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNo.text.toString(),
            System.currentTimeMillis(),
            etCabang.text.toString()
        )

        val updateData = mutableMapOf<String, Any>()
        updateData["namaPelanggan"] = data.namaPelanggan.toString()
        updateData["alamatPelanggan"] = data.alamatPelanggan.toString()
        updateData["noHPPelanggan"] = data.noHPPelanggan.toString()
        updateData["cabang"] = data.cabang.toString()

        pelangganRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Pelanggan Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Data Pelanggan Gagal Diperbarui", Toast.LENGTH_SHORT).show()
            }
    }
}