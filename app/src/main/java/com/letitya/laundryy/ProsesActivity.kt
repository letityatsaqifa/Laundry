package com.letitya.laundryy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.letitya.laundryy.adapter.PilihLayananTambahanAdapter
import com.letitya.laundryy.laporan.DataLaporanActivity
import com.letitya.laundryy.modeldata.ModelLaporan
import com.letitya.laundryy.modeldata.ModelTambahan
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date

class ProsesActivity : AppCompatActivity() {

    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNohp: TextView
    private lateinit var tvNamaLayanan: TextView
    private lateinit var tvHarga: TextView
    private lateinit var tvLayananTambahan: TextView
    private lateinit var rvLayananTambahan: RecyclerView
    private lateinit var btnBatal: Button
    private lateinit var btnPembayaran: Button
    private lateinit var tvTotalHargaValue: TextView

    private lateinit var adapter: PilihLayananTambahanAdapter
    private val dataList = mutableListOf<ModelTambahan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proses)

        initViews()
        setupRecyclerView()
        getDataFromIntent()
        setupClickListeners()
    }

    private fun initViews() {
        tvNamaPelanggan = findViewById(R.id.tvNamaPelanggan)
        tvNohp = findViewById(R.id.tvNohp)
        tvNamaLayanan = findViewById(R.id.tvNamaLayanan)
        tvHarga = findViewById(R.id.tvHarga)
        tvLayananTambahan = findViewById(R.id.tvLayananTambahan)
        rvLayananTambahan = findViewById(R.id.rvKonfirmasi_LayananTambahan)
        btnBatal = findViewById(R.id.btnBatal)
        btnPembayaran = findViewById(R.id.btnPembayaran)
        tvTotalHargaValue = findViewById(R.id.tvTotalHargaValue)
    }

    private fun setupRecyclerView() {
        adapter = PilihLayananTambahanAdapter(ArrayList(dataList))
        rvLayananTambahan.layoutManager = LinearLayoutManager(this)
        rvLayananTambahan.adapter = adapter
        rvLayananTambahan.setHasFixedSize(true)
    }

    private fun formatRupiah(number: Int): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number.toLong())
    }

    private fun getDataFromIntent() {
        intent?.let {
            tvNamaPelanggan.text = it.getStringExtra("namaPelanggan") ?: "N/A"
            tvNohp.text = it.getStringExtra("noHP") ?: "N/A"
            tvNamaLayanan.text = it.getStringExtra("namaLayanan") ?: "N/A"

            val hargaMain = it.getStringExtra("hargaLayanan")?.toIntOrNull() ?: 0
            tvHarga.text = formatRupiah(hargaMain)

            val tambahanList = it.getSerializableExtra("dataTambahan") as? ArrayList<ModelTambahan>
            tambahanList?.let { list ->
                dataList.clear()
                dataList.addAll(list)
                adapter.updatedata(ArrayList(dataList))

                val hargaTambahan = list.sumOf { it.harga?.toIntOrNull() ?: 0 }
                val totalHarga = hargaMain + hargaTambahan

                tvTotalHargaValue.text = formatRupiah(totalHarga)
                tvLayananTambahan.visibility = if (list.isEmpty()) TextView.GONE else TextView.VISIBLE
            } ?: run {
                dataList.clear()
                adapter.updatedata(ArrayList(dataList))
                tvLayananTambahan.visibility = TextView.GONE
                tvTotalHargaValue.text = formatRupiah(hargaMain)
            }
        }
    }

    private fun setupClickListeners() {
        btnBatal.setOnClickListener {
            finish()
        }

        btnPembayaran.setOnClickListener {
            showPembayaranDialog()
        }
    }

    private fun showPembayaranDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.mod_pembayaran, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = builder.create()

        val btnPopUpBayarNanti: Button = dialogView.findViewById(R.id.btnBayarNanti)
        val btnPopUpTunai: Button = dialogView.findViewById(R.id.btnTunai)
        val btnPopUpQris: Button = dialogView.findViewById(R.id.btnQris)
        val btnPopUpDana: Button = dialogView.findViewById(R.id.btnDana)
        val btnPopUpGoPay: Button = dialogView.findViewById(R.id.btnGoPay)
        val btnPopUpOvo: Button = dialogView.findViewById(R.id.btnOvo)
        val tvPopUpBatal: TextView = dialogView.findViewById(R.id.tvBatal)

        btnPopUpBayarNanti.setOnClickListener { proceedToPayment("Bayar Nanti") }
        btnPopUpTunai.setOnClickListener { proceedToPayment("Tunai") }
        btnPopUpQris.setOnClickListener { proceedToPayment("QRIS") }
        btnPopUpDana.setOnClickListener { proceedToPayment("DANA") }
        btnPopUpGoPay.setOnClickListener { proceedToPayment("GoPay") }
        btnPopUpOvo.setOnClickListener { proceedToPayment("OVO") }

        tvPopUpBatal.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun proceedToPayment(metode: String) {
        val status = if (metode == "Bayar Nanti") "Belum Dibayar" else "Sudah Dibayar"

        // Create a new ModelLaporan with the transaction data
        val newLaporan = ModelLaporan(
            idLaporan = "", // Firebase will generate this
            namaPelanggan = tvNamaPelanggan.text.toString(),
            tanggal = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            layananUtama = tvNamaLayanan.text.toString(),
            jumlahTambahan = dataList.size,
            totalBayar = tvTotalHargaValue.text.toString(),
            status = status,
            tanggalDiambil = null
        )

        // Add to DataLaporanActivity
        DataLaporanActivity.addNewLaporanToFirebase(newLaporan)

        // Prepare data for NotaTransaksiActivity
        val intent = Intent(this, NotaTransaksiActivity::class.java).apply {
            putExtra("metode_pembayaran", metode)
            putExtra("nama_pelanggan", tvNamaPelanggan.text.toString())
            putExtra("no_hp_pelanggan", tvNohp.text.toString())
            putExtra("nama_pegawai", intent.getStringExtra("namaPegawai") ?: "")
            putExtra("nama_layanan", tvNamaLayanan.text.toString())
            putExtra("harga_layanan", intent.getStringExtra("hargaLayanan"))

            val subtotalTambahan = dataList.sumOf { it.harga?.toIntOrNull() ?: 0 }
            val totalBayar = (intent.getStringExtra("hargaLayanan")?.toIntOrNull() ?: 0) + subtotalTambahan

            putExtra("subtotal_tambahan", subtotalTambahan)
            putExtra("total_bayar", totalBayar)
            putExtra("layanan_tambahan", ArrayList(dataList))
            putExtra("status_pembayaran", status)
        }
        startActivity(intent)
        finish()
    }
}