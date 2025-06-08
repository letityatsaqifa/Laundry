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
import com.letitya.laundryy.adapter.PilihLayananAdapter
import com.letitya.laundryy.modeldata.ModelLayanan

class PilihLayananActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myref = database.getReference("layanan")

    private lateinit var rvPilihLayanan: RecyclerView
    private lateinit var listLayanan: ArrayList<ModelLayanan>
    private lateinit var fullList: ArrayList<ModelLayanan>
    private lateinit var adapter: PilihLayananAdapter
    private lateinit var tvKosong: TextView
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var sharedPref: SharedPreferences
    private var cabangLayanan: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_layanan)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        init()
        cabangLayanan = sharedPref.getString("cabang", "") ?: ""

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvPilihLayanan.layoutManager = layoutManager
        rvPilihLayanan.setHasFixedSize(true)

        listLayanan = arrayListOf()
        fullList = arrayListOf()

        adapter = PilihLayananAdapter(listLayanan)
        rvPilihLayanan.adapter = adapter

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
        rvPilihLayanan = findViewById(R.id.rvPilih_Layanan)
        tvKosong = findViewById(R.id.tvPILIH_LAYANAN_Kosong)
        searchView = findViewById(R.id.svPilihLayanan)
    }

    private fun getData() {
        myref.limitToLast(100).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listLayanan.clear()
                fullList.clear()
                for (dataSnapshot in snapshot.children) {
                    val layanan = dataSnapshot.getValue(ModelLayanan::class.java)
                    layanan?.let {
                        listLayanan.add(it)
                        fullList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
                tvKosong.visibility = if (listLayanan.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                tvKosong.text = "Terjadi kesalahan: ${error.message}"
                tvKosong.visibility = View.VISIBLE
            }
        })
    }

    private fun filterList(query: String?) {
        val filteredList = ArrayList<ModelLayanan>()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(fullList)
        } else {
            val search = query.lowercase()
            for (item in fullList) {
                if (
                    item.namaLayanan?.lowercase()?.contains(search) == true ||
                    item.hargaLayanan?.lowercase()?.contains(search) == true
                ) {
                    filteredList.add(item)
                }
            }
        }
        listLayanan.clear()
        listLayanan.addAll(filteredList)
        adapter.notifyDataSetChanged()
    }
}