package com.letitya.laundryy.layanan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.letitya.laundryy.R
import com.letitya.laundryy.adapter.DataLayananAdapter
import com.letitya.laundryy.adapter.DataPelangganAdapter
import com.letitya.laundryy.modeldata.ModelLayanan
import com.letitya.laundryy.modeldata.ModelPelanggan

class DataLayananActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")
    lateinit var rvDataLayanan: RecyclerView
    lateinit var dataLayanan : FloatingActionButton
    lateinit var layananList: ArrayList<ModelLayanan>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_layanan)

        rvDataLayanan = findViewById(R.id.rvData_Layanan)
        dataLayanan = findViewById(R.id.fabDataLayananTambah)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDataLayanan.layoutManager = layoutManager
        rvDataLayanan.setHasFixedSize(true)
        layananList = arrayListOf<ModelLayanan>()

        dataLayanan.setOnClickListener {
            val intent = Intent(this,TambahLayananActivity::class.java)
            startActivity(intent)
        }

        val query = myRef.orderByChild("idLayanan").limitToLast(100)
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    layananList.clear()
                    for (dataSnapshot in snapshot.children){
                        val layanan = dataSnapshot.getValue(ModelLayanan::class.java)
                        layananList.add(layanan!!)
                    }
                    val adapter = DataLayananAdapter(layananList, this@DataLayananActivity)
                    rvDataLayanan.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataLayananActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}