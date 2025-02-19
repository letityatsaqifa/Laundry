package com.letitya.laundryy.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.letitya.laundryy.modeldata.ModelPegawai
import java.util.ArrayList
import com.letitya.laundryy.R

class DataPegawaiAdapter (private val listPegawai: ArrayList<ModelPegawai>) :
    RecyclerView.Adapter<DataPegawaiAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddatapegawai, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPegawai[position]

        holder.tvID.text = item.idPegawai ?: "Tidak ada ID"
        holder.tvNama.text = item.namaPegawai
        holder.tvJK.text = item.jk
        holder.tvAlamat.text = item.alamatPegawai
        holder.tvNo.text = item.noHPPegawai
        holder.tvJabatan.text = item.jabatanPegawai
        holder.tvCabang.text = item.cabangPegawai

        holder.cvCard.setOnClickListener {

        }
        holder.btnHubungi.setOnClickListener {

        }
        holder.btnLihat.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return listPegawai.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard: CardView = itemView.findViewById(R.id.cvPegawai)
        val tvID: TextView = itemView.findViewById(R.id.tvCardPegawai_Id)
        val tvNama: TextView = itemView.findViewById(R.id.tvCardPegawai_Nama)
        val tvJK: TextView = itemView.findViewById(R.id.tvCardPegawai_JK)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvCardPegawai_Alamat)
        val tvNo: TextView = itemView.findViewById(R.id.tvCardPegawai_No)
        val tvJabatan: TextView = itemView.findViewById(R.id.tvCardPegawai_Jabatan)
        val tvCabang: TextView = itemView.findViewById(R.id.tvCardPegawai_Cabang)
        val btnHubungi: Button = itemView.findViewById(R.id.btnCardPegawai_Hubungi)
        val btnLihat: Button = itemView.findViewById(R.id.btnCardPegawai_Lihat)
    }
}