package br.edu.utfpr.usandogps_pos2025

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import br.edu.utfpr.usandogps_pos2025.databinding.ActivityMapsBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

        val latitude = intent.getDoubleExtra( "latitude" , 0.0 )
        val longitude = intent.getDoubleExtra( "longitude", 0.0  )

        recuperaEndereco( latitude, longitude )


    }

    fun recuperaEndereco( latitude : Double, longitude : Double ) {
        Thread {
            val endereco = "https://maps.googleapis.com/maps/api/geocode/xml?latlng=${latitude},${longitude}&key=AIzaSyCMzWccWPPD5Q8mKmyk0AVx3e-_SgTakpA"

            val url = URL( endereco )
            val urlConnection = url.openConnection()

            val inputStream = urlConnection.getInputStream()
            val entrada = BufferedReader( InputStreamReader( inputStream ) )

            val saida = StringBuilder()

            var linha = entrada.readLine()

            while ( linha != null ) {
                saida.append( linha )
                linha = entrada.readLine()
            }

            val rua = saida.substring(
                saida.indexOf( "<formatted_address>" ) + 19,
                saida.indexOf( "</formatted_address>")
            )

            runOnUiThread {
                val local = LatLng(latitude, longitude )
                mMap.addMarker(MarkerOptions().position(local).title( rua ) )
                mMap.moveCamera(CameraUpdateFactory.newLatLng(local))

                mMap.setMapType( GoogleMap.MAP_TYPE_SATELLITE )
            }

        }.start()
    }
}