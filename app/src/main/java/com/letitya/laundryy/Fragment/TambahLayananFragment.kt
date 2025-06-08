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
import com.letitya.laundryy.layanan.DataLayananActivity
import com.letitya.laundryy.modeldata.ModelLayanan

class TambahLayananFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("layanan")
    private lateinit var tvJudul: TextView
    private lateinit var etNama: EditText
    private lateinit var etHarga: EditText
    private lateinit var etCabang: EditText
    private lateinit var btSimpan: Button

    private var idLayanan: String = ""

    companion object {
        fun newInstance(
            judul: String,
            idLayanan: String,
            namaLayanan: String,
            hargaLayanan: String,
            cabang: String
        ): TambahLayananFragment {
            val fragment = TambahLayananFragment()
            val args = Bundle()
            args.putString("judul", judul)
            args.putString("idLayanan", idLayanan)
            args.putString("namaLayanan", namaLayanan)
            args.putString("hargaLayanan", hargaLayanan)
            args.putString("cabang", cabang)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_tambah_layanan, container, false)

        tvJudul = view.findViewById(R.id.tvJudul)
        etNama = view.findViewById(R.id.etNama)
        etHarga = view.findViewById(R.id.etHarga)
        etCabang = view.findViewById(R.id.etCabang)
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

        if (btSimpan.text.equals(getString(R.string.simpan))) {
            val layananBaru = myRef.push()
            val layananId = layananBaru.key
            val data = ModelLayanan(
                layananId.toString(),
                etNama.text.toString(),
                etHarga.text.toString(),
                etCabang.text.toString()
            )

            layananBaru.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.sukse_simpan_pelanggan),
                        Toast.LENGTH_SHORT
                    ).show()
                    (activity as? DataLayananActivity)?.onFormFinished()
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
        idLayanan = arguments?.getString("idLayanan") ?: ""
        val judul = arguments?.getString("judul")
        val nama = arguments?.getString("namaLayanan")
        val harga = arguments?.getString("hargaLayanan")
        val cabang = arguments?.getString("cabang")
        tvJudul.text = judul
        etNama.setText(nama)
        etHarga.setText(harga)
        etCabang.setText(cabang)
        if (tvJudul.text != getString(R.string.layanan_tambah)) {
            if (judul == "Edit Layanan") {
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
    }

    private fun hidup() {
        etNama.isEnabled = true
        etHarga.isEnabled = true
        etCabang.isEnabled = true
    }

    private fun update() {
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
        updateData["cabang"] = data.cabangLayanan.toString()

        layananRef.updateChildren(updateData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Data Layanan Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                (activity as? DataLayananActivity)?.onFormFinished()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Data Layanan Gagal Diperbarui", Toast.LENGTH_SHORT).show()
            }
    }
}