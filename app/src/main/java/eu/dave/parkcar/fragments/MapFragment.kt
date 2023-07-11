package eu.dave.parkcar.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import eu.dave.parkcar.repository.DatabaseHelper
import eu.dave.parkcar.R

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var btnCenter: Button
    private lateinit var btnSave: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var databaseHelper: DatabaseHelper
    private var userMarker: Marker? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = rootView.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        btnCenter = rootView.findViewById(R.id.btnCenter)
        btnSave = rootView.findViewById(R.id.btnSaveMap)
        btnCenter.setOnClickListener { centerUserLocation() }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        databaseHelper = DatabaseHelper(requireContext())
        return rootView
    }

    private fun centerUserLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                    // Crea il marker dell'utente solo se non esiste già
                    if (userMarker == null) {
                        userMarker = googleMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title("Posizione Utente")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        )
                    } else {
                        // Aggiorna la posizione del marker dell'utente
                        userMarker?.position = latLng
                    }
                }
            }
        } else {
            Log.d("MapFragment", "Location permission not granted")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onResume()
        requestLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        userMarker?.remove()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.uiSettings.isZoomGesturesEnabled = true

        googleMap.uiSettings.isScrollGesturesEnabled = true

        btnSave.setOnClickListener {
            val latLng = googleMap.cameraPosition.target
            saveParking(latLng)
        }

    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }

    private fun saveParking(latLng: LatLng) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_save_parking, null)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
        val alertDialog = dialogBuilder.create()

        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveMap)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()

            if (name.isNotEmpty()) {
                val existingParkingByName = databaseHelper.getParkingByName(name)
                // TODO implementare existingParkingByLatLong
                if (existingParkingByName != null) {
                    // Mostra un messaggio di errore che il nome esiste già
                    showMessage("Impossibile salvare, il nome è già presente")
                } else {
                    // Il nome non esiste, esegui il salvataggio del parcheggio
                    val latitude = userMarker?.position?.latitude ?: 0.0
                    val longitude = userMarker?.position?.longitude ?: 0.0
                    val id = databaseHelper.insertParking(latitude, longitude, name)

                    // Aggiungi un segnaposto adatto nel punto di salvataggio
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(name)
                    )

                    // Mostra un messaggio di successo
                    showMessage("Parcheggio salvato con successo")
                    alertDialog.dismiss()
                }
            } else {
                // Mostra un messaggio di errore se il nome è vuoto
                showMessage("Inserisci un nome valido")
            }


        }

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}
