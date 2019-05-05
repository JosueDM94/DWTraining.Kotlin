package com.training.droidbountyhunter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.training.data.DatabaseBountyHunter
import com.training.models.Fugitivo
import kotlinx.android.synthetic.main.activity_agregar.*

class AgregarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar)
    }

    fun guardarFugitivoPresionado(view: View) {
        val nombre = nombreFugitivoTextView.text.toString()
        if (nombre.isNotEmpty()) {
            val database = DatabaseBountyHunter(this)
            database.insertarFugitivo(Fugitivo(0, nombre, 0))
            setResult(0)
            finish()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Alerta")
                .setMessage("Favor de capturar el nombre del fugitivo.")
                .show()
        }
    }
}
