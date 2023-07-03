package eu.dave.parkcar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ParkListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var parkListAdapter: ParkListAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_park_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        databaseHelper = DatabaseHelper(requireContext())
        val parkList = databaseHelper.getAllParks()

        parkListAdapter = ParkListAdapter(parkList) { clickedPark ->
            // TODO implementare l'evento cliccabile
        }
        recyclerView.adapter = parkListAdapter
    }

    fun refreshList() {
        val updatedParkingList = databaseHelper.getAllParks()
        parkListAdapter.updateList(updatedParkingList)
    }
}


