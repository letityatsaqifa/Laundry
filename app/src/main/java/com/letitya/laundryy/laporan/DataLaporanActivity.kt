package com.letitya.laundryy.laporan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.letitya.laundryy.R
import com.letitya.laundryy.adapter.DataLaporanAdapter
import com.letitya.laundryy.modeldata.ModelLaporan

class DataLaporanActivity : AppCompatActivity() {
    private lateinit var rvDataLaporan: RecyclerView
    private lateinit var adapter: DataLaporanAdapter
    private val database = Firebase.database
    private val laporanRef = database.getReference("laporan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_laporan)

        rvDataLaporan = findViewById(R.id.rvData_Laporan)
        rvDataLaporan.setHasFixedSize(true)
        rvDataLaporan.layoutManager = LinearLayoutManager(this)

        adapter = DataLaporanAdapter(ArrayList(), this)
        rvDataLaporan.adapter = adapter

        loadDataFromFirebase()
    }

    private fun loadDataFromFirebase() {
        laporanRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val laporanList = ArrayList<ModelLaporan>()
                for (data in snapshot.children) {
                    val laporan = data.getValue(ModelLaporan::class.java)
                    laporan?.let {
                        laporanList.add(it)
                    }
                }
                adapter.updateData(laporanList)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    companion object {
        fun addNewLaporanToFirebase(newLaporan: ModelLaporan) {
            try {
                val database = Firebase.database
                val laporanRef = database.getReference("laporan")
                val newLaporanRef = laporanRef.push()
                newLaporan.idLaporan = newLaporanRef.key ?: ""
                newLaporanRef.setValue(newLaporan)
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}