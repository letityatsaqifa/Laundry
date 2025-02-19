package com.letitya.laundryy.pegawai

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
    lateinit var etJK: EditText
    lateinit var etAlamat: EditText
    lateinit var etNo: EditText
    lateinit var etJabatan: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_pegawai)

        tvJudul = findViewById(R.id.tvJudul)
        etNama = findViewById(R.id.etNama)
        etJK = findViewById(R.id.etJK)
        etAlamat = findViewById(R.id.etAlamat)
        etNo = findViewById(R.id.etNo)
        etJabatan = findViewById(R.id.etJabatan)
        etCabang = findViewById(R.id.etCabang)
        btSimpan = findViewById(R.id.btnSimpan)

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
        val jk = etJK.text.toString()
        val alamat = etAlamat.text.toString()
        val no = etNo.text.toString()
        val jabatan = etJabatan.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_nama_pegawai), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (jk.isEmpty()) {
            etJK.error = this.getString(R.string.validasi_jk_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_jk_pegawai), Toast.LENGTH_SHORT).show()
            etJK.requestFocus()
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
        if (jabatan.isEmpty()) {
            etJabatan.error = this.getString(R.string.validasi_jabatan_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_jabatan_pegawai), Toast.LENGTH_SHORT).show()
            etJabatan.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_cabang_pegawai), Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }

        val pegawaiBaru = myRef.push()
        val pegawaiId = pegawaiBaru.key ?: ""

        val data = ModelPegawai(
            pegawaiId,
            etNama.text.toString(),
            etJK.text.toString(),
            etAlamat.text.toString(),
            etNo.text.toString(),
            etJabatan.text.toString(),
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
    }
}
