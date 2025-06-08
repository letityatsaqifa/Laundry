package com.letitya.laundryy

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.letitya.laundryy.modeldata.ModelUser

class RegistrasiActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")
    private lateinit var etNama: EditText
    private lateinit var etNoHP: EditText
    private lateinit var etPassword: EditText
    private lateinit var etCabang: EditText
    private lateinit var etRole: EditText
    private lateinit var btSimpan: Button
    private lateinit var tvRegistrasi: TextView

    private val adminPassword = "apahayo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrasi)

        showPasswordVerificationDialog()

        etNama = findViewById(R.id.etNama)
        etNoHP = findViewById(R.id.etNo)
        etPassword = findViewById(R.id.etPw)
        etCabang = findViewById(R.id.etCabang)
        etRole = findViewById(R.id.etRole)
        btSimpan = findViewById(R.id.btLogin)
        tvRegistrasi = findViewById(R.id.tvKonfirRegister)

        setFormEnabled(false)

        tvRegistrasi.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btSimpan.setOnClickListener {
            registerUser()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showPasswordVerificationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.mod_sandi, null)
        val etPassword = dialogView.findViewById<EditText>(R.id.etVerificationPassword)
        val btnVerify = dialogView.findViewById<Button>(R.id.btnVerify)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnVerify.setOnClickListener {
            val inputPassword = etPassword.text.toString().trim()

            if (inputPassword == adminPassword) {
                setFormEnabled(true)
                dialog.dismiss()
            } else {
                Toast.makeText(this, getString(R.string.sandiSalah), Toast.LENGTH_SHORT).show()
                etPassword.text.clear()
                etPassword.requestFocus()
            }
        }

        dialog.show()
    }

    private fun setFormEnabled(enabled: Boolean) {
        etNama.isEnabled = enabled
        etNoHP.isEnabled = enabled
        etPassword.isEnabled = enabled
        etCabang.isEnabled = enabled
        etRole.isEnabled = enabled
        btSimpan.isEnabled = enabled
    }

    private fun registerUser() {
        val nama = etNama.text.toString().trim()
        val nomorHP = etNoHP.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val cabang = etCabang.text.toString().trim()
        val role = etRole.text.toString().trim()

        when {
            nama.isEmpty() -> {
                etNama.error = getString(R.string.namaKosong)
                return
            }
            nomorHP.isEmpty() -> {
                etNoHP.error = getString(R.string.noKosong)
                return
            }
            password.isEmpty() -> {
                etPassword.error = getString(R.string.passKosong)
                return
            }
            cabang.isEmpty() -> {
                etCabang.error = getString(R.string.cabangkosong)
                return
            }
            role.isEmpty() -> {
                etRole.error = getString(R.string.roleKosong)
                return
            }
            !nomorHP.matches(Regex("^[0-9]{10,15}$")) -> {
                etNoHP.error = getString(R.string.maxNo)
                return
            }
            password.length < 6 -> {
                etPassword.error = getString(R.string.minPass)
                return
            }
        }

        val progressDialog = ProgressDialog(this).apply {
            setMessage(getString(R.string.mendaftar))
            setCancelable(false)
            show()
        }

        usersRef.orderByChild("noHP").equalTo(nomorHP).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()

                if (snapshot.exists()) {
                    etNoHP.error = getString(R.string.noTerdaftar)
                    Toast.makeText(this@RegistrasiActivity, getString(R.string.noDigunakan), Toast.LENGTH_LONG).show()
                } else {
                    val userId = usersRef.push().key!!
                    val user = ModelUser(
                        idUser = userId,
                        nama = nama,
                        noHP = nomorHP,
                        password = password,
                        role = role,
                        idCabang = cabang
                    )

                    usersRef.child(userId).setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(this@RegistrasiActivity, getString(R.string.registerBerhasil), Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegistrasiActivity, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@RegistrasiActivity,
                                "Gagal registrasi: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Log.e("RegisterError", "Database error: ${error.message}")
                Toast.makeText(
                    this@RegistrasiActivity,
                    "Terjadi kesalahan: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}