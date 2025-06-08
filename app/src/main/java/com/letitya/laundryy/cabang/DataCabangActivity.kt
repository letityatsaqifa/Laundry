package com.letitya.laundryy.cabang

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
import com.letitya.laundryy.Fragment.TambahCabangFragment
import com.letitya.laundryy.R
import com.letitya.laundryy.adapter.DataCabangAdapter
import com.letitya.laundryy.modeldata.ModelCabang

class DataCabangActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("cabang")
    lateinit var rvDataCabang: RecyclerView
    lateinit var dataCabang: FloatingActionButton
    lateinit var cabangList: ArrayList<ModelCabang>
    private var isLandscape = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_cabang)

        // Check if we're in landscape mode
        isLandscape = resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

        rvDataCabang = findViewById(R.id.rvData_Cabang)
        dataCabang = findViewById(R.id.fabDataCabangTambah)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDataCabang.layoutManager = layoutManager
        rvDataCabang.setHasFixedSize(true)
        cabangList = arrayListOf<ModelCabang>()

        dataCabang.setOnClickListener {
            showForm(null)
        }

        val query = myRef.orderByChild("idCabang").limitToLast(100)
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    cabangList.clear()
                    for (dataSnapshot in snapshot.children){
                        val cabang = dataSnapshot.getValue(ModelCabang::class.java)
                        cabangList.add(cabang!!)
                    }
                    val adapter = DataCabangAdapter(cabangList, this@DataCabangActivity) { cabang ->
                        showForm(cabang)
                    }
                    rvDataCabang.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataCabangActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showForm(cabang: ModelCabang?) {
        val formFragment = if (cabang == null) {
            // Create new instance for adding
            TambahCabangFragment.newInstance(
                getString(R.string.cabang_tambah),
                "",
                "",
                "",
                "",
                ""
            )
        } else {
            // Create new instance for editing
            TambahCabangFragment.newInstance(
                "Edit Cabang",
                cabang.idCabang ?: "",
                cabang.namaCabang ?: "",
                cabang.managerCabang ?: "",
                cabang.alamatCabang ?: "",
                cabang.noHP ?: ""
            )
        }

        if (isLandscape) {
            // Show in the right panel
            findViewById<View>(R.id.form_container).visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.form_container, formFragment)
                .commit()
        } else {
            // Start as new activity in portrait
            val intent = Intent(this, TambahCabangActivity::class.java).apply {
                if (cabang != null) {
                    putExtra("judul", "Edit Cabang")
                    putExtra("idCabang", cabang.idCabang)
                    putExtra("namaCabang", cabang.namaCabang)
                    putExtra("managerCabang", cabang.managerCabang)
                    putExtra("alamatCabang", cabang.alamatCabang)
                    putExtra("noHP", cabang.noHP)
                } else {
                    putExtra("judul", getString(R.string.cabang_tambah))
                    putExtra("idCabang", "")
                    putExtra("namaCabang", "")
                    putExtra("managerCabang", "")
                    putExtra("alamatCabang", "")
                    putExtra("noHP", "")
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

                // Hide the container after removal
                findViewById<View>(R.id.form_container).visibility = View.GONE
            }
        }
    }
}