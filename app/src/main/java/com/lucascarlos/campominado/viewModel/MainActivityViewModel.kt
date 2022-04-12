package com.lucascarlos.campominado.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucascarlos.campominado.model.Column
import com.lucascarlos.campominado.model.Field
import com.lucascarlos.campominado.src.Params

class MainActivityViewModel : ViewModel() {

    private val params = Params()

    private var _board: MutableLiveData<List<Column>> = MutableLiveData()

    init {
        createBoard()
    }

    fun getBoardObserver(): MutableLiveData<List<Column>> {
        return _board
    }

    private fun createBoard() {
        val initialBoard =
            List(12) { currentColumn ->
                Column(
                    List(20) { currentRow ->
                        Field(
                            row = currentRow,
                            column = currentColumn,
                            opened = false,
                            flagged = false,
                            mined = false,
                            exploded = false,
                            nearMines = 0
                        )
                    }
                )
            }
        spreadMines(initialBoard, 10)
    }

    private fun spreadMines(board: List<Column>, minesAmount: Int) {
        val columns = board.size - 1
        val rows = board[0].field.size - 1
        var minesPlanted = 0

        while (minesPlanted < minesAmount) {
            val rowSel = (0..rows).random()
            val columnSel = (0..columns).random()

            if (!board[columnSel].field[rowSel].mined) {
                board[columnSel].field[rowSel].mined = true
                minesPlanted++
            }
        }
        _board.postValue(board)
    }

    private fun getNeighbors(column: Int, row: Int): MutableList<Field> {
        val tempBoard = _board.value
        val neighbors = mutableListOf<Field>()
        val columns = listOf(column - 1, column, column + 1)
        val rows = listOf(row - 1, row, row + 1)

        columns.forEach { c ->
            rows.forEach { r ->
                var different = false
                if (c != column || r != row)
                    different = true

                var validRow = false
                if (r >= 0 && r < tempBoard?.get(0)?.field?.size!!)
                    validRow = true

                var validColumn = false
                if (c >= 0 && c < tempBoard?.size!!)
                    validColumn = true

                if (different && validRow && validColumn) {
                    neighbors.add(tempBoard?.get(c)?.field?.get(r)!!)
                }
            }
        }
        return neighbors
    }

    private fun safeNeighborhood(column: Int, row: Int): Boolean {
        val neighbors: List<Field> = getNeighbors(column, row)

        val neighborsMined = neighbors.filter { currentField ->
            currentField.mined
        }
        if (neighborsMined.isNotEmpty()) {
            return false
        }
        return true
    }

    fun openField(column: Int, row: Int) {
        val tempBoard = _board.value
        val field = tempBoard?.get(column)?.field?.get(row)

        if (field != null) {
            if (!field.opened) {
                field.opened = true
                when {
                    field.mined -> {
                        field.exploded = true
                    }
                    safeNeighborhood(column, row) -> {
                        val neighbors = getNeighbors(column, row)
                        neighbors.forEach { n ->
                            openField(n.column, n.row)
                        }
                    }
                    else -> {
                        val neighbors = getNeighbors(column, row)
                        field.nearMines = neighbors.filter { n -> n.mined }.size
                    }
                }
            }
        }
        _board.postValue(tempBoard)
    }

    fun flagField(column: Int, row: Int) {
        val tempBoard = _board.value
        val field = tempBoard?.get(column)?.field?.get(row)

        if (field != null) {
            if (!field.opened) {
                field.flagged = !field.flagged
            }
        }
        _board.postValue(tempBoard)
    }
}