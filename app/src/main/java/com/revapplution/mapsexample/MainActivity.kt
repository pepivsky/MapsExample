package com.revapplution.mapsexample

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener {

    private lateinit var map: GoogleMap

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createFragment()
    }

    private fun createFragment() {
        val mapFragment: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment?.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap //crea el mapa
        map.setOnMyLocationButtonClickListener(this)
        enableLocation() //activa la localizacion
        //me lleva a mi localizacion
        onMyLocationButtonClick()


    }

    private fun createMarker(lattitude: Double, longittude: Double) {
        val coordinates = LatLng(lattitude, longittude)
        val marker = MarkerOptions().position(coordinates).title("Mi ubicacion actual")
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            2000,
            null
        )
    }

    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation() {
        if (!::map.isInitialized) return //si el mapa nop esta creado no ejecutamos pedir permiso

        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            //pedir permiso
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) { //si entra aqui significa que no acepto los permisos
            Toast.makeText(this, "Ve a ajustes y acpeta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            //pedir por primera vez el permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    //captura si el usuario acepta los permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permiso aceptado
                map.isMyLocationEnabled = true
            } else {
                //no acepto el permiso
                Toast.makeText(this, "Ve a ajustes y acpeta los permisos", Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
            }
        }
    }

    override fun onResumeFragments() { //comprobar que los permisos siguen activados si el usuario se va de la app y regresa
        super.onResumeFragments()
        if (::map.isInitialized) return
        if (!isLocationPermissionGranted()) { //si el permiso no esta activado
            map.isMyLocationEnabled = false
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Set my ubication", Toast.LENGTH_SHORT).show()
        return false

    }


}