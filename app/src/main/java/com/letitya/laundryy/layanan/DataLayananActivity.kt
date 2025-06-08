package com.letitya.laundryy.layanan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.letitya.laundryy.Fragment.TambahLayananFragment
import com.letitya.laundryy.R
import com.letitya.laundryy.adapter.DataLayananAdapter
import com.letitya.laundryy.modeldata.ModelLayanan

class DataLayananActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")
    lateinit var rvDataLayanan: RecyclerView
    lateinit var dataLayanan: FloatingActionButton
    lateinit var layananList: ArrayList<ModelLayanan>
    private var isLandscape = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_layanan)

        // Check if we're in landscape mode
        isLandscape = resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

        rvDataLayanan = findViewById(R.id.rvData_Layanan)
        dataLayanan = findViewById(R.id.fabDataLayananTambah)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDataLayanan.layoutManager = layoutManager
        rvDataLayanan.setHasFixedSize(true)
        layananList = arrayListOf<ModelLayanan>()

        dataLayanan.setOnClickListener {
            showForm(null)
        }

        val query = myRef.orderByChild("idLayanan").limitToLast(100)
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    layananList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val layanan = dataSnapshot.getValue(ModelLayanan::class.java)
                        layananList.add(layanan!!)
                    }
                    val adapter = DataLayananAdapter(layananList, this@DataLayananActivity) { layanan ->
                        showForm(layanan)
                    }
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

    private fun showForm(layanan: ModelLayanan?) {
        val formFragment = if (layanan == null) {
            TambahLayananFragment.newInstance(
                getString(R.string.layanan_tambah),
                "",
                "",
                "",
                ""
            )
        } else {
            TambahLayananFragment.newInstance(
                "Edit Layanan",
                layanan.idLayanan ?: "",
                layanan.namaLayanan ?: "",
                layanan.hargaLayanan ?: "",
                layanan.cabangLayanan ?: ""
            )
        }

        if (isLandscape) {
            findViewById<View>(R.id.form_container).visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.form_container, formFragment)
                .commit()
        } else {
            val intent = Intent(this, TambahLayananActivity::class.java).apply {
                if (layanan != null) {
                    putExtra("judul", "Edit Layanan")
                    putExtra("idLayanan", layanan.idLayanan)
                    putExtra("namaLayanan", layanan.namaLayanan)
                    putExtra("hargaLayanan", layanan.hargaLayanan)
                    putExtra("cabang", layanan.cabangLayanan)
                } else {
                    putExtra("judul", getString(R.string.layanan_tambah))
                    putExtra("idLayanan", "")
                    putExtra("namaLayanan", "")
                    putExtra("hargaLayanan", "")
                    putExtra("cabang", "")
                }
            }
            startActivity(intent)
        }
    }

    fun onFormFinished() {
        if (isLandscape) {
            val fragment = supportFragmentManager.findFragmentById(R.id.form_container)
            if (fragment != null) {
                supportFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .remove(fragment)
                    .commit()

                findViewById<View>(R.id.form_container).visibility = View.GONE
            }
        }
    }
}