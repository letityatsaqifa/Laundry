package com.letitya.laundryy

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.letitya.laundryy.adapter.PilihLayananTambahanAdapter
import com.letitya.laundryy.modeldata.ModelTambahan
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class NotaTransaksiActivity : AppCompatActivity() {

    private lateinit var tvIdTransaksi2: TextView
    private lateinit var tvTanggal2: TextView
    private lateinit var tvPelanggan2: TextView
    private lateinit var tvKaryawan2: TextView
    private lateinit var tvLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var rvNotaLayananTambahan: RecyclerView
    private lateinit var tvSubTotalTambahanValue: TextView
    private lateinit var tvTotalBayarValue: TextView
    private lateinit var btnKirimWa: Button
    private lateinit var btnProses: Button
    private lateinit var tvCabang: TextView

    private lateinit var adapter: PilihLayananTambahanAdapter
    private val dataList = mutableListOf<ModelTambahan>()

    private var idTransaksi = ""
    private var namaPelanggan = ""
    private var noHpPelanggan = ""
    private var namaPegawai = ""
    private var namaLayanan = ""
    private var hargaLayanan = ""
    private var subtotalTambahan = 0
    private var totalBayar = 0

    companion object {
        private const val REQUEST_ENABLE_BT = 1002
        private const val REQUEST_BLUETOOTH_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nota_transaksi)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
        setupRecyclerView()
        ambilData()
        setupKlikListener()
    }

    private fun init() {
        tvIdTransaksi2 = findViewById(R.id.tvIdTransaksi2)
        tvTanggal2 = findViewById(R.id.tvTanggal2)
        tvPelanggan2 = findViewById(R.id.tvPelanggan2)
        tvKaryawan2 = findViewById(R.id.tvKaryawan2)
        tvLayanan = findViewById(R.id.tvLayanan)
        tvHargaLayanan = findViewById(R.id.tvHargaLayanan)
        rvNotaLayananTambahan = findViewById(R.id.rvNota_LayananTambahan)
        tvSubTotalTambahanValue = findViewById(R.id.tvSubTotalTambahanValue)
        tvTotalBayarValue = findViewById(R.id.tvTotalBayarValue)
        btnKirimWa = findViewById(R.id.btnKirimWa)
        btnProses = findViewById(R.id.btnProses)
        tvCabang = findViewById(R.id.tvCabang)
    }

    private fun setupRecyclerView() {
        adapter = PilihLayananTambahanAdapter(ArrayList(dataList))
        rvNotaLayananTambahan.layoutManager = LinearLayoutManager(this)
        rvNotaLayananTambahan.adapter = adapter
        rvNotaLayananTambahan.setHasFixedSize(true)
    }

    private fun formatRupiah(jumlah: Int): String {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(jumlah.toLong())
    }

    private fun ambilData() {
        val sharedPref: SharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val cabang = sharedPref.getString("idCabang", "Cabang Tidak Diketahui") ?: "Cabang Tidak Diketahui"
        tvCabang.text = cabang

        idTransaksi = "TRX-${System.currentTimeMillis().toString().takeLast(6)}"
        tvIdTransaksi2.text = idTransaksi

        val formatTanggal = SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault())
        val tanggalSekarang = formatTanggal.format(Date())
        tvTanggal2.text = tanggalSekarang

        intent?.let {
            namaPelanggan = it.getStringExtra("nama_pelanggan") ?: "Tidak Ada"
            noHpPelanggan = it.getStringExtra("no_hp_pelanggan") ?: ""
            namaPegawai = it.getStringExtra("nama_pegawai") ?: "Tidak Ada"
            namaLayanan = it.getStringExtra("nama_layanan") ?: "Tidak Ada"

            hargaLayanan = it.getStringExtra("harga_layanan") ?: "0"
            subtotalTambahan = it.getIntExtra("subtotal_tambahan", 0)
            totalBayar = it.getIntExtra("total_bayar", 0)

            tvPelanggan2.text = namaPelanggan
            tvKaryawan2.text = namaPegawai
            tvLayanan.text = namaLayanan
            tvHargaLayanan.text = formatRupiah(hargaLayanan.toIntOrNull() ?: 0)
            tvSubTotalTambahanValue.text = formatRupiah(subtotalTambahan)
            tvTotalBayarValue.text = formatRupiah(totalBayar)

            val listTambahan = it.getSerializableExtra("layanan_tambahan") as? ArrayList<ModelTambahan>
            listTambahan?.let { list ->
                dataList.clear()
                dataList.addAll(list)
                adapter.updatedata(ArrayList(dataList))

                val layoutParams = rvNotaLayananTambahan.layoutParams
                layoutParams.height = (list.size * resources.getDimension(R.dimen.card_height)).toInt()
                rvNotaLayananTambahan.layoutParams = layoutParams
            }
        }
    }

    private fun setupKlikListener() {
        btnKirimWa.setOnClickListener {
            if (noHpPelanggan.isNotEmpty()) {
                kirimWhatsApp()
            } else {
                Toast.makeText(this, getString(R.string.whatsapp_not_found), Toast.LENGTH_SHORT).show()
            }
        }

        btnProses.setOnClickListener {
            cetakNota()
        }
    }

    private fun kirimWhatsApp() {
        try {
            val nomorHP = noHpPelanggan.replace("[^0-9]".toRegex(), "")
            val nomorWhatsApp = if (nomorHP.startsWith("0")) {
                "62${nomorHP.substring(1)}"
            } else if (!nomorHP.startsWith("62")) {
                "62$nomorHP"
            } else {
                nomorHP
            }

            val teksNota = buildString {
                appendLine("*${getString(R.string.nota_title)}*")
                appendLine()
                appendLine("*🆔 ${getString(R.string.id_transaksi_label)}:* $idTransaksi")
                appendLine("*📅 ${getString(R.string.tanggal_label)}:* ${tvTanggal2.text}")
                appendLine("*👤 ${getString(R.string.pelanggan_label)}:* $namaPelanggan")
                appendLine("*👥 ${getString(R.string.pegawai_label)}:* $namaPegawai")
                appendLine()
                appendLine("*✨ ${getString(R.string.layanan_utama_label)}:*")
                appendLine("• $namaLayanan - ${tvHargaLayanan.text}")
                appendLine()

                if (dataList.isNotEmpty()) {
                    appendLine("*➕ ${getString(R.string.layanan_tambahan_label)}:*")
                    dataList.forEach {
                        appendLine("• ${it.namaTambahan} - ${formatRupiah(it.harga?.toIntOrNull() ?: 0)}")
                    }
                    appendLine()
                }

                appendLine("*💰 ${getString(R.string.subtotalTambahan)}:* ${tvSubTotalTambahanValue.text}")
                appendLine("*💳 ${getString(R.string.totalbayar)}:* ${tvTotalBayarValue.text}")
                appendLine()
                appendLine("🙏 ${getString(R.string.thank_you_note)}")
            }

            val intent = Intent(Intent.ACTION_VIEW)
            val url = "https://wa.me/$nomorWhatsApp?text=${Uri.encode(teksNota)}"
            intent.data = Uri.parse(url)
            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.whatsapp_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    private fun cetakNota() {
        try {
            val teksStruk = buildString {
                appendLine("Qraf Laundry")
                appendLine(tvCabang.text.toString())
                appendLine()
                appendLine("${getString(R.string.id_transaksi_label)}: $idTransaksi")
                appendLine("${getString(R.string.tanggal_label)}: ${tvTanggal2.text}")
                appendLine("${getString(R.string.pelanggan_label)}: $namaPelanggan")
                appendLine("${getString(R.string.pegawai_label)}: $namaPegawai")
                appendLine()
                appendLine("${getString(R.string.layanan_utama_label)}:")
                appendLine("${tvLayanan.text} - ${tvHargaLayanan.text}")
                appendLine()

                if (dataList.isNotEmpty()) {
                    appendLine("${getString(R.string.layanan_tambahan_label)}:")
                    dataList.forEach {
                        appendLine("${it.namaTambahan} - ${formatRupiah(it.harga?.toIntOrNull() ?: 0)}")
                    }
                    appendLine()
                }

                appendLine("${getString(R.string.subtotalTambahan)}: ${tvSubTotalTambahanValue.text}")
                appendLine("${getString(R.string.totalbayar)}: ${tvTotalBayarValue.text}")
                appendLine()
                appendLine(getString(R.string.thank_you_note))
            }

            cetak(teksStruk)

        } catch (e: Exception) {
            Toast.makeText(this, "Gagal mencetak struk: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cetak(teks: String) {
        if (checkBluetoothPermissions()) {
            startBluetoothPrinting(teks)
        } else {
            requestBluetoothPermissions()
        }
    }

    private fun checkBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }

        ActivityCompat.requestPermissions(
            this,
            permissions,
            REQUEST_BLUETOOTH_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnProses.performClick()
                } else {
                    Toast.makeText(
                        this,
                        "Izin Bluetooth diperlukan untuk mencetak struk",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun startBluetoothPrinting(teks: String) {
        try {
            if (!checkBluetoothPermissions()) {
                requestBluetoothPermissions()
                return
            }

            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Perangkat tidak mendukung Bluetooth", Toast.LENGTH_SHORT).show()
                return
            }

            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                }
                return
            }

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestBluetoothPermissions()
                return
            }

            val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
            val printer = pairedDevices.firstOrNull { device ->
                device.name?.contains("Printer", ignoreCase = true) == true
            }

            printer?.let { device ->
                try {
                    val socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                    socket.connect()
                    val outputStream = socket.outputStream

                    // ESC/POS commands
                    val init = byteArrayOf(0x1B, 0x40) // Initialize printer
                    val alignCenter = byteArrayOf(0x1B, 0x61, 0x01) // Center alignment
                    val cut = byteArrayOf(0x1D, 0x56, 0x41, 0x00) // Full cut

                    outputStream.write(init)
                    outputStream.write(alignCenter)
                    outputStream.write(teks.toByteArray(Charsets.UTF_8))
                    outputStream.write(cut)
                    outputStream.flush()
                    socket.close()

                    runOnUiThread {
                        Toast.makeText(this, "Struk sedang dicetak", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "Gagal terhubung ke printer: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                runOnUiThread {
                    Toast.makeText(this, "Printer Bluetooth tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            runOnUiThread {
                Toast.makeText(this, "Izin Bluetooth diperlukan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == RESULT_OK) {
                    btnProses.performClick()
                } else {
                    Toast.makeText(this, "Bluetooth harus diaktifkan untuk mencetak", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}