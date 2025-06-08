package com.letitya.laundryy.modeldata

data class ModelLaporan(
    var idLaporan : String = "",
    val namaPelanggan: String = "",
    val tanggal: String = "",
    val layananUtama: String = "",
    val jumlahTambahan: Int = 0,
    val totalBayar: String = "",
    var status: String = "",
    var tanggalDiambil: String? = null
)