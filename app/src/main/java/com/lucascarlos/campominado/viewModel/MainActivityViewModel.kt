package com.lucascarlos.campominado.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucascarlos.campominado.model.Column
import com.lucascarlos.campominado.model.Field
import com.lucascarlos.campominado.src.Params

class MainActivityViewModel : ViewModel() {

    private val params = Params()

    var board: MutableLiveData<List<Column>> = MutableLiveData()
    var gameDifficultySelected: MutableLiveData<Int> = MutableLiveData(0)

    private var columnsAmount: Int = params.getColumnsAmount()
    private var rowsAmount: Int = params.getRowsAmount()

    private var minesAmount: Int = params.getMinesAmount(gameDifficultySelected.value!!)
    var flagCounter: MutableLiveData<Int> = MutableLiveData(minesAmount)

    private var fieldNonMinedAmount: Int = rowsAmount * columnsAmount - minesAmount
    private var fieldOpenedAmount: Int = 0

    var gameOver: MutableLiveData<Boolean> = MutableLiveData()
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

    private fun spreadMines(initialBoard: List<Column>, minesAmount: Int) {
        val columns = initialBoard.size - 1
        val rows = initialBoard[0].field.size - 1
        var minesPlanted = 0

        while (minesPlanted < minesAmount) {
            val rowSel = (0..rows).random()
            val columnSel = (0..columns).random()

            if (!initialBoard[columnSel].field[rowSel].mined) {
                initialBoard[columnSel].field[rowSel].mined = true
                minesPlanted++
            }
        }
        board.value = initialBoard
    }

    private fun getNeighbors(column: Int, row: Int): MutableList<Field> {
        val neighbors = mutableListOf<Field>()
        val columns = listOf(column - 1, column, column + 1)
        val rows = listOf(row - 1, row, row + 1)

        columns.forEach { c ->
            rows.forEach { r ->
                var different = false
                if (c != column || r != row)
                    different = true

                var validRow = false
                if (r >= 0 && r < board.value?.get(0)?.field?.size!!)
                    validRow = true

                var validColumn = false
                if (c >= 0 && c < board.value?.size!!)
                    validColumn = true

                if (different && validRow && validColumn) {
                    neighbors.add(board.value?.get(c)?.field?.get(r)!!)
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

    fun openField(column: Int, row: Int): (Boolean?) {

        if (gameOver.value == true) return null

        val field = board.value?.get(column)?.field?.get(row)

        if (field != null && !field.flagged && !field.opened) {
            field.opened = true
            fieldOpenedAmount++

            if (fieldOpenedAmount == fieldNonMinedAmount) {
                wonGame()
            }
            when {
                field.mined -> {
                    field.exploded = true
                    gameOver()
                    return null
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
                    return false
                }
            }
        }
        return null
    }

    fun updateBoard() {
        board.value = board.value
    }

    fun flagField(column: Int, row: Int) {

        if (gameOver.value == true) return

        val field = board.value?.get(column)?.field?.get(row)

        if (field != null) {
            if (!field.flagged) {
                field.flagged = true
                decreaseFlagCounter()
            } else {
                field.flagged = false
                increaseFlagCounter()
            }
        }
    }

    private fun increaseFlagCounter() {
        flagCounter.value = flagCounter.value?.plus(1)
    }

    private fun decreaseFlagCounter() {
        flagCounter.value = flagCounter.value?.minus(1)
    }

    fun restartGame() {
        gameOver.value = false
        wonGame.value = false
        fieldOpenedAmount = 0
        minesAmount = params.getMinesAmount(gameDifficultySelected.value!!)
        fieldNonMinedAmount = rowsAmount * columnsAmount - minesAmount
        flagCounter.value = minesAmount
        createBoard()
    }

    private fun gameOver() {
        openAllFields()
        gameOver.value = true
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
        board.value = tempBoard
    }
}