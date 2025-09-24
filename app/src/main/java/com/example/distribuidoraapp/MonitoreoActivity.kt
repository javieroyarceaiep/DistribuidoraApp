package com.example.distribuidoraapp

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.distribuidoraapp.databinding.ActivityMonitoreoBinding
import com.google.firebase.database.*

class MonitoreoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonitoreoBinding
    private val ref: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("sensores/camion1/temperatura")
    }
    private val limite = -10.0  // ajusta el límite deseado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoreoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temp = snapshot.getValue(Double::class.java) ?: return
                binding.tvTemp.text = "Temperatura actual: $temp °C"
                if (temp > limite) {
                    binding.tvEstado.text = "Estado: ¡ALERTA!"
                    binding.tvEstado.setTextColor(Color.RED)
                } else {
                    binding.tvEstado.text = "Estado: OK"
                    binding.tvEstado.setTextColor(Color.GREEN)
                }
            }
            override fun onCancelled(error: DatabaseError) { /* puedes loguear el error */ }
        })
    }
}
