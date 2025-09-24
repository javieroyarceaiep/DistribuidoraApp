package com.example.distribuidoraapp

data class Ubicacion(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
