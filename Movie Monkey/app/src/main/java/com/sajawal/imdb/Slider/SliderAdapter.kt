package com.sajawal.imdb.Slider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.sajawal.imdb.R

class SliderAdapter(var sliderItems: List<SliderItem>): RecyclerView.Adapter<SliderAdapter.ViewHolder> (){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        var imageView: RoundedImageView
        init{
            imageView = view.findViewById(R.id.imageSlide)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(sliderItems.get(position).image)
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }
}