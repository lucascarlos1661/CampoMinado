package com.lucascarlos.campominado

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
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
        setSpinnerDifficulty(binding)

        binding.apply {
            recyclerFields.apply {
                layoutManager =
                    LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL, false)
                recyclerViewAdapter = ColumnAdapter(this@MainActivity) { viewModelStore }
                adapter = recyclerViewAdapter
            }

            restartGameButton.setOnClickListener {
                restartGame()
            }
        }

        viewModel.board.observe(this) {
            recyclerViewAdapter.setListData(it)
            recyclerViewAdapter.notifyDataSetChanged()
        }

        viewModel.flagCounter.observe(this) {
            binding.flagCounter.text = viewModel.flagCounter.value.toString()
        }

        viewModel.gameDifficultySelected.observe(this) {
            changeGameDifficulty()
        }

        viewModel.wonGame.observe(this) { won ->
            if (won) {
                val alert = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.won_game_title))
                    .setMessage(getString(R.string.won_game_message))
                    .setCancelable(true)
                    .setNegativeButton(getString(R.string.game_negative_button)) { _, _ ->
                    }
                    .setPositiveButton(getString(R.string.game_positive_button)) { _, _ ->
                        viewModel.lostGame.value = false
                        restartGame()
                    }
                    .show()
                alert.getButton(DialogInterface.BUTTON_POSITIVE).isAllCaps = false
                alert.getButton(DialogInterface.BUTTON_NEGATIVE).isAllCaps = false
            }
        }

        viewModel.lostGame.observe(this) { lose ->
            if (lose) {
                val alert = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.lost_game_title))
                    .setMessage(getString(R.string.lost_game_message))
                    .setCancelable(true)
                    .setNegativeButton(getString(R.string.game_negative_button)) { _, _ ->
                    }
                    .setPositiveButton(getString(R.string.game_positive_button)) { _, _ ->
                        viewModel.lostGame.value = false
                        restartGame()
                    }
                    .show()
                alert.getButton(DialogInterface.BUTTON_POSITIVE).isAllCaps = false
                alert.getButton(DialogInterface.BUTTON_NEGATIVE).isAllCaps = false
            }
        }
    }

    private fun changeGameDifficulty() {
        restartGame()
    }

    private fun setSpinnerDifficulty(binding: ActivityMainBinding) {
        ArrayAdapter.createFromResource(
            this@MainActivity,
            R.array.difficulties,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDifficultySelector.adapter = adapter

            binding.spinnerDifficultySelector.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        viewModel.gameDifficultySelected.value = adapterView?.selectedItemPosition
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
        }
    }

    private fun restartGame() {
        viewModel.restartGame()
    }
}