package eu.dave.parkcar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Trova i bottoni nel layout dell'Activity
        val mapButton = findViewById<Button>(R.id.mapButton)
        val parkingListButton = findViewById<Button>(R.id.parkingListButton)

        // Imposta il listener per il pulsante della mappa
        mapButton.setOnClickListener {
            showMapFragment()
        }

        // Imposta il listener per il pulsante dell'elenco dei parcheggi
        parkingListButton.setOnClickListener {
            showParkingListFragment()
        }

        // Mostra inizialmente il Fragment della mappa
        showMapFragment()
    }

    private fun showMapFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val mapFragment = MapFragment()
        fragmentTransaction.replace(R.id.fragmentContainer, mapFragment)
        fragmentTransaction.commit()
    }

    private fun showParkingListFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val parkingListFragment = ParkingListFragment()
        fragmentTransaction.replace(R.id.fragmentContainer, parkingListFragment)
        fragmentTransaction.commit()
    }


}