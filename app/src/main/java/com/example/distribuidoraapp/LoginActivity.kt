package com.example.distribuidoraapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

// URL EXACTA de tu RTDB
private val db by lazy {
    FirebaseDatabase.getInstance("https://distribuidoraapp-a0a4b-default-rtdb.firebaseio.com")
}

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val fused by lazy { LocationServices.getFusedLocationProviderClient(this) }

    private val requestLocationPerm =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) saveLocation() else Log.d("UBI","Permiso de ubicación DENEGADO")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin   = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass  = etPassword.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { t ->
                if (t.isSuccessful) {
                    val uid = auth.currentUser!!.uid

                    // 1) CREA EL NODO ubicacion/<UID> SIEMPRE (placeholder + timestamp)
                    val ref = db.getReference("ubicacion").child(uid)
                    ref.updateChildren(
                        mapOf(
                            "lat" to 0.0,
                            "lng" to 0.0,
                            "timestamp" to ServerValue.TIMESTAMP
                        )
                    )

                    // 2) Luego intenta obtener y actualizar lat/lng reales
                    ensureLocationPermissionThenSave()

                    // 3) Abre el Menú
                    startActivity(Intent(this, MenuActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        t.exception?.localizedMessage ?: "Credenciales inválidas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun ensureLocationPermissionThenSave() {
        val fineGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        val coarseGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            saveLocation()
        } else {
            requestLocationPerm.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Actualiza lat/lng en /ubicacion/<UID>. Si no hay GPS, deja el placeholder 0,0.
    @SuppressLint("MissingPermission")
    private fun saveLocation() {
        val uid = auth.currentUser?.uid ?: return
        val ref = db.getReference("ubicacion").child(uid)

        val cts = CancellationTokenSource()
        fused.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    ref.updateChildren(
                        mapOf(
                            "lat" to loc.latitude,
                            "lng" to loc.longitude,
                            // refresca timestamp
                            "timestamp" to ServerValue.TIMESTAMP
                        )
                    ).addOnSuccessListener {
                        Toast.makeText(this, "Ubicación actualizada", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Fallback: lastLocation (si tampoco hay, se queda 0,0)
                    fused.lastLocation.addOnSuccessListener { last ->
                        if (last != null) {
                            ref.updateChildren(
                                mapOf(
                                    "lat" to last.latitude,
                                    "lng" to last.longitude,
                                    "timestamp" to ServerValue.TIMESTAMP
                                )
                            ).addOnSuccessListener {
                                Toast.makeText(this, "Ubicación (lastLocation) actualizada", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.d("UBI","Fallo al tomar ubicación: ${e.localizedMessage}")
            }
    }
}
