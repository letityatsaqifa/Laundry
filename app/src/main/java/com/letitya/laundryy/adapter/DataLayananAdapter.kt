package com.letitya.laundryy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.letitya.laundryy.R
import com.letitya.laundryy.modeldata.ModelLayanan

class DataLayananAdapter(private val listLayanan: ArrayList<ModelLayanan>) : RecyclerView.Adapter<DataLayananAdapter.ViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddatalayanan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listLayanan[position]

        holder.tvID.text = item.idLayanan ?: "Tidak ada ID"
        holder.tvNama.text = item.namaLayanan
        holder.tvHarga.text = item.hargaLayanan
        holder.tvCabang.text = item.cabangLayanan

        holder.cvCard.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return listLayanan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard: CardView = itemView.findViewById(R.id.cvLayanan)
        val tvID: TextView = itemView.findViewById(R.id.tvCardLayanan_Id)
        val tvNama: TextView = itemView.findViewById(R.id.tvCardLayanan_Nama)
        val tvHarga: TextView = itemView.findViewById(R.id.tvCardLayanan_Harga)
        val tvCabang: TextView = itemView.findViewById(R.id.tvCardLayanan_Cabang)
    }
}