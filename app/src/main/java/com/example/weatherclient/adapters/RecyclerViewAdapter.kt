package com.example.weatherclient.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherclient.model.DailyWeather
import com.example.weatherclient.R
import java.text.DecimalFormat

class RecyclerViewAdapter(mContext: Context) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private var df : DecimalFormat = DecimalFormat("#.#")
    var data: MutableList<DailyWeather> = mutableListOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.dailyWeatherImage)
        var textView: TextView = itemView.findViewById(R.id.dailyWeatherTextView)
        var textViewDate: TextView = itemView.findViewById(R.id.dailyWeatherDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_listitem, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = df.format(data[position].temp) + "℃"
        holder.imageView.setImageResource(data[position].detail)
        holder.textViewDate.text = data[position].date
    }

    override fun getItemCount(): Int {
        return data.size
    }
}