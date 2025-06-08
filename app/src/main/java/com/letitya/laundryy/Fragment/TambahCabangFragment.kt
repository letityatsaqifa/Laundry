package com.letitya.laundryy.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.letitya.laundryy.R
import com.letitya.laundryy.cabang.DataCabangActivity
import com.letitya.laundryy.modeldata.ModelCabang

class TambahCabangFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("cabang")
    private lateinit var tvJudul: TextView
    private lateinit var etNama: EditText
    private lateinit var etManager: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNo: EditText
    private lateinit var btSimpan: Button

    private var idCabang: String = ""

    companion object {
        fun newInstance(
            judul: String,
            idCabang: String,
            namaCabang: String,
            managerCabang: String,
            alamatCabang: String,
            noHP: String
        ): TambahCabangFragment {
            val fragment = TambahCabangFragment()
            val args = Bundle()
            args.putString("judul", judul)
            args.putString("idCabang", idCabang)
            args.putString("namaCabang", namaCabang)
            args.putString("managerCabang", managerCabang)
            args.putString("alamatCabang", alamatCabang)
            args.putString("noHP", noHP)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_tambah_cabang, container, false)

        tvJudul = view.findViewById(R.id.tvJudul)
        etNama = view.findViewById(R.id.etNama)
        etManager = view.findViewById(R.id.etManager)
        etAlamat = view.findViewById(R.id.etAlamat)
        etNo = view.findViewById(R.id.etNo)
        btSimpan = view.findViewById(R.id.btnSimpan)

        getData()

        btSimpan.setOnClickListener {
            cekValidasi()
        }

        return view
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val manager = etManager.text.toString()
        val alamat = etAlamat.text.toString()
        val no = etNo.text.toString()

        if (nama.isEmpty()) {
            etNama.error = getString(R.string.validasi_nama_pelanggan)
            Toast.makeText(requireContext(), getString(R.string.validasi_nama_pelanggan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (manager.isEmpty()) {
            etManager.error = getString(R.string.validasiManager)
            Toast.makeText(requireContext(), getString(R.string.validasiManager), Toast.LENGTH_SHORT).show()
            etManager.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = getString(R.string.validasi_alamat_pelanggan)
            Toast.makeText(requireContext(), getString(R.string.validasi_alamat_pelanggan), Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (no.isEmpty()) {
            etNo.error = getString(R.string.validasi_no_pelanggan)
            Toast.makeText(requireContext(), getString(R.string.validasi_no_pelanggan), Toast.LENGTH_SHORT).show()
            etNo.requestFocus()
            return
        }

        if (btSimpan.text.equals(getString(R.string.simpan))) {
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
                    Toast.makeText(requireContext(), getString(R.string.sukse_simpan_pelanggan), Toast.LENGTH_SHORT).show()
                    (activity as? DataCabangActivity)?.onFormFinished()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), getString(R.string.gagal_simpan), Toast.LENGTH_SHORT).show()
                }
        } else if (btSimpan.text.equals(getString(R.string.sunting))) {
            hidup()
            etNama.requestFocus()
            btSimpan.text = getString(R.string.perbarui)
        } else if (btSimpan.text.equals(getString(R.string.perbarui))) {
            update()
        }
    }

    fun getData() {
        idCabang = arguments?.getString("idCabang") ?: ""
        val judul = arguments?.getString("judul")
        val nama = arguments?.getString("namaCabang")
        val manager = arguments?.getString("managerCabang")
        val alamat = arguments?.getString("alamatCabang")
        val hp = arguments?.getString("noHP")
        tvJudul.text = judul
        etNama.setText(nama)
        etManager.setText(manager)
        etAlamat.setText(alamat)
        etNo.setText(hp)
        if (!tvJudul.text.equals(getString(R.string.cabang_tambah))) {
            if (judul == "Edit Cabang") {
                mati()
                btSimpan.text = getString(R.string.sunting)
            }
        } else {
            hidup()
            etNama.requestFocus()
            btSimpan.text = getString(R.string.simpan)
        }
    }

    fun mati() {
        etNama.isEnabled = false
        etManager.isEnabled = false
        etAlamat.isEnabled = false
        etNo.isEnabled = false
    }

    fun hidup() {
        etNama.isEnabled = true
        etManager.isEnabled = true
        etAlamat.isEnabled = true
        etNo.isEnabled = true
    }

    fun update() {
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
                Toast.makeText(requireContext(), "Data Cabang Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                (activity as? DataCabangActivity)?.onFormFinished()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Data Cabang Gagal Diperbarui", Toast.LENGTH_SHORT).show()
            }
    }
}