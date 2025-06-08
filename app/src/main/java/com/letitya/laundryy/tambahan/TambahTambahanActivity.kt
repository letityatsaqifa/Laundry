package com.letitya.laundryy.tambahan

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
import com.letitya.laundryy.modeldata.ModelTambahan

class TambahTambahanActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etHarga: EditText
    lateinit var etCabang: EditText
    lateinit var etStatus: EditText
    lateinit var btSimpan: Button

    var idTambahan: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_tambahan)

        tvJudul = findViewById(R.id.tvJudul)
        etNama = findViewById(R.id.etNama)
        etHarga = findViewById(R.id.etHarga)
        etCabang = findViewById(R.id.etCabang)
        etStatus = findViewById(R.id.etStatus)
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
        val status = etStatus.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_pelanggan)
            Toast.makeText(this@TambahTambahanActivity, this.getString(R.string.validasi_nama_pelanggan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (harga.isEmpty()) {
            etHarga.error = this.getString(R.string.validasi_harga)
            Toast.makeText(this@TambahTambahanActivity, this.getString(R.string.validasi_harga), Toast.LENGTH_SHORT).show()
            etHarga.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_pelanggan)
            Toast.makeText(this@TambahTambahanActivity, this.getString(R.string.validasi_cabang_pelanggan), Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        if (status.isEmpty()) {
            etStatus.error = this.getString(R.string.validasi_status)
            Toast.makeText(this@TambahTambahanActivity, this.getString(R.string.validasi_status), Toast.LENGTH_SHORT).show()
            etStatus.requestFocus()
            return
        }

        if (btSimpan.text.equals(this.getString(R.string.simpan))){
            val tambahanBaru = myRef.push()
            val tambahanId = tambahanBaru.key
            val data = ModelTambahan(
                tambahanId.toString(),
                etNama.text.toString(),
                etHarga.text.toString(),
                etCabang.text.toString(),
                etStatus.text.toString(),
            )

            tambahanBaru.setValue(data)
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
        idTambahan = intent.getStringExtra("idTambahan").toString()
        val judul = intent.getStringExtra("judul")
        val nama = intent.getStringExtra("namaTambahan")
        val harga = intent.getStringExtra("harga")
        val cabang = intent.getStringExtra("cabang")
        val status = intent.getStringExtra("status")
        tvJudul.text = judul
        etNama.setText(nama)
        etHarga.setText(harga)
        etStatus.setText(status)
        etCabang.setText(cabang)
        if(!tvJudul.text.equals(this.getString(R.string.tambahan_tambah))){
            if(judul.equals("Edit Tambahan")){
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
        etStatus.isEnabled = false
        etCabang.isEnabled = false
    }

    fun hidup(){
        etNama.isEnabled = true
        etHarga.isEnabled = true
        etStatus.isEnabled = true
        etCabang.isEnabled = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(){
        val tambahanRef = database.getReference("tambahan").child(idTambahan)
        val data = ModelTambahan(
            idTambahan,
            etNama.text.toString(),
            etHarga.text.toString(),
            etStatus.text.toString(),
            etCabang.text.toString()
        )

        val updateData = mutableMapOf<String, Any>()
        updateData["namaTambahan"] = data.namaTambahan.toString()
        updateData["harga"] = data.harga.toString()
        updateData["status"] = data.status.toString()
        updateData["cabang"] = data.cabang.toString()

        tambahanRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(this@TambahTambahanActivity, "Data Tambahan Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@TambahTambahanActivity, "Data Tambahan Gagal Diperbarui", Toast.LENGTH_SHORT).show()
            }
    }
}