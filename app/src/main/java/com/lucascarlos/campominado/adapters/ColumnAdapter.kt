package com.lucascarlos.campominado.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lucascarlos.campominado.databinding.ColumnItemBinding
import com.lucascarlos.campominado.model.Column
import com.lucascarlos.campominado.model.Field

class ColumnAdapter(private val context: Context) :
    RecyclerView.Adapter<ColumnAdapter.ViewHolder>() {

    private var columnList = listOf<Column>()

    fun setListData(data: List<Column>) {
        this.columnList = data
    }

    inner class ViewHolder(val binding: ColumnItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ColumnItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = columnList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentColumn = columnList[position]

        with(holder) {
            setFieldsItemRecycler(binding.recyclerField, currentColumn.field.toList())
        }
    }

    private fun setFieldsItemRecycler(recyclerView: RecyclerView, fieldsList: List<Field>) {
        val itemRecyclerAdapter = FieldAdapter(context, fieldsList)

        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        recyclerView.adapter = itemRecyclerAdapter

    }
}