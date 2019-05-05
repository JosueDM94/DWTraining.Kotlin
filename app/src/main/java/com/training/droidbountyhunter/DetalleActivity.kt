package com.training.droidbountyhunter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.*
import android.view.View.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.training.data.*
import com.training.models.*
import com.training.network.*
import com.training.utils.PictureTools
import com.training.utils.PictureTools.Companion.MEDIA_TYPE_IMAGE
import kotlinx.android.synthetic.main.activity_detalle.*
import org.json.JSONObject

class DetalleActivity : AppCompatActivity(), LocationListener {
    private var UDID: String? = ""
    var fugitivo: Fugitivo? = null
    var database: DatabaseBountyHunter? = null

    private var direccionImagen: Uri? = null
    private val REQUEST_CODE_PHOTO_IMAGE = 1787

    private val REQUEST_CODE_GPS = 1234
    private var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle)

        @SuppressLint("HardwareIds")
        UDID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        fugitivo = intent.extras["fugitivo"] as Fugitivo
        // Se obtiene el nombre del fugitivo del objeto fugitivo y se usa como título
        title = fugitivo!!.name + " - " + fugitivo!!.id
        // Se identifica si es Fugitivo o capturado para el mensaje...
        if (fugitivo!!.status == 0) {
            etiquetaMensaje.text = "El fugitivo sigue suelto..."
            activarGPS()
        } else {
            etiquetaMensaje.text = "Atrapado!!!"
            botonCapturar.visibility = GONE

            if (fugitivo!!.photo.isNotEmpty()) {
                val bitmap = PictureTools.decodeSampledBitmapFromUri(
                    fugitivo!!.photo,
                    200, 200
                )
                pictureFugitive.setImageBitmap(bitmap)
            }
        }
    }

    override fun onDestroy() {
        apagarGPS()
        pictureFugitive.setImageBitmap(null)
        System.gc()
        super.onDestroy()
    }

    fun OnMapClick(view: View) {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra("fugitivo", fugitivo)
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    private fun activarGPS() {
        if (isGPSActivated()) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as
                    LocationManager
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000, 0f, this
            )
            Toast.makeText(this, "Activando GPS...", Toast.LENGTH_LONG).show()
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            // BestProvider
            val provider = locationManager!!.getBestProvider(criteria, true)
            // Getting last location available
            val location = locationManager!!.getLastKnownLocation(provider)
            if (location != null) {
                fugitivo!!.latitude = location.latitude
                fugitivo!!.longitude = location.longitude
            }
        }
    }

    private fun isGPSActivated(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                // Should we show an explanation
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE_GPS
                    );
                    return false;
                } else {
                    //No explanation needed, we can request the permissions.
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE_GPS
                    );
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private fun apagarGPS() {
        if (locationManager != null) {
            try {
                locationManager!!.removeUpdates(this)
                Toast.makeText(this, "Desactivando GPS...", Toast.LENGTH_LONG)
            } catch (e: SecurityException) {
                Toast.makeText(
                    this, "Error desactivando GPS " + e.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onLocationChanged(location: Location?) {
        fugitivo!!.latitude = location!!.latitude
        fugitivo!!.longitude = location.longitude
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

    fun capturarFugitivoPresionado(view: View) {
        database = DatabaseBountyHunter(this)
        fugitivo!!.status = 1
        if (fugitivo!!.photo.isEmpty()) {
            Toast.makeText(
                this,
                "Es necesario tomar la foto antes de capturar al fugitivo",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        database!!.actualizarFugitivo(fugitivo!!)
        val services = NetworkServices(object : onTaskListener {
            override fun tareaCompletada(respuesta: String) {
                val obj = JSONObject(respuesta)
                val mensaje = obj.optString("mensaje", "")
                mensajeDeCerrado(mensaje)
            }

            override fun tareaConError(codigo: Int, mensaje: String, error: String) {
                Toast.makeText(
                    applicationContext,
                    "Ocurrio un problema en la comunicación con el WebService!!!",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        services.execute("Atrapar", UDID)
        botonCapturar.visibility = GONE
        botonEliminar.visibility = GONE
        setResult(0)
    }

    fun eliminarFugitivoPresionado(view: View) {
        database = DatabaseBountyHunter(this)
        database!!.borrarFugitivo(fugitivo!!)
        setResult(0)
        finish()
    }

    fun mensajeDeCerrado(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.create()
        builder.setTitle("Alerta!!!")
            .setMessage(mensaje)
            .setOnDismissListener {
                setResult(fugitivo!!.status)
                finish()
            }.show()
    }

    fun OnFotoClick(view: View) {
        if (PictureTools.permissionReadMemmory(this)) {
            obtenFotoDeCamara()
        }
    }

    private fun obtenFotoDeCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        direccionImagen = PictureTools.getOutputMediaFileUri(this, MEDIA_TYPE_IMAGE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, direccionImagen)
        startActivityForResult(intent, REQUEST_CODE_PHOTO_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PHOTO_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                fugitivo!!.photo = PictureTools.currentPhotoPath
                val bitmap = PictureTools
                    .decodeSampledBitmapFromUri(PictureTools.currentPhotoPath, 200, 200)
                pictureFugitive.setImageBitmap(bitmap)
            } else if (requestCode == REQUEST_CODE_GPS) {
                activarGPS()
            }
        }
    }
}