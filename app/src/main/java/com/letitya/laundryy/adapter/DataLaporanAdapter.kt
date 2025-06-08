package com.letitya.laundryy.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.letitya.laundryy.R
import com.letitya.laundryy.modeldata.ModelLaporan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataLaporanAdapter(private var listLaporan: ArrayList<ModelLaporan>, private val context: Context) :
    RecyclerView.Adapter<DataLaporanAdapter.ListViewHolder>() {

    private val database = FirebaseDatabase.getInstance()
    private val laporanRef = database.getReference("laporan")

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomer: TextView = itemView.findViewById(R.id.tvNomer)
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvLayanan: TextView = itemView.findViewById(R.id.tvLayanan)
        val tvKeterangan: TextView = itemView.findViewById(R.id.tvKeterangan)
        val tvBayar: TextView = itemView.findViewById(R.id.tvBayar)
        val btStatus: Button = itemView.findViewById(R.id.btStatus)
        val tvDiambil: TextView = itemView.findViewById(R.id.tvDiambil)
        val tvKetDiambil: TextView = itemView.findViewById(R.id.tvKetDiambil)
    }

    fun updateData(newList: List<ModelLaporan>) {
        listLaporan.clear()
        listLaporan.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddatalaporan, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val laporan = listLaporan[position]

        try {
            holder.tvNomer.text = "[${position + 1}]"
            holder.tvNama.text = laporan.namaPelanggan
            holder.tvTanggal.text = laporan.tanggal
            holder.tvLayanan.text = laporan.layananUtama
            holder.tvKeterangan.text = if (laporan.jumlahTambahan > 0) {
                "+${laporan.jumlahTambahan} ${context.getString(R.string.layananTambahan)}"
            } else {
                context.getString(R.string.tidakadatambahan)
            }
            holder.tvBayar.text = laporan.totalBayar

            updateStatusUI(holder, laporan.status, laporan.tanggalDiambil)

            holder.btStatus.setOnClickListener {
                try {
                    when (laporan.status) {
                        "Belum Dibayar" -> {
                            updateLaporanStatus(laporan.idLaporan, "Sudah Dibayar", null)
                        }
                        "Sudah Dibayar" -> {
                            val tanggalDiambil = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                            updateLaporanStatus(laporan.idLaporan, "Selesai", tanggalDiambil)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateLaporanStatus(idLaporan: String, newStatus: String, tanggalDiambil: String?) {
        try {
            if (idLaporan.isNotEmpty()) {
                val updates = hashMapOf<String, Any>(
                    "status" to newStatus
                )

                if (tanggalDiambil != null) {
                    updates["tanggalDiambil"] = tanggalDiambil
                }

                laporanRef.child(idLaporan).updateChildren(updates)
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateStatusUI(holder: ListViewHolder, status: String, tanggalDiambil: String?) {
        when (status) {
            "Belum Dibayar" -> {
                holder.tvStatus.text = context.getString(R.string.belumDibayar)
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_belum_bayar)
                holder.btStatus.text = context.getString(R.string.bayarSekarang)
                holder.btStatus.setBackgroundColor(Color.parseColor("#CD5656"))
                holder.btStatus.visibility = View.VISIBLE
                holder.tvDiambil.visibility = View.GONE
                holder.tvKetDiambil.visibility = View.GONE
            }
            "Sudah Dibayar" -> {
                holder.tvStatus.text = context.getString(R.string.sudahDibayar)
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_sudah_bayar)
                holder.btStatus.text = context.getString(R.string.ambilSekarang)
                holder.btStatus.setBackgroundColor(Color.parseColor("#6096B4"))
                holder.btStatus.visibility = View.VISIBLE
                holder.tvDiambil.visibility = View.GONE
                holder.tvKetDiambil.visibility = View.GONE
            }
            "Selesai" -> {
                holder.tvStatus.text = context.getString(R.string.selesai)
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_selesai)
                holder.btStatus.visibility = View.GONE
                holder.tvKetDiambil.visibility = View.VISIBLE
                holder.tvDiambil.visibility = View.VISIBLE
                holder.tvDiambil.text = "$tanggalDiambil"
            }
        }
    }

    override fun getItemCount(): Int = listLaporan.size
}