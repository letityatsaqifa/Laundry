package com.letitya.laundryy.pegawai

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
import com.letitya.laundryy.modeldata.ModelPegawai

class TambahPegawaiActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNo: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button

    var idPegawai: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pegawai)

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

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val no = etNo.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_nama_pegawai), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = this.getString(R.string.validasi_alamat_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_pegawai), Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (no.isEmpty()) {
            etNo.error = this.getString(R.string.validasi_no_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_no_pegawai), Toast.LENGTH_SHORT).show()
            etNo.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_cabang_pegawai), Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }

        if (btSimpan.text.equals(this.getString(R.string.simpan))){

            val pegawaiBaru = myRef.push()
            val pegawaiId = pegawaiBaru.key ?: ""

            val data = ModelPegawai(
                pegawaiId,
                etNama.text.toString(),
                etAlamat.text.toString(),
                etNo.text.toString(),
                timestamp = System.currentTimeMillis(),
                etCabang.text.toString(),
            )

            pegawaiBaru.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(this, this.getString(R.string.sukse_simpan_pelanggan), Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, this.getString(R.string.gagal_simpan), Toast.LENGTH_SHORT).show()
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
        idPegawai = intent.getStringExtra("idPegawai").toString()
        val judul = intent.getStringExtra("judul")
        val nama = intent.getStringExtra("namaPegawai")
        val alamat = intent.getStringExtra("alamatPegawai")
        val hp = intent.getStringExtra("noHPPegawai")
        val cabang = intent.getStringExtra("idCabang")
        tvJudul.text = judul
        etNama.setText(nama)
        etAlamat.setText(alamat)
        etNo.setText(hp)
        etCabang.setText(cabang)
        if(!tvJudul.text.equals(this.getString(R.string.pegawai_tambah))){
            if(judul.equals("Edit Pegawai")){
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
        etAlamat.isEnabled = false
        etNo.isEnabled = false
        etCabang.isEnabled = false
    }

    fun hidup(){
        etNama.isEnabled = true
        etAlamat.isEnabled = true
        etNo.isEnabled = true
        etCabang.isEnabled = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(){
        val pegawaiRef = database.getReference("pegawai").child(idPegawai)
        val data = ModelPegawai(
            idPegawai,
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNo.text.toString(),
            System.currentTimeMillis(),
            etCabang.text.toString()
        )

        val updateData = mutableMapOf<String, Any>()
        updateData["namaPegawai"] = data.namaPegawai.toString()
        updateData["alamatPegawai"] = data.alamatPegawai.toString()
        updateData["noHPPegawai"] = data.noHPPegawai.toString()
        updateData["cabangPegawai"] = data.cabangPegawai.toString()

        pegawaiRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(this@TambahPegawaiActivity, "Data Pegawai Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@TambahPegawaiActivity, "Data Pegawai Gagal Diperbarui", Toast.LENGTH_SHORT).show()
            }
    }
}
