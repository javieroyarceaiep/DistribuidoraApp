package com.example.distribuidoraapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CompraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compra)

        val etTotal = findViewById<EditText>(R.id.etTotal)
        val etKm = findViewById<EditText>(R.id.etKm)
        val btn = findViewById<Button>(R.id.btnCalcular)
        val tv = findViewById<TextView>(R.id.tvResultado)

        btn.setOnClickListener {
            val total = etTotal.text.toString().toIntOrNull() ?: 0
            val km = etKm.text.toString().toIntOrNull() ?: 0
            val costo = when {
                total >= 50_000 && km <= 20 -> 0
                total in 25_000..49_999 -> km * 150
                else -> km * 300
            }
            tv.text = "Costo de despacho: $costo CLP"
        }
    }
}
