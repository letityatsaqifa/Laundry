package com.letitya.laundryy.modeldata

import java.io.Serializable

class ModelTambahan (
    val idTambahan: String? = null,
    val namaTambahan: String? = null,
    val harga: String? = null,
    val cabang: String? = null,
    val status: String? = null
) : Serializable