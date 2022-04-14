package com.lucascarlos.campominado

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lucascarlos.campominado.adapters.ColumnAdapter
import com.lucascarlos.campominado.databinding.ActivityMainBinding
import com.lucascarlos.campominado.viewModel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var recyclerViewAdapter: ColumnAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        binding.apply {
            recyclerFields.apply {
                layoutManager =
                    LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL, false)
                recyclerViewAdapter = ColumnAdapter(this@MainActivity) { viewModelStore }
                adapter = recyclerViewAdapter
            }
        }

        viewModel.getBoardObserver().observe(this) {
            recyclerViewAdapter.setListData(it)
            recyclerViewAdapter.notifyDataSetChanged()
        }

        viewModel.getFlagsAmountObserver().observe(this) {
            binding.flagCounter.text = viewModel.flagsAmount.value.toString()
        }
    }
}