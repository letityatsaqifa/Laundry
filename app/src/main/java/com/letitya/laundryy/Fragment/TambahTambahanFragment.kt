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
import com.letitya.laundryy.modeldata.ModelTambahan
import com.letitya.laundryy.tambahan.DataTambahanActivity

class TambahTambahanFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("tambahan")
    private lateinit var tvJudul: TextView
    private lateinit var etNama: EditText
    private lateinit var etHarga: EditText
    private lateinit var etCabang: EditText
    private lateinit var etStatus: EditText
    private lateinit var btSimpan: Button

    private var idTambahan: String = ""

    companion object {
        fun newInstance(
            judul: String,
            idTambahan: String,
            namaTambahan: String,
            harga: String,
            cabang: String,
            status: String
        ): TambahTambahanFragment {
            val fragment = TambahTambahanFragment()
            val args = Bundle()
            args.putString("judul", judul)
            args.putString("idTambahan", idTambahan)
            args.putString("namaTambahan", namaTambahan)
            args.putString("harga", harga)
            args.putString("cabang", cabang)
            args.putString("status", status)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_tambah_tambahan, container, false)

        tvJudul = view.findViewById(R.id.tvJudul)
        etNama = view.findViewById(R.id.etNama)
        etHarga = view.findViewById(R.id.etHarga)
        etCabang = view.findViewById(R.id.etCabang)
        etStatus = view.findViewById(R.id.etStatus)
        btSimpan = view.findViewById(R.id.btnSimpan)

        getData()

        btSimpan.setOnClickListener {
            cekValidasi()
        }

        return view
    }

    private fun cekValidasi() {
        val nama = etNama.text.toString()
        val harga = etHarga.text.toString()
        val cabang = etCabang.text.toString()
        val status = etStatus.text.toString()

        if (nama.isEmpty()) {
            etNama.error = getString(R.string.validasi_nama_pelanggan)
            Toast.makeText(requireContext(), getString(R.string.validasi_nama_pelanggan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (harga.isEmpty()) {
            etHarga.error = getString(R.string.validasi_harga)
            Toast.makeText(requireContext(), getString(R.string.validasi_harga), Toast.LENGTH_SHORT).show()
            etHarga.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = getString(R.string.validasi_cabang_pelanggan)
            Toast.makeText(requireContext(), getString(R.string.validasi_cabang_pelanggan), Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        if (status.isEmpty()) {
            etStatus.error = getString(R.string.validasi_status)
            Toast.makeText(requireContext(), getString(R.string.validasi_status), Toast.LENGTH_SHORT).show()
            etStatus.requestFocus()
            return
        }

        if (btSimpan.text.equals(getString(R.string.simpan))) {
            val tambahanBaru = myRef.push()
            val tambahanId = tambahanBaru.key
            val data = ModelTambahan(
                tambahanId.toString(),
                etNama.text.toString(),
                etHarga.text.toString(),
                etCabang.text.toString(),
                etStatus.text.toString()
            )

            tambahanBaru.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.sukse_simpan_pelanggan),
                        Toast.LENGTH_SHORT
                    ).show()
                    (activity as? DataTambahanActivity)?.onFormFinished()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), getString(R.string.gagal_simpan), Toast.LENGTH_SHORT)
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
        idTambahan = arguments?.getString("idTambahan") ?: ""
        val judul = arguments?.getString("judul")
        val nama = arguments?.getString("namaTambahan")
        val harga = arguments?.getString("harga")
        val cabang = arguments?.getString("cabang")
        val status = arguments?.getString("status")
        tvJudul.text = judul
        etNama.setText(nama)
        etHarga.setText(harga)
        etCabang.setText(cabang)
        etStatus.setText(status)
        if (tvJudul.text != getString(R.string.tambahan_tambah)) {
            if (judul == "Edit Tambahan") {
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
        etHarga.isEnabled = false
        etCabang.isEnabled = false
        etStatus.isEnabled = false
    }

    private fun hidup() {
        etNama.isEnabled = true
        etHarga.isEnabled = true
        etCabang.isEnabled = true
        etStatus.isEnabled = true
    }

    private fun update() {
        val tambahanRef = database.getReference("tambahan").child(idTambahan)
        val data = ModelTambahan(
            idTambahan,
            etNama.text.toString(),
            etHarga.text.toString(),
            etCabang.text.toString(),
            etStatus.text.toString()
        )

        val updateData = mutableMapOf<String, Any>()
        updateData["namaTambahan"] = data.namaTambahan.toString()
        updateData["harga"] = data.harga.toString()
        updateData["cabang"] = data.cabang.toString()
        updateData["status"] = data.status.toString()

        tambahanRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data Tambahan Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                (activity as? DataTambahanActivity)?.onFormFinished()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Data Tambahan Gagal Diperbarui", Toast.LENGTH_SHORT).show()
            }
    }
}