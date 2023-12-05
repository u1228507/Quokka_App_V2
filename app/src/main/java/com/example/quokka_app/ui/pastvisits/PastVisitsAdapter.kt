package com.example.quokka_app.ui.pastvisits

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quokka_app.R
import java.text.SimpleDateFormat
import java.util.Locale

interface OnItemClickListener {
    fun onButtonClick(position: Int)
}

class PastVisitsAdapter(
    private val dataList: List<PastVisitsPostpartumDataClass>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PastVisitsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_pastvisits_cardview, parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View, private val listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.visitDateTextDateGoesHere)
        private val button: Button = itemView.findViewById(R.id.viewVisitButton)

        init {
            button.setOnClickListener {
                listener.onButtonClick(adapterPosition)
            }
        }

        fun bind(data: PastVisitsPostpartumDataClass) {
            val visitDate = data.visitDate
            Log.d("Unicorn", "Binding date: $visitDate")

            if (visitDate != null) {
                val formattedDate =
                    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(visitDate)
                textView.text = formattedDate
                Log.d("Unicorn", "Formatted date: $formattedDate")
            } else {
                Log.w("Unicorn", "visitDate is null")
                textView.text = "No date available"
            }
        }
    }
}
