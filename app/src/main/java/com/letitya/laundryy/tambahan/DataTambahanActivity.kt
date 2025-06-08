package com.letitya.laundryy.tambahan

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
import com.letitya.laundryy.Fragment.TambahTambahanFragment
import com.letitya.laundryy.R
import com.letitya.laundryy.adapter.DataTambahanAdapter
import com.letitya.laundryy.modeldata.ModelTambahan

class DataTambahanActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("tambahan")
    lateinit var rvDataTambahan: RecyclerView
    lateinit var dataTambahan: FloatingActionButton
    lateinit var tambahanList: ArrayList<ModelTambahan>
    private var isLandscape = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_tambahan)

        // Check if we're in landscape mode
        isLandscape = resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

        rvDataTambahan = findViewById(R.id.rvData_Tambahan)
        dataTambahan = findViewById(R.id.fabDataTambahanTambah)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDataTambahan.layoutManager = layoutManager
        rvDataTambahan.setHasFixedSize(true)
        tambahanList = arrayListOf<ModelTambahan>()

        dataTambahan.setOnClickListener {
            showForm(null)
        }

        val query = myRef.orderByChild("idTambahan").limitToLast(100)
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    tambahanList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val tambahan = dataSnapshot.getValue(ModelTambahan::class.java)
                        tambahanList.add(tambahan!!)
                    }
                    val adapter = DataTambahanAdapter(tambahanList, this@DataTambahanActivity) { tambahan ->
                        showForm(tambahan)
                    }
                    rvDataTambahan.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataTambahanActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showForm(tambahan: ModelTambahan?) {
        val formFragment = if (tambahan == null) {
            TambahTambahanFragment.newInstance(
                getString(R.string.tambahan_tambah),
                "",
                "",
                "",
                "",
                ""
            )
        } else {
            TambahTambahanFragment.newInstance(
                "Edit Tambahan",
                tambahan.idTambahan ?: "",
                tambahan.namaTambahan ?: "",
                tambahan.harga ?: "",
                tambahan.cabang ?: "",
                tambahan.status ?: ""
            )
        }

        if (isLandscape) {
            findViewById<View>(R.id.form_container).visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.form_container, formFragment)
                .commit()
        } else {
            val intent = Intent(this, TambahTambahanActivity::class.java).apply {
                if (tambahan != null) {
                    putExtra("judul", "Edit Tambahan")
                    putExtra("idTambahan", tambahan.idTambahan)
                    putExtra("namaTambahan", tambahan.namaTambahan)
                    putExtra("harga", tambahan.harga)
                    putExtra("cabang", tambahan.cabang)
                    putExtra("status", tambahan.status)
                } else {
                    putExtra("judul", getString(R.string.tambahan_tambah))
                    putExtra("idTambahan", "")
                    putExtra("namaTambahan", "")
                    putExtra("harga", "")
                    putExtra("cabang", "")
                    putExtra("status", "")
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