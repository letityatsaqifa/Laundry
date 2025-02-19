package com.letitya.laundryy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.letitya.laundryy.R
import com.letitya.laundryy.modeldata.ModelPelanggan
import java.text.SimpleDateFormat
import java.util.*

class DataPelangganAdapter(private val listPelanggan: List<ModelPelanggan>, private val context: Context) :
    RecyclerView.Adapter<DataPelangganAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddatapelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPelanggan[position]

        holder.tvID.text = item.idPelanggan ?: "Tidak ada ID"
        holder.tvNama.text = item.namaPelanggan
        holder.tvAlamat.text = context.getString(R.string.alamatPelanggan) + ": " + item.alamatPelanggan
        holder.tvNo.text = context.getString(R.string.noPelanggan) + ": " + item.noHPPelanggan
        holder.tvTimestamp.text = formatTimestamp(item.timestamp)
        holder.tvCabang.text = context.getString(R.string.cabangPelanggan) + ": " + item.cabang

        holder.cvCard.setOnClickListener {

        }
        holder.btnHubungi.setOnClickListener {

        }
        holder.btnLihat.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return listPelanggan.size
    }

    private fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) return "Tidak ada data"
        val locale = Locale.getDefault()
        val sdf = SimpleDateFormat("EEEE, dd MMM yyyy HH:mm:ss", locale)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(timestamp))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard: CardView = itemView.findViewById(R.id.cvPelanggan)
        val tvID: TextView = itemView.findViewById(R.id.tvCardPelanggan_Id)
        val tvNama: TextView = itemView.findViewById(R.id.tvCardPelanggan_Nama)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvCardPelanggan_Alamat)
        val tvNo: TextView = itemView.findViewById(R.id.tvCardPelanggan_No)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvCardPelanggan_Timestamp)
        val tvCabang: TextView = itemView.findViewById(R.id.tvCardPelanggan_cabang)
        val btnHubungi: Button = itemView.findViewById(R.id.btnCardPelanggan_Hubungi)
        val btnLihat: Button = itemView.findViewById(R.id.btnCardPelanggan_Lihat)
    }
}
