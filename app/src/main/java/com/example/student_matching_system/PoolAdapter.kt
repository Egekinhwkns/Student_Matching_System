package com.example.student_matching_system

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class poolAdapter(val tmpList : MutableList<studentModel>, private val clickListener: (studentModel) -> Unit) : RecyclerView.Adapter<poolAdapter.Holder>() {

    class Holder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val pp = itemView.findViewById<ImageView>(R.id.ppPoolItem)
        val poolName = itemView.findViewById<TextView>(R.id.textPoolItem)
        val poolStatus = itemView.findViewById<TextView>(R.id.statusTv)

        fun bindItems(item : studentModel){

            poolName.text = item.poolName
            poolStatus.text = item.poolStatus

            if(item.pp != null){
                pp.setImageBitmap(
                    BitmapFactory.decodeByteArray(
                        item.pp,
                        0,
                        item.pp.size
                    )
                )
            }
            else{
                pp.setImageResource(R.drawable.empty)
            }
            pp.clipToOutline = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_student_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return tmpList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindItems(tmpList.get(position))
        holder.itemView.setOnClickListener{
            clickListener(tmpList.get(position))
        }
    }
}