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
import com.letitya.laundryy.R
import com.letitya.laundryy.cabang.TambahCabangActivity
import com.letitya.laundryy.modeldata.ModelCabang
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DataCabangAdapter(
    private val listCabang: List<ModelCabang>,
    private val context: Context,
    private val onItemClick: (ModelCabang) -> Unit
) : RecyclerView.Adapter<DataCabangAdapter.ViewHolder>(){
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.carddatacabang, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DataCabangAdapter.ViewHolder, position: Int) {
        val item = listCabang[position]

        holder.tvID.text = context.getString(R.string.idCabang) + ": " + item.idCabang
        holder.tvNama.text = item.namaCabang
        holder.tvManager.text = context.getString(R.string.managerCabang) + ": " + item.managerCabang
        holder.tvAlamat.text = context.getString(R.string.alamatCabang) + ": " + item.alamatCabang
        holder.tvNo.text = context.getString(R.string.noCabang) + ": " + item.noHP
        holder.tvTimestamp.text = formatTimestamp(item.timestamp)

        holder.cvCard.setOnClickListener {
            onItemClick(item)
        }
        holder.btHubungi.setOnClickListener {
            var nomor = item.noHP ?: ""
            if (nomor.startsWith("0")) {
                nomor = "62" + nomor.substring(1)  // buat ganti 0 di depan ke 62
            }
            nomor = nomor.replace(Regex("[^\\d]"), "")  // buat hapus karakter non-digit

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
        holder.btLihat.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_mod_cabang, null)
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            // Set data ke TextView di dialog
            dialogView.findViewById<TextView>(R.id.tvJudul2).text = item.idCabang ?: "-"
            dialogView.findViewById<TextView>(R.id.tvNama2).text = item.namaCabang ?: "-"
            dialogView.findViewById<TextView>(R.id.tvManager2).text = item.managerCabang ?: "-"
            dialogView.findViewById<TextView>(R.id.tvAlamat2).text = item.alamatCabang ?: "-"
            dialogView.findViewById<TextView>(R.id.tvNo2).text = item.noHP ?: "-"

            // Tombol sunting
            dialogView.findViewById<Button>(R.id.buttonsunting).setOnClickListener {
                val editIntent = Intent(appContext, TambahCabangActivity::class.java)
                editIntent.putExtra("judul", "Edit Cabang")
                editIntent.putExtra("idCabang", item.idCabang)
                editIntent.putExtra("namaCabang", item.namaCabang)
                editIntent.putExtra("managerCabang", item.managerCabang)
                editIntent.putExtra("noHP", item.noHP)
                editIntent.putExtra("alamatCabang", item.alamatCabang)
                context.startActivity(editIntent)
                dialog.dismiss()
            }

            // Tombol hapus
            dialogView.findViewById<Button>(R.id.buttonhapus).setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Konfirmasi Hapus")
                    .setMessage("Apakah kamu yakin ingin menghapus cabang ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        databaseReference.child(item.idCabang ?: "").removeValue()
                        Toast.makeText(context, "Cabang dihapus", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }

            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return listCabang.size
    }

    private fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) return "Tidak ada data"
        val locale = Locale.getDefault()
        val sdf = SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm:ss", locale)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(Date(timestamp))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCard: CardView = itemView.findViewById(R.id.cvCabang)
        val tvID: TextView = itemView.findViewById(R.id.tvCardCabang_Id)
        val tvNama: TextView = itemView.findViewById(R.id.tvCardCabang_Nama)
        val tvManager: TextView = itemView.findViewById(R.id.tvCardCabang_Manager)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvCardCabang_Alamat)
        val tvNo: TextView = itemView.findViewById(R.id.tvCardCabang_No)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvCardCabang_Timestamp)
        val btHubungi: Button = itemView.findViewById(R.id.btnCardCabang_Hubungi)
        val btLihat: Button = itemView.findViewById(R.id.btnCardCabang_Lihat)
    }
}