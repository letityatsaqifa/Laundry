package com.letitya.laundryy

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.letitya.laundryy.adapter.PilihPelangganAdapter
import com.letitya.laundryy.modeldata.ModelPelanggan

class PilihPelangganActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myref = database.getReference("pelanggan")

    private lateinit var rvPilihPelanggan: RecyclerView
    private lateinit var listPelanggan: ArrayList<ModelPelanggan>
    private lateinit var fullList: ArrayList<ModelPelanggan>
    private lateinit var adapter: PilihPelangganAdapter
    private lateinit var tvKosong: TextView
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var sharedPref: SharedPreferences
    private var cabang: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_pelanggan)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        init()
        cabang = sharedPref.getString("cabang", "") ?: ""

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvPilihPelanggan.layoutManager = layoutManager
        rvPilihPelanggan.setHasFixedSize(true)

        listPelanggan = arrayListOf()
        fullList = arrayListOf()

        adapter = PilihPelangganAdapter(listPelanggan)
        rvPilihPelanggan.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getData()

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun init() {
        rvPilihPelanggan = findViewById(R.id.rvPilih_Pelanggan)
        tvKosong = findViewById(R.id.tvPILIH_PELANGGAN_Kosong)
        searchView = findViewById(R.id.svPilihPelanggan)
    }

    private fun getData() {
        myref.limitToLast(100).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPelanggan.clear()
                fullList.clear()
                for (dataSnapshot in snapshot.children) {
                    val pelanggan = dataSnapshot.getValue(ModelPelanggan::class.java)
                    pelanggan?.let {
                        listPelanggan.add(it)
                        fullList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
                tvKosong.visibility = if (listPelanggan.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                tvKosong.text = "Terjadi kesalahan: ${error.message}"
                tvKosong.visibility = View.VISIBLE
            }
        })
    }

    private fun filterList(query: String?) {
        val filteredList = ArrayList<ModelPelanggan>()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(fullList)
        } else {
            val search = query.lowercase()
            for (item in fullList) {
                if (
                    item.namaPelanggan?.lowercase()?.contains(search) == true ||
                    item.noHPPelanggan?.lowercase()?.contains(search) == true ||
                    item.alamatPelanggan?.lowercase()?.contains(search) == true
                ) {
                    filteredList.add(item)
                }
            }
        }

        listPelanggan.clear()
        listPelanggan.addAll(filteredList)
        adapter.notifyDataSetChanged()
    }
}