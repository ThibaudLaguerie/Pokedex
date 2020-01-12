package com.benjaminledet.pokedex.ui.pokemon.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.benjaminledet.pokedex.R
import com.benjaminledet.pokedex.data.model.Move
import com.google.android.material.card.MaterialCardView

class MyAdapter (val moves: List<Move>, val callback: (Int) -> Unit):
RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(val v : LinearLayout) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_move, parent, false)
        val holder = MyViewHolder(layout as LinearLayout)

        return holder
    }

    override fun getItemCount(): Int = moves.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val move = moves[position]

        Log.d("Move" ,"${move.type}")
        holder.v.findViewById<TextView>(R.id.moveCardName).text = move.name.capitalize()
        holder.v.findViewById<TextView>(R.id.moveCardType).text = move.type.capitalize()
        holder.v.findViewById<TextView>(R.id.moveCardAccuracy).text = move.accuracy.toString()
        holder.v.findViewById<TextView>(R.id.moveCardPower).text = move.power.toString()
        holder.v.findViewById<TextView>(R.id.moveCardPp).text = move.pp.toString()
        holder.v.findViewById<MaterialCardView>(R.id.moveCard).setCardBackgroundColor(holder.v.context.getColor(
            switchToSetBackgroundColor(move.type, 1)))
    }

}