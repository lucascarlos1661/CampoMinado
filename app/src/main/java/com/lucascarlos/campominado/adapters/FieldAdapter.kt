package com.lucascarlos.campominado.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lucascarlos.campominado.databinding.FieldItemBinding
import com.lucascarlos.campominado.model.Field

class FieldAdapter(private val context: Context, private val fieldList: List<Field>) :
    RecyclerView.Adapter<FieldAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: FieldItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FieldItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = fieldList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentField = fieldList[position]
        with(holder.binding) {
            when {
                currentField.opened -> {
                    regularField.visibility = View.GONE
                    openedField.visibility = View.VISIBLE
                }
                currentField.mined -> {
                    //mine.visibility = View.VISIBLE
                }
                currentField.exploded -> {
                    regularField.visibility = View.GONE
                    openedField.visibility = View.VISIBLE
                    minedField.visibility = View.VISIBLE
                }
                currentField.flagged -> {
                    flag.visibility = View.VISIBLE
                }
            }
        }
    }
}