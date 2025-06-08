package com.letitya.laundryy.adapter

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.letitya.laundryy.R
import com.letitya.laundryy.modeldata.ModelPelanggan
import java.text.SimpleDateFormat
import java.util.*

class DataPelangganAdapter(
    private val listPelanggan: List<ModelPelanggan>,
    private val context: Context,
    private val onItemClick: (ModelPelanggan) -> Unit
) : RecyclerView.Adapter<DataPelangganAdapter.ViewHolder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddatapelanggan, parent, false)
        appContext = parent.context
        databaseReference = FirebaseDatabase.getInstance().getReference("pelanggan")
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
            onItemClick(item)
        }

        holder.btnHubungi.setOnClickListener {
            var nomor = item.noHPPelanggan ?: ""
            if (nomor.startsWith("0")) {
                nomor = "62" + nomor.substring(1)
            }
            nomor = nomor.replace(Regex("[^\\d]"), "")

            if (nomor.isNotEmpty()) {
                val url = "https://wa.me/$nomor"
                try {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "WhatsApp tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Nomor tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        holder.btnLihat.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_mod_pelanggan, null)
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            dialogView.findViewById<TextView>(R.id.tvjudul2).text = item.idPelanggan ?: "-"
            dialogView.findViewById<TextView>(R.id.tvnama2).text = item.namaPelanggan ?: "-"
            dialogView.findViewById<TextView>(R.id.tvalamat2).text = item.alamatPelanggan ?: "-"
            dialogView.findViewById<TextView>(R.id.tvno2).text = item.noHPPelanggan ?: "-"
            dialogView.findViewById<TextView>(R.id.tvcabang2).text = item.cabang ?: "-"

            dialogView.findViewById<Button>(R.id.buttonsunting).setOnClickListener {
                onItemClick(item)
                dialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.buttonhapus).setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Apakah kamu yakin ingin menghapus pelanggan ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        databaseReference.child(item.idPelanggan ?: "").removeValue()
                        Toast.makeText(context, "Pelanggan dihapus", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return listPelanggan.size
    }

    private fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) return "Tidak ada data"
        val locale = Locale.getDefault()
        val sdf = SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm:ss", locale)
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