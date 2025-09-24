package com.example.distribuidoraapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<Button>(R.id.btnCompra).setOnClickListener {
            startActivity(Intent(this, CompraActivity::class.java))
        }
        findViewById<Button>(R.id.btnMonitoreo).setOnClickListener {
            startActivity(Intent(this, MonitoreoActivity::class.java))
        }
    }
}
