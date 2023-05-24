package com.example.student_matching_system

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PoolAdapter2(val tmpList : MutableList<studentModel>, private val clickListener: (studentModel) -> Unit) : RecyclerView.Adapter<PoolAdapter2.Holder2>() {

    class Holder2(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val pp = itemView.findViewById<ImageView>(R.id.ppPoolItem)
        val poolName = itemView.findViewById<TextView>(R.id.textPoolItem)
        val poolStatus = itemView.findViewById<TextView>(R.id.statusTv)
        //val acceptButton = itemView.findViewById<Button>(R.id.acceptButton)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder2 {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_student_item, parent, false)
        return Holder2(view)
    }

    override fun onBindViewHolder(holder: Holder2, position: Int) {
        holder.bindItems(tmpList.get(position))
        //holder.acceptButton.setOnClickListener{
            //clickListener(tmpList.get(position))
        //}
        holder.itemView.setOnClickListener{
            clickListener(tmpList.get(position))
        }
    }

    override fun getItemCount(): Int {
        return tmpList.size
    }
}