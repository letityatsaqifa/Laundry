package com.letitya.laundryy.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import com.letitya.laundryy.modeldata.ModelPegawai
import java.text.SimpleDateFormat
import java.util.*

class DataPegawaiAdapter(
    private val listPegawai: List<ModelPegawai>,
    private val context: Context,
    private val onItemClick: (ModelPegawai) -> Unit
) : RecyclerView.Adapter<DataPegawaiAdapter.ViewHolder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddatapegawai, parent, false)
        appContext = parent.context
        databaseReference = FirebaseDatabase.getInstance().getReference("pegawai")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPegawai[position]

        holder.tvID.text = item.idPegawai ?: "Tidak ada ID"
        holder.tvNama.text = item.namaPegawai
        holder.tvAlamat.text = context.getString(R.string.alamatPegawai) + ": " + item.alamatPegawai
        holder.tvNo.text = context.getString(R.string.noPegawai) + ": " + item.noHPPegawai
        holder.tvTimestamp.text = formatTimestamp(item.timestamp)
        holder.tvCabang.text = context.getString(R.string.cabangPegawai) + ": " + item.cabangPegawai

        holder.cvCard.setOnClickListener {
            onItemClick(item)
        }

        holder.btnHubungi.setOnClickListener {
            var nomor = item.noHPPegawai ?: ""
            if (nomor.startsWith("0")) {
                nomor = "62" + nomor.substring(1)
            }
            nomor = nomor.replace(Regex("[^\\d]"), "")

            if (nomor.isNotEmpty()) {
                val url = "https://wa.me/$nomor"
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
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
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_mod_pegawai, null)
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            dialogView.findViewById<TextView>(R.id.tvJudul2).text = item.idPegawai ?: "-"
            dialogView.findViewById<TextView>(R.id.tvNama2).text = item.namaPegawai ?: "-"
            dialogView.findViewById<TextView>(R.id.tvAlamat2).text = item.alamatPegawai ?: "-"
            dialogView.findViewById<TextView>(R.id.tvNo2).text = item.noHPPegawai ?: "-"
            dialogView.findViewById<TextView>(R.id.tvCabang2).text = item.cabangPegawai ?: "-"

            dialogView.findViewById<Button>(R.id.buttonsunting).setOnClickListener {
                onItemClick(item)
                dialog.dismiss()
            }

            dialogView.findViewById<Button>(R.id.buttonhapus).setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Apakah kamu yakin ingin menghapus pegawai ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        databaseReference.child(item.idPegawai ?: "").removeValue()
                        Toast.makeText(context, "Pegawai dihapus", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return listPegawai.size
    }

    private fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) return "Tidak ada data"
        val locale = Locale.getDefault()
        val sdf = SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm:ss", locale)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(timestamp))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard: CardView = itemView.findViewById(R.id.cvPegawai)
        val tvID: TextView = itemView.findViewById(R.id.tvCardPegawai_Id)
        val tvNama: TextView = itemView.findViewById(R.id.tvCardPegawai_Nama)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvCardPegawai_Alamat)
        val tvNo: TextView = itemView.findViewById(R.id.tvCardPegawai_No)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvCardPegawai_Timestamp)
        val tvCabang: TextView = itemView.findViewById(R.id.tvCardPegawai_Cabang)
        val btnHubungi: Button = itemView.findViewById(R.id.btnCardPegawai_Hubungi)
        val btnLihat: Button = itemView.findViewById(R.id.btnCardPegawai_Lihat)
    }
}