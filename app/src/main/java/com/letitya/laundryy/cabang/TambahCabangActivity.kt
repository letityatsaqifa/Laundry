package com.letitya.laundryy.cabang

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
import com.letitya.laundryy.modeldata.ModelCabang

class TambahCabangActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("cabang")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etManager: EditText
    lateinit var etAlamat: EditText
    lateinit var etNo: EditText
    lateinit var btSimpan: Button

    var idCabang: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_cabang)

        tvJudul = findViewById(R.id.tvJudul)
        etNama = findViewById(R.id.etNama)
        etManager = findViewById(R.id.etManager)
        etAlamat = findViewById(R.id.etAlamat)
        etNo = findViewById(R.id.etNo)
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
        val manager = etManager.text.toString()
        val alamat = etAlamat.text.toString()
        val no = etNo.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_pelanggan)
            Toast.makeText(this@TambahCabangActivity, this.getString(R.string.validasi_nama_pelanggan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (manager.isEmpty()) {
            etManager.error = this.getString(R.string.validasiManager)
            Toast.makeText(this@TambahCabangActivity, this.getString(R.string.validasiManager), Toast.LENGTH_SHORT).show()
            etManager.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = this.getString(R.string.validasi_alamat_pelanggan)
            Toast.makeText(this@TambahCabangActivity, this.getString(R.string.validasi_alamat_pelanggan), Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (no.isEmpty()) {
            etNo.error = this.getString(R.string.validasi_no_pelanggan)
            Toast.makeText(this@TambahCabangActivity, this.getString(R.string.validasi_no_pelanggan), Toast.LENGTH_SHORT).show()
            etNo.requestFocus()
            return
        }

        if (btSimpan.text.equals(this.getString(R.string.simpan))){
            val cabangBaru = myRef.push()
            val cabangId = cabangBaru.key
            val data = ModelCabang(
                cabangId.toString(),
                etNama.text.toString(),
                etManager.text.toString(),
                etAlamat.text.toString(),
                etNo.text.toString(),
                timestamp = System.currentTimeMillis(),
            )

            cabangBaru.setValue(data)
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
        idCabang = intent.getStringExtra("idCabang").toString()
        val judul = intent.getStringExtra("judul")
        val nama = intent.getStringExtra("namaCabang")
        val manager = intent.getStringExtra("managerCabang")
        val alamat = intent.getStringExtra("alamatCabang")
        val hp = intent.getStringExtra("noHP")
        tvJudul.text = judul
        etNama.setText(nama)
        etManager.setText(manager)
        etAlamat.setText(alamat)
        etNo.setText(hp)
        if(!tvJudul.text.equals(this.getString(R.string.cabang_tambah))){
            if(judul.equals("Edit Cabang")){
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
        etManager.isEnabled = false
        etAlamat.isEnabled = false
        etNo.isEnabled = false
    }

    fun hidup(){
        etNama.isEnabled = true
        etManager.isEnabled = true
        etAlamat.isEnabled = true
        etNo.isEnabled = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(){
        val cabangRef = database.getReference("cabang").child(idCabang)
        val data = ModelCabang(
            idCabang,
            etNama.text.toString(),
            etManager.text.toString(),
            etAlamat.text.toString(),
            etNo.text.toString(),
            System.currentTimeMillis()
        )

        val updateData = mutableMapOf<String, Any>()
        updateData["namaCabang"] = data.namaCabang.toString()
        updateData["managerCabang"] = data.managerCabang.toString()
        updateData["alamatCabang"] = data.alamatCabang.toString()
        updateData["noHP"] = data.noHP.toString()

        cabangRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(this@TambahCabangActivity, "Data Cabang Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@TambahCabangActivity, "Data Cabang Gagal Diperbarui", Toast.LENGTH_SHORT).show()
            }
    }
}