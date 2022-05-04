package com.lucascarlos.campominado.adapters

import android.content.Context
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
    private var safeNeighborhood: Boolean? = null

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
            setField(currentField, this)

            field.setOnClickListener {
                openField(currentField)
            }

            field.setOnLongClickListener {
                flagField(currentField)
                return@setOnLongClickListener true
            }
        }
    }

    private fun openField(currentField: Field) {
        if (currentField.flagged or currentField.opened) return
        viewModel.viewModelScope.launch {
            safeNeighborhood = viewModel.openField(currentField.column, currentField.row)
        }
        if (safeNeighborhood == false) {
            notifyItemChanged(currentField.row)
        } else {
            viewModel.viewModelScope.launch {
                viewModel.updateBoard()
            }
        }
    }

    private fun flagField(currentField: Field) {
        if (currentField.opened) {
            return
        }
        viewModel.viewModelScope.launch {
            viewModel.flagField(currentField.column, currentField.row)
        }
        notifyItemChanged(currentField.row)
    }

    private fun setField(currentField: Field, binding: FieldItemBinding) {
        with(binding) {
            when {
                currentField.opened && currentField.flagged && !currentField.mined -> {
                    fieldImageView.setImageResource(R.drawable.not_mined_field)
                }
                currentField.opened && currentField.flagged && currentField.mined -> {
                    flag.visibility = View.VISIBLE
                }
                currentField.opened && currentField.mined && !currentField.exploded -> {
                    mine.visibility = View.VISIBLE
                }
                currentField.exploded -> {
                    fieldImageView.setImageResource(R.drawable.mined_field)
                }
                currentField.flagged -> {
                    flag.visibility = View.VISIBLE
                }
                currentField.opened -> {
                    fieldImageView.setImageResource(R.drawable.opened_field)
                    if (currentField.nearMines > 0) {
                        nearMines.text = currentField.nearMines.toString()
                        nearMines.setTextColor(getNearMinesTextColor(currentField.nearMines))
                        nearMines.visibility = View.VISIBLE
                    } else {
                        nearMines.visibility = View.GONE
                    }
                }
            }
            if (!currentField.flagged) flag.visibility = View.GONE
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