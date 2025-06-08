package com.letitya.laundryy

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import com.letitya.laundryy.modeldata.ModelUser

class LoginActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")
    private lateinit var etNo: EditText
    private lateinit var etPw: EditText
    private lateinit var btLogin: Button
    private lateinit var tvKonfirRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        if (sharedPref.getString("user_id", null) != null) {
            startActivity(Intent(this, LaundryActivity::class.java))
            finish()
            return
        }

        etNo = findViewById(R.id.etNo)
        etPw = findViewById(R.id.etPw)
        btLogin = findViewById(R.id.btLogin)
        tvKonfirRegister = findViewById(R.id.tvKonfirRegister)

        btLogin.setOnClickListener {
            loginUser()
        }

        tvKonfirRegister.setOnClickListener {
            startActivity(Intent(this, RegistrasiActivity::class.java))
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loginUser() {
        val noHP = etNo.text.toString().trim()
        val password = etPw.text.toString().trim()

        if (noHP.isEmpty()) {
            etNo.error = getString(R.string.noKosong)
            return
        }

        if (password.isEmpty()) {
            etPw.error = getString(R.string.passKosong)
            return
        }

        val progressDialog = ProgressDialog(this).apply {
            setMessage(getString(R.string.memeriksa))
            setCancelable(false)
            show()
        }

        usersRef.orderByChild("noHP").equalTo(noHP).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(ModelUser::class.java)
                        if (user != null && user.password == password) {
                            val sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("user_id", user.idUser)
                                putString("namaPegawai", user.nama)
                                putString("noHpPegawai", user.noHP)
                                putString("idCabang", user.idCabang)
                                putString("user_role", user.role)
                                apply()
                            }

                            Toast.makeText(this@LoginActivity, getString(R.string.LoginBerhasil), Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, LaundryActivity::class.java))
                            finish()
                            return
                        }
                    }
                }
                Toast.makeText(this@LoginActivity, getString(R.string.noPassSalah), Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}