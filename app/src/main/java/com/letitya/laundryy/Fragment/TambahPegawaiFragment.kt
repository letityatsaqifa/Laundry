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
import com.letitya.laundryy.modeldata.ModelPegawai
import com.letitya.laundryy.pegawai.DataPegawaiActivity
import java.util.Date

class TambahPegawaiFragment : Fragment() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pegawai")
    private lateinit var tvJudul: TextView
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNo: EditText
    private lateinit var etCabang: EditText
    private lateinit var btSimpan: Button

    private var idPegawai: String = ""

    companion object {
        fun newInstance(
            judul: String,
            idPegawai: String,
            namaPegawai: String,
            noHPPegawai: String,
            alamatPegawai: String,
            cabangPegawai: String
        ): TambahPegawaiFragment {
            val fragment = TambahPegawaiFragment()
            val args = Bundle()
            args.putString("judul", judul)
            args.putString("idPegawai", idPegawai)
            args.putString("namaPegawai", namaPegawai)
            args.putString("noHPPegawai", noHPPegawai)
            args.putString("alamatPegawai", alamatPegawai)
            args.putString("cabangPegawai", cabangPegawai)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_tambah_pegawai, container, false)

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
            etNama.error = getString(R.string.validasi_nama_pegawai)
            Toast.makeText(requireContext(), getString(R.string.validasi_nama_pegawai), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = getString(R.string.validasi_alamat_pegawai)
            Toast.makeText(requireContext(), getString(R.string.validasi_alamat_pegawai), Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (no.isEmpty()) {
            etNo.error = getString(R.string.validasi_no_pegawai)
            Toast.makeText(requireContext(), getString(R.string.validasi_no_pegawai), Toast.LENGTH_SHORT).show()
            etNo.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = getString(R.string.validasi_cabang_pegawai)
            Toast.makeText(requireContext(), getString(R.string.validasi_cabang_pegawai), Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }

        if (btSimpan.text.equals(getString(R.string.simpan))) {
            val pegawaiBaru = myRef.push()
            val pegawaiId = pegawaiBaru.key
            val data = ModelPegawai(
                pegawaiId.toString(),
                etNama.text.toString(),
                etAlamat.text.toString(),
                etNo.text.toString(),
                timestamp = System.currentTimeMillis(),
                etCabang.text.toString()
            )

            pegawaiBaru.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.sukse_simpan_pelanggan),
                        Toast.LENGTH_SHORT
                    ).show()
                    (activity as? DataPegawaiActivity)?.onFormFinished()
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
        idPegawai = arguments?.getString("idPegawai") ?: ""
        val judul = arguments?.getString("judul")
        val nama = arguments?.getString("namaPegawai")
        val alamat = arguments?.getString("alamatPegawai")
        val hp = arguments?.getString("noHPPegawai")
        val cabang = arguments?.getString("cabangPegawai")
        tvJudul.text = judul
        etNama.setText(nama)
        etAlamat.setText(alamat)
        etNo.setText(hp)
        etCabang.setText(cabang)
        if (tvJudul.text != getString(R.string.pegawai_tambah)) {
            if (judul == "Edit Pegawai") {
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

    private fun update() {
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
                Toast.makeText(requireContext(), "Data Pegawai Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                (activity as? DataPegawaiActivity)?.onFormFinished()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Data Pegawai Gagal Diperbarui", Toast.LENGTH_SHORT).show()
            }
    }
}