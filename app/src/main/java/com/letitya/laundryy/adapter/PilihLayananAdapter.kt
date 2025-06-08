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
import com.letitya.laundryy.modeldata.ModelLayanan

class PilihLayananAdapter(private val listLayanan: List<ModelLayanan>) :
    RecyclerView.Adapter<PilihLayananAdapter.ViewHolder>(){
        lateinit var appContext: Context
        lateinit var databaseReference: DatabaseReference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardpilihlayanan, parent, false)
        appContext = parent.context
        databaseReference = FirebaseDatabase.getInstance().getReference("layanan")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PilihLayananAdapter.ViewHolder, position: Int) {
        val nomor = position + 1
        val item = listLayanan[position]
        holder.tvCardLayanan_Id.text = "[$nomor]"
        holder.tvNama.text = item.namaLayanan
        holder.tvHarga.text = appContext.getString(R.string.harga) + " : " + item.hargaLayanan

        holder.cvCard.setOnClickListener {
            val intent = Intent(appContext, TransaksiActivity::class.java)
            intent.putExtra("idLayanan", item.idLayanan)
            intent.putExtra("nama", item.namaLayanan)
            intent.putExtra("harga", item.hargaLayanan)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return listLayanan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvCardLayanan_Nama)
        val tvHarga: TextView = itemView.findViewById(R.id.tvCardLayanan_Harga)
        val cvCard: CardView = itemView.findViewById(R.id.cvLayanan)
        val tvCardLayanan_Id: TextView = itemView.findViewById(R.id.tvCardLayanan_Id)
    }
}