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
import com.letitya.laundryy.modeldata.ModelTambahan
import com.letitya.laundryy.tambahan.TambahTambahanActivity

class DataTambahanAdapter(
    private val listTambahan: List<ModelTambahan>,
    private val context: Context,
    private val onItemClick: (ModelTambahan) -> Unit
) : RecyclerView.Adapter<DataTambahanAdapter.ViewHolder>(){
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddatatambahan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataTambahanAdapter.ViewHolder, position: Int) {
        val item = listTambahan[position]

        holder.tvID.text = context.getString(R.string.idTambahan) + ": " + item.idTambahan
        holder.tvNama.text = item.namaTambahan
        holder.tvHarga.text = context.getString(R.string.hargaTambah) + ": " + item.harga
        holder.tvCabang.text = context.getString(R.string.cabangTamabh) + ": " + item.cabang
        holder.tvStatus.text = context.getString(R.string.statusTambahan) + ": " + item.status

        holder.cvCard.setOnClickListener {
            onItemClick(item)
        }
        holder.btnLihat.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_mod_tambahan, null)
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            dialogView.findViewById<TextView>(R.id.tvJudul2).text = item.idTambahan ?: "-"
            dialogView.findViewById<TextView>(R.id.tvNama2).text = item.namaTambahan ?: "-"
            dialogView.findViewById<TextView>(R.id.tvHarga2).text = item.harga ?: "-"
            dialogView.findViewById<TextView>(R.id.tvCabang2).text = item.cabang ?: "-"
            dialogView.findViewById<TextView>(R.id.tvStatus2).text = item.status ?: "-"

            dialogView.findViewById<Button>(R.id.buttonsunting).setOnClickListener {
                val editIntent = Intent(appContext, TambahTambahanActivity::class.java)
                editIntent.putExtra("judul", "Edit Tambahan")
                editIntent.putExtra("idTambahan", item.idTambahan)
                editIntent.putExtra("namaTambahan", item.namaTambahan)
                editIntent.putExtra("harga", item.harga)
                editIntent.putExtra("cabang", item.cabang)
                editIntent.putExtra("status", item.status)
                context.startActivity(editIntent)
                dialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.buttonhapus).setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Apakah kamu yakin ingin menghapus tambahan layanan ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        databaseReference.child(item.idTambahan ?: "").removeValue()
                        Toast.makeText(context, "Tambahan layanan dihapus", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return listTambahan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard: CardView = itemView.findViewById(R.id.cvTambahan)
        val tvID: TextView = itemView.findViewById(R.id.tvCardTambahan_Id)
        val tvNama: TextView = itemView.findViewById(R.id.tvCardTambahan_Tambahan)
        val tvHarga: TextView = itemView.findViewById(R.id.tvCardTambahan_Harga)
        val tvCabang: TextView = itemView.findViewById(R.id.tvCardTambahan_Cabang)
        val tvStatus: TextView = itemView.findViewById(R.id.tvCardTambahan_Status)
        val btnLihat: Button = itemView.findViewById(R.id.btnCardTambahan_Lihat)
    }

}