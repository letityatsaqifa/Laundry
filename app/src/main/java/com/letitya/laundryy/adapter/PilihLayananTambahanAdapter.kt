package com.letitya.laundryy.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.letitya.laundryy.R
import com.letitya.laundryy.TransaksiActivity
import com.letitya.laundryy.modeldata.ModelTambahan
import java.text.NumberFormat
import java.util.Locale

class PilihLayananTambahanAdapter(private val tambahanList: MutableList<ModelTambahan>) :
    RecyclerView.Adapter<PilihLayananTambahanAdapter.ViewHolder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    private var onItemClickListener: ((ModelTambahan, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (ModelTambahan, Int) -> Unit) {
        this.onItemClickListener = listener
    }

    private fun formatRupiah(number: Int): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number.toLong())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardpilihlayanantambahan, parent, false)
        appContext = parent.context
        databaseReference = FirebaseDatabase.getInstance().getReference("tambahan")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = position + 1
        val item = tambahanList[position]
        holder.tvCardLayananTambahan_Id.text = "[$nomor]"
        holder.tvNama.text = item.namaTambahan
        holder.tvHarga.text = appContext.getString(R.string.hargaTambahan) + " : " + formatRupiah(item.harga?.toIntOrNull() ?: 0)

        holder.cvCard.setOnClickListener {
            val intent = Intent()
            intent.putExtra("idTambahan", item.idTambahan)
            intent.putExtra("namaTambahan", item.namaTambahan)
            intent.putExtra("harga", item.harga)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }

        if (appContext is TransaksiActivity) {
            holder.ivHapus.visibility = View.VISIBLE
            holder.ivHapus.setOnClickListener {
                onItemClickListener?.invoke(item, position)
            }
        } else {
            holder.ivHapus.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return tambahanList.size
    }

    fun updatedata(newData: List<ModelTambahan>) {
        tambahanList.clear()
        tambahanList.addAll(newData)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        tambahanList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tambahanList.size)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvCardLayananTambahan_Nama)
        val tvHarga: TextView = itemView.findViewById(R.id.tvCardLayananTambahan_Harga)
        val cvCard: CardView = itemView.findViewById(R.id.cvLayananTambahan)
        val ivHapus: ImageView = itemView.findViewById(R.id.ivHapus)
        val tvCardLayananTambahan_Id: TextView = itemView.findViewById(R.id.tvCardLayananTambahan_Id)
    }
}