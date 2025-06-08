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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.letitya.laundryy.adapter.PilihLayananTambahanAdapter
import com.letitya.laundryy.modeldata.ModelTambahan

class PilihLayananTambahanActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myref = database.getReference("tambahan")

    private lateinit var rvPilihLayananTambahan: RecyclerView
    private lateinit var tambahanList: ArrayList<ModelTambahan>
    private lateinit var fullList: ArrayList<ModelTambahan>
    private lateinit var adapter: PilihLayananTambahanAdapter
    private lateinit var tvKosong: TextView
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var sharedPref: SharedPreferences
    private var cabang: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_layanan_tambahan)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        init()
        cabang = sharedPref.getString("cabang", "") ?: ""

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvPilihLayananTambahan.layoutManager = layoutManager
        rvPilihLayananTambahan.setHasFixedSize(true)

        tambahanList = arrayListOf()
        fullList = arrayListOf()

        adapter = PilihLayananTambahanAdapter(tambahanList)
        rvPilihLayananTambahan.adapter = adapter

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
        rvPilihLayananTambahan = findViewById(R.id.rvPilih_LayananTambahan)
        tvKosong = findViewById(R.id.tvPILIH_LAYANANTAMBAHAN_Kosong)
        searchView = findViewById(R.id.svPilihLayananTambahan)
    }

    private fun getData() {
        myref.limitToLast(100).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tambahanList.clear()
                fullList.clear()
                for (dataSnapshot in snapshot.children) {
                    val tambahan = dataSnapshot.getValue(ModelTambahan::class.java)
                    tambahan?.let {
                        tambahanList.add(it)
                        fullList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
                tvKosong.visibility = if (tambahanList.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                tvKosong.text = "Terjadi kesalahan: ${error.message}"
                tvKosong.visibility = View.VISIBLE
            }
        })
    }

    private fun filterList(query: String?) {
        val filteredList = ArrayList<ModelTambahan>()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(fullList)
        } else {
            val search = query.lowercase()
            for (item in fullList) {
                if (
                    item.namaTambahan?.lowercase()?.contains(search) == true ||
                    item.harga?.lowercase()?.contains(search) == true
                ) {
                    filteredList.add(item)
                }
            }
        }

        tambahanList.clear()
        tambahanList.addAll(filteredList)
        adapter.notifyDataSetChanged()
    }

    private fun updateRecyclerView() {
        val reversedList = tambahanList.reversed()
        rvPilihLayananTambahan.adapter = PilihLayananTambahanAdapter(ArrayList(reversedList))
    }
}