package eu.dave.parkcar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.dave.parkcar.repository.DatabaseHelper
import eu.dave.parkcar.repository.ParkingListAdapter
import eu.dave.parkcar.R

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
            // TODO implementare l'evento cliccabile
        }
        recyclerView.adapter = parkingListAdapter
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


