package com.letitya.laundryy.pegawai

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
import com.letitya.laundryy.Fragment.TambahPegawaiFragment
import com.letitya.laundryy.R
import com.letitya.laundryy.adapter.DataPegawaiAdapter
import com.letitya.laundryy.modeldata.ModelPegawai

class DataPegawaiActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")
    lateinit var rvDataPegawai: RecyclerView
    lateinit var dataPegawai: FloatingActionButton
    lateinit var pegawaiList: ArrayList<ModelPegawai>
    private var isLandscape = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pegawai)

        // Check if we're in landscape mode
        isLandscape = resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

        rvDataPegawai = findViewById(R.id.rvData_Pegawai)
        dataPegawai = findViewById(R.id.fabDataPegawaiTambah)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDataPegawai.layoutManager = layoutManager
        rvDataPegawai.setHasFixedSize(true)
        pegawaiList = arrayListOf<ModelPegawai>()

        dataPegawai.setOnClickListener {
            showForm(null)
        }

        val query = myRef.orderByChild("idPegawai").limitToLast(100)
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    pegawaiList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val pegawai = dataSnapshot.getValue(ModelPegawai::class.java)
                        pegawaiList.add(pegawai!!)
                    }
                    val adapter = DataPegawaiAdapter(pegawaiList, this@DataPegawaiActivity) { pegawai ->
                        showForm(pegawai)
                    }
                    rvDataPegawai.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataPegawaiActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showForm(pegawai: ModelPegawai?) {
        val formFragment = if (pegawai == null) {
            TambahPegawaiFragment.newInstance(
                getString(R.string.pegawai_tambah),
                "",
                "",
                "",
                "",
                ""
            )
        } else {
            TambahPegawaiFragment.newInstance(
                "Edit Pegawai",
                pegawai.idPegawai ?: "",
                pegawai.namaPegawai ?: "",
                pegawai.noHPPegawai ?: "",
                pegawai.alamatPegawai ?: "",
                pegawai.cabangPegawai ?: ""
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
            val intent = Intent(this, TambahPegawaiActivity::class.java).apply {
                if (pegawai != null) {
                    putExtra("judul", "Edit Pegawai")
                    putExtra("idPegawai", pegawai.idPegawai)
                    putExtra("namaPegawai", pegawai.namaPegawai)
                    putExtra("noHPPegawai", pegawai.noHPPegawai)
                    putExtra("alamatPegawai", pegawai.alamatPegawai)
                    putExtra("cabangPegawai", pegawai.cabangPegawai)
                } else {
                    putExtra("judul", getString(R.string.pegawai_tambah))
                    putExtra("idPegawai", "")
                    putExtra("namaPegawai", "")
                    putExtra("noHPPegawai", "")
                    putExtra("alamatPegawai", "")
                    putExtra("cabangPegawai", "")
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