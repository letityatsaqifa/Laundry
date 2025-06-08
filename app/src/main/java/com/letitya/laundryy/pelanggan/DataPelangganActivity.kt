package com.letitya.laundryy.pelanggan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
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
import com.letitya.laundryy.Fragment.TambahPelangganFragment
import com.letitya.laundryy.R
import com.letitya.laundryy.adapter.DataPelangganAdapter
import com.letitya.laundryy.modeldata.ModelPelanggan

class DataPelangganActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    lateinit var rvDataPelanggan: RecyclerView
    lateinit var DataPelanggan: FloatingActionButton
    lateinit var pelangganList: ArrayList<ModelPelanggan>
    private var isLandscape = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pelanggan)

        isLandscape = resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

        rvDataPelanggan = findViewById(R.id.rvData_Pelanggan)
        DataPelanggan = findViewById(R.id.fabDataPenggunaTambah)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDataPelanggan.layoutManager = layoutManager
        rvDataPelanggan.setHasFixedSize(true)
        pelangganList = arrayListOf<ModelPelanggan>()

        DataPelanggan.setOnClickListener {
            showForm(null)
        }

        val query = myRef.orderByChild("idPelanggan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    pelangganList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val pelanggan = dataSnapshot.getValue(ModelPelanggan::class.java)
                        pelangganList.add(pelanggan!!)
                    }
                    val adapter = DataPelangganAdapter(pelangganList, this@DataPelangganActivity) { pelanggan ->
                        showForm(pelanggan)
                    }
                    rvDataPelanggan.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataPelangganActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showForm(pelanggan: ModelPelanggan?) {
        val formFragment = if (pelanggan == null) {
            TambahPelangganFragment.newInstance(
                getString(R.string.pelanggan_tambah),
                "",
                "",
                "",
                "",
                ""
            )
        } else {
            TambahPelangganFragment.newInstance(
                "Edit Pelanggan",
                pelanggan.idPelanggan ?: "",
                pelanggan.namaPelanggan ?: "",
                pelanggan.noHPPelanggan ?: "",
                pelanggan.alamatPelanggan ?: "",
                pelanggan.cabang ?: ""
            )
        }

        if (isLandscape) {
            // Make sure container is visible
            findViewById<FrameLayout>(R.id.form_container).visibility = View.VISIBLE

            // Replace fragment with animation
            supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.form_container, formFragment)
                .commit()
        } else {
            val intent = Intent(this, TambahPelangganActivity::class.java).apply {
                if (pelanggan != null) {
                    putExtra("judul", "Edit Pelanggan")
                    putExtra("idPelanggan", pelanggan.idPelanggan)
                    putExtra("namaPelanggan", pelanggan.namaPelanggan)
                    putExtra("noHPPelanggan", pelanggan.noHPPelanggan)
                    putExtra("alamatPelanggan", pelanggan.alamatPelanggan)
                    putExtra("cabang", pelanggan.cabang)
                } else {
                    putExtra("judul", getString(R.string.pelanggan_tambah))
                    putExtra("idPelanggan", "")
                    putExtra("namaPelanggan", "")
                    putExtra("noHPPelanggan", "")
                    putExtra("alamatPelanggan", "")
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

                // Hide the container after removal
                findViewById<FrameLayout>(R.id.form_container).visibility = View.GONE
            }
        }
    }
}