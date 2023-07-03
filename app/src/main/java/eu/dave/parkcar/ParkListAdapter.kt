package eu.dave.parkcar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParkListAdapter(private var parkList: List<Park>, private val onItemClick: (Park) -> Unit) : RecyclerView.Adapter<ParkListAdapter.ParkingViewHolder>() {

    class ParkingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.park_item, parent, false)
        return ParkingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ParkingViewHolder, position: Int) {
        val park = parkList[position]
        holder.nameTextView.text = park.name

        holder.itemView.setOnClickListener {
            onItemClick(park)
        }
    }

    override fun getItemCount(): Int {
        return parkList.size
    }

    fun updateList(updatedParkList: List<Park>) {
        val oldSize = parkList.size
        parkList = updatedParkList
        val newSize = parkList.size

        if (oldSize == newSize) {
            for (index in 0 until oldSize) {
                if (parkList[index] != updatedParkList[index]) {
                    notifyItemChanged(index)
                }
            }
        } else {
            notifyDataSetChanged()
        }
    }
}
