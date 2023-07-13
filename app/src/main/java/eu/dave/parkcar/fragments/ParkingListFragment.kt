package eu.dave.parkcar.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.dave.parkcar.repository.DatabaseHelper
import eu.dave.parkcar.repository.ParkingListAdapter
import eu.dave.parkcar.R
import eu.dave.parkcar.entity.Parking

class ParkingListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var parkingListAdapter: ParkingListAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

        // Recupera i riferimenti agli elementi del layout
        val btnShareParkingList = dialog.findViewById<Button>(R.id.btnShareParkingList)
        val btnShareCenterParkingList = dialog.findViewById<Button>(R.id.btnShareCenterParkingList)
        val btnCancelParkingList = dialog.findViewById<Button>(R.id.btnCancelParkingList)
        val txtParkingDetails = dialog.findViewById<TextView>(R.id.txtParkingDetails)

        // Imposta il testo del TextView con i dettagli del parcheggio
        txtParkingDetails.text = "Nome: ${parking.name}\nLatitudine: ${parking.latitude}\nLongitudine: ${parking.longitude}"

        // Aggiungi i listener di clic ai pulsanti, se necessario

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
}


