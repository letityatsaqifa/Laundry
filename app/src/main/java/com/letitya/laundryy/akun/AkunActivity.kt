package com.letitya.laundryy.akun

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.letitya.laundryy.LaundryActivity
import com.letitya.laundryy.LoginActivity
import com.letitya.laundryy.R
import com.letitya.laundryy.modeldata.ModelUser

class AkunActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")
    private lateinit var tvIdPegawai: TextView
    private lateinit var tvNamaPegawai: TextView
    private lateinit var tvNoHP: TextView
    private lateinit var tvPassword: TextView
    private lateinit var tvCabang: TextView
    private lateinit var tvRole: TextView
    private lateinit var btnlogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_akun)

        tvIdPegawai = findViewById(R.id.tvIdPegawai2)
        tvNamaPegawai = findViewById(R.id.tvNamaPegawai2)
        tvNoHP = findViewById(R.id.tvNoHP2)
        tvPassword = findViewById(R.id.tvNamaPassword2)
        tvCabang = findViewById(R.id.tvCabang2)
        tvRole = findViewById(R.id.tvRole)
        btnlogout = findViewById(R.id.btLogin)

        val userId = intent.getStringExtra("userId") ?:
        getSharedPreferences("user_data", MODE_PRIVATE).getString("user_id", "")

        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserData(userId)

        btnlogout.setOnClickListener {
            showLogoutConfirmation()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LaundryActivity::class.java))
        finish()
    }

    private fun showLogoutConfirmation() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage(getString(R.string.konfirLogout))
            .setPositiveButton(getString(R.string.ya)) { dialog, which ->
                performLogout()
            }
            .setNegativeButton(getString(R.string.no), null)
            .create()
            .show()
    }

    private fun performLogout() {
        getSharedPreferences("user_data", MODE_PRIVATE).edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    private fun loadUserData(userId: String) {
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(ModelUser::class.java)
                    user?.let {
                        tvIdPegawai.text = it.idUser
                        tvNamaPegawai.text = it.nama
                        tvNoHP.text = it.noHP
                        tvPassword.text = "••••••••"
                        tvCabang.text = it.idCabang
                        tvRole.text = it.role
                    }
                } else {
                    Toast.makeText(this@AkunActivity, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AkunError", "Database error: ${error.message}")
                Toast.makeText(
                    this@AkunActivity,
                    "Gagal memuat data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        })
    }
}