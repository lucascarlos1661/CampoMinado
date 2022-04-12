package com.lucascarlos.campominado.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.lucascarlos.campominado.R
import com.lucascarlos.campominado.databinding.FieldItemBinding
import com.lucascarlos.campominado.model.Field
import com.lucascarlos.campominado.viewModel.MainActivityViewModel
import kotlinx.coroutines.launch

class FieldAdapter(
    private val context: Context,
    private val fieldList: List<Field>,
    private val viewModelStoreOwner: ViewModelStoreOwner
) :
    RecyclerView.Adapter<FieldAdapter.ViewHolder>() {

    private lateinit var viewModel: MainActivityViewModel

    inner class ViewHolder(val binding: FieldItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FieldItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = fieldList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        viewModel = ViewModelProvider(viewModelStoreOwner).get(MainActivityViewModel::class.java)
        val currentField = fieldList[position]

        with(holder.binding) {
            when {
                currentField.exploded && currentField.exploded -> {
                    regularField.visibility = View.GONE
                    minedField.visibility = View.VISIBLE
                    return
                }
                currentField.opened -> {
                    regularField.visibility = View.GONE
                    if (currentField.nearMines > 0) {
                        nearMines.text = currentField.nearMines.toString()
                        nearMines.setTextColor(getNearMinesTextColor(currentField.nearMines))
                        nearMines.visibility = View.VISIBLE
                    } else {
                        nearMines.visibility = View.GONE
                    }
                    openedField.visibility = View.VISIBLE
                }
                currentField.flagged -> {
                    flag.visibility = View.VISIBLE
                }
            }

            field.setOnClickListener {
                if (currentField.flagged or currentField.opened) return@setOnClickListener
                viewModel.viewModelScope.launch {
                    viewModel.openField(currentField.column, currentField.row)
                }
            }

            field.setOnLongClickListener {
                viewModel.viewModelScope.launch {
                    viewModel.flagField(currentField.column, currentField.row)
                }
                true
            }
        }
    }

    private fun getNearMinesTextColor(nearMines: Int): Int {
        when (nearMines) {
            1 -> {
                return ContextCompat.getColor(context, R.color.nearMines1)
            }
            2 -> {
                return ContextCompat.getColor(context, R.color.nearMines2)
            }
            3 -> {
                return ContextCompat.getColor(context, R.color.nearMines3)
            }
            4 -> {
                return ContextCompat.getColor(context, R.color.nearMines4)
            }
            else -> {
                return ContextCompat.getColor(context, R.color.nearMines5)
            }
        }
    }
}