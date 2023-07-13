package eu.dave.parkcar.repository

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.dave.parkcar.R
import eu.dave.parkcar.entity.Parking

class ParkingListAdapter(private var parkingList: List<Parking>, private val onItemClick: (Parking) -> Unit) : RecyclerView.Adapter<ParkingListAdapter.ParkingViewHolder>() {

    class ParkingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.parking_item, parent, false)
        return ParkingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ParkingViewHolder, position: Int) {
        val parking = parkingList[position]
        holder.nameTextView.text = parking.name

        holder.itemView.setOnClickListener {
            onItemClick(parking)
        }
    }

    override fun getItemCount(): Int {
        return parkingList.size
    }

    fun updateList(updatedParkingList: List<Parking>) {
        val oldSize = parkingList.size
        parkingList = updatedParkingList
        val newSize = parkingList.size

        if (oldSize == newSize) {
            for (index in 0 until oldSize) {
                if (parkingList[index] != updatedParkingList[index]) {
                    notifyItemChanged(index)
                }
            }
        } else {
            notifyDataSetChanged()
        }
    }
}
