package com.letitya.laundryy.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.letitya.laundryy.R
import com.letitya.laundryy.TransaksiActivity
import com.letitya.laundryy.modeldata.ModelPelanggan

class PilihPelangganAdapter (private val listPelanggan: List<ModelPelanggan>) :
    RecyclerView.Adapter<PilihPelangganAdapter.ViewHolder>() {
        lateinit var appContext: Context
        lateinit var databaseReference: DatabaseReference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardpilihpelanggan, parent, false)
        appContext = parent.context
        databaseReference = FirebaseDatabase.getInstance().getReference("pelanggan")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = position + 1
        val item = listPelanggan[position]
        holder.tvCardPelanggan_Id.text = "[$nomor]"
        holder.tvNama.text = item.namaPelanggan
        holder.tvAlamat.text = appContext.getString(R.string.alamatPelanggan) + " : " + item.alamatPelanggan
        holder.tvNo.text = appContext.getString(R.string.noPelanggan) + " : " + item.noHPPelanggan

        holder.cvCard.setOnClickListener {
            val intent = Intent(appContext, TransaksiActivity::class.java)
            intent.putExtra("idPelanggan", item.idPelanggan)
            intent.putExtra("namaPelanggan", item.namaPelanggan)
            intent.putExtra("noHPPelanggan", item.noHPPelanggan)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return listPelanggan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvCardPelanggan_Nama)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvCardPelanggan_Alamat)
        val tvNo: TextView = itemView.findViewById(R.id.tvCardPelanggan_No)
        val cvCard: CardView = itemView.findViewById(R.id.cvPelanggan)
        val tvCardPelanggan_Id: TextView = itemView.findViewById(R.id.tvCardPelanggan_Id)
    }

}