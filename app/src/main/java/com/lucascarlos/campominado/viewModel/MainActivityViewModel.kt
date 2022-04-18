package com.lucascarlos.campominado.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucascarlos.campominado.model.Column
import com.lucascarlos.campominado.model.Field
import com.lucascarlos.campominado.src.Params

class MainActivityViewModel : ViewModel() {

    private val params = Params()

    var board: MutableLiveData<List<Column>> = MutableLiveData()
    var flagCounter: MutableLiveData<Int> = MutableLiveData(params.getMinesAmount())
    private var columnsAmount: Int = params.getColumnsAmount()
    private var rowsAmount: Int = params.getRowsAmount()
    private var minesAmount: Int = params.getMinesAmount()
    private var fieldNonMinedAmount: Int = rowsAmount * columnsAmount - minesAmount
    private var fieldOpenedAmount: Int = 0

    var lostGame: MutableLiveData<Boolean> = MutableLiveData()
    var wonGame: MutableLiveData<Boolean> = MutableLiveData()

    init {
        createBoard()
    }

    private fun createBoard() {
        val initialBoard =
            List(columnsAmount) { currentColumn ->
                Column(
                    List(rowsAmount) { currentRow ->
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
        spreadMines(initialBoard, minesAmount)
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
        this.board.postValue(board)
    }

    private fun getNeighbors(column: Int, row: Int): MutableList<Field> {
        val tempBoard = board.value
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

        if (lostGame.value == true) return

        val tempBoard = board.value
        val field = tempBoard?.get(column)?.field?.get(row)

        if (field != null) {
            if (!field.opened) {
                field.opened = true
                fieldOpenedAmount++

                if (fieldOpenedAmount == fieldNonMinedAmount) {
                    wonGame()
                }
                when {
                    field.mined -> {
                        field.exploded = true
                        lostGame()
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
        board.postValue(tempBoard)
    }

    fun flagField(column: Int, row: Int) {

        if (lostGame.value == true) return

        val tempBoard = board.value
        val field = tempBoard?.get(column)?.field?.get(row)

        if (field != null) {
            if (!field.opened) {
                if (!field.flagged) {
                    field.flagged = true
                    decreaseFlagCounter()
                } else {
                    field.flagged = false
                    increaseFlagCounter()
                }
            }
        }
        board.postValue(tempBoard)
    }

    private fun increaseFlagCounter() {
        flagCounter.value = flagCounter.value?.plus(1)
    }

    private fun decreaseFlagCounter() {
        flagCounter.value = flagCounter.value?.minus(1)
    }

    fun restartGame() {
        lostGame.value = false
        wonGame.value = false
        fieldOpenedAmount = 0
        flagCounter.value = params.getMinesAmount()
        createBoard()
    }

    private fun lostGame() {
        openAllFields()
        lostGame.value = true
    }

    private fun wonGame() {
        openAllFields()
        wonGame.value = true
    }

    private fun openAllFields() {
        val tempBoard: List<Column>? = board.value

        tempBoard?.forEach { row ->
            row.field.forEach { field ->
                if (field.mined || field.flagged) {
                    field.opened = true
                }
            }
        }
        board.postValue(tempBoard)
    }
}