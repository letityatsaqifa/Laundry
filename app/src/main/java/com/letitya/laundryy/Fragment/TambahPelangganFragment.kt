package com.letitya.laundryy.Fragment

import com.letitya.laundryy.pelanggan.DataPelangganActivity

import android.os.Build
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
import com.letitya.laundryy.modeldata.ModelPelanggan
import androidx.annotation.RequiresApi


class TambahPelangganFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pelanggan")
    private lateinit var tvJudul: TextView
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNo: EditText
    private lateinit var etCabang: EditText
    private lateinit var btSimpan: Button

    private var idPelanggan: String = ""

    companion object {
        fun newInstance(
            judul: String,
            idPelanggan: String,
            namaPelanggan: String,
            noHPPelanggan: String,
            alamatPelanggan: String,
            cabang: String
        ): TambahPelangganFragment {
            val fragment = TambahPelangganFragment()
            val args = Bundle()
            args.putString("judul", judul)
            args.putString("idPelanggan", idPelanggan)
            args.putString("namaPelanggan", namaPelanggan)
            args.putString("noHPPelanggan", noHPPelanggan)
            args.putString("alamatPelanggan", alamatPelanggan)
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
        val view = inflater.inflate(R.layout.activity_tambah_pelanggan, container, false)

        tvJudul = view.findViewById(R.id.tvJudul)
        etNama = view.findViewById(R.id.etNama)
        etAlamat = view.findViewById(R.id.etAlamat)
        etNo = view.findViewById(R.id.etNo)
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
        val alamat = etAlamat.text.toString()
        val no = etNo.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = getString(R.string.validasi_nama_pelanggan)
            Toast.makeText(requireContext(), getString(R.string.validasi_nama_pelanggan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
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
        if (cabang.isEmpty()) {
            etCabang.error = getString(R.string.validasi_cabang_pelanggan)
            Toast.makeText(requireContext(), getString(R.string.validasi_cabang_pelanggan), Toast.LENGTH_SHORT).show()
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
                        requireContext(),
                        getString(R.string.sukse_simpan_pelanggan),
                        Toast.LENGTH_SHORT
                    ).show()
                    (activity as? DataPelangganActivity)?.onFormFinished()
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
        idPelanggan = arguments?.getString("idPelanggan") ?: ""
        val judul = arguments?.getString("judul")
        val nama = arguments?.getString("namaPelanggan")
        val alamat = arguments?.getString("alamatPelanggan")
        val hp = arguments?.getString("noHPPelanggan")
        val cabang = arguments?.getString("cabang")
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
                Toast.makeText(requireContext(), "Data Pelanggan Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                (activity as? DataPelangganActivity)?.onFormFinished()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Data Pelanggan Gagal Diperbarui", Toast.LENGTH_SHORT).show()
            }
    }
}