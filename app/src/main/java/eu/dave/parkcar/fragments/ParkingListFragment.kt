package eu.dave.parkcar.fragments

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.dave.parkcar.R
import eu.dave.parkcar.entity.Parking
import eu.dave.parkcar.repository.DatabaseHelper
import eu.dave.parkcar.repository.ParkingListAdapter

class ParkingListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var parkingListAdapter: ParkingListAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parking_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        databaseHelper = DatabaseHelper(requireContext())
        val parkList = databaseHelper.getAllParkings()

        parkingListAdapter = ParkingListAdapter(parkList) { clickedParking ->
            showParkingDetailsDialog(clickedParking)
        }
        recyclerView.adapter = parkingListAdapter
    }

    private fun showParkingDetailsDialog(parking: Parking) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_parking_list)

        val btnShare = dialog.findViewById<Button>(R.id.btnShareParkingList)
        val btnCenter = dialog.findViewById<Button>(R.id.btnCenterParkingList)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancelParkingList)
        val txtParkingDetails = dialog.findViewById<TextView>(R.id.txtParkingDetails)

        txtParkingDetails.text = "Nome: ${parking.name}\nLatitudine: ${parking.latitude}\nLongitudine: ${parking.longitude}"

        btnShare.setOnClickListener { shareLocation(parking) }
        btnCancel.setOnClickListener {
            databaseHelper.deleteParking(parking.id)
            refreshList()
            dialog.dismiss()
        }
        // TODO implement btnCenter

        dialog.show()
    }

    fun refreshList() {
        val updatedParkingList = databaseHelper.getAllParkings()
        parkingListAdapter.updateList(updatedParkingList)
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun shareLocation(parking: Parking) {
        val shareText =
            "La posizione del parcheggio ${parking.name}: ${parking.latitude}, ${parking.longitude}"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val chooserIntent = Intent.createChooser(sendIntent, "Condividi la posizione")

        try {
            startActivity(chooserIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "Nessuna app disponibile per la condivisione",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}


