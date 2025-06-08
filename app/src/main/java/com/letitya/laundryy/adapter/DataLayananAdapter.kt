package com.letitya.laundryy.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.letitya.laundryy.R
import com.letitya.laundryy.layanan.TambahLayananActivity
import com.letitya.laundryy.modeldata.ModelLayanan

class DataLayananAdapter(
    private val listLayanan: ArrayList<ModelLayanan>,
    private val context: Context,
    private val onItemClick: (ModelLayanan) -> Unit
) : RecyclerView.Adapter<DataLayananAdapter.ViewHolder> () {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddatalayanan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listLayanan[position]

        holder.tvID.text = item.idLayanan ?: "Tidak ada ID"
        holder.tvNama.text = item.namaLayanan
        holder.tvHarga.text = context.getString(R.string.harga) + ": " + item.hargaLayanan
        holder.tvCabang.text = context.getString(R.string.cabang) + ": " + item.cabangLayanan

        holder.cvCard.setOnClickListener {
            onItemClick(item)
        }
        holder.btnLihat.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_mod_layanan, null)
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            dialogView.findViewById<TextView>(R.id.tvJudul2).text = item.idLayanan ?: "-"
            dialogView.findViewById<TextView>(R.id.tvNama2).text = item.namaLayanan ?: "-"
            dialogView.findViewById<TextView>(R.id.tvHarga2).text = item.hargaLayanan ?: "-"
            dialogView.findViewById<TextView>(R.id.tvcabang2).text = item.cabangLayanan ?: "-"

            dialogView.findViewById<Button>(R.id.buttonsunting).setOnClickListener {
                val editIntent = Intent(appContext, TambahLayananActivity::class.java)
                editIntent.putExtra("judul", "Edit Layanan")
                editIntent.putExtra("idLayanan", item.idLayanan)
                editIntent.putExtra("namaLayanan", item.namaLayanan)
                editIntent.putExtra("hargaLayanan", item.hargaLayanan)
                editIntent.putExtra("cabangLayanan", item.cabangLayanan)
                context.startActivity(editIntent)
                dialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.buttonhapus).setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Apakah kamu yakin ingin menghapus layanan ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        databaseReference.child(item.idLayanan ?: "").removeValue()
                        Toast.makeText(context, "Layanan dihapus", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }

            dialog.show()
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
        val btnLihat: Button = itemView.findViewById(R.id.btnCardLayanan_Lihat)
    }
}