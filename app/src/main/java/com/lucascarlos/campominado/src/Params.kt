package com.lucascarlos.campominado.src

import android.content.res.Resources
import android.util.DisplayMetrics
import kotlin.math.floor

class Params {

    private val blockSize: Int = 30

    // The height, width, number of rows and columns of the board are calculated according to the user's screen
    // The width of the board is equal to the total width of the user's screen minus margin horizontal
    private val marginHorizontalBoard = 5

    private var boardWidthInDp: Float =
        Resources.getSystem().displayMetrics.run { (widthPixels / density) - marginHorizontalBoard }

    // The height of the board is fixed at 85% of the total height of the user's screen
    private val boardProportion = 0.85F

    private var boardHeightInDp: Float =
        Resources.getSystem().displayMetrics.run { ((heightPixels / density) * boardProportion) }

    // The difficulty level will be calculated through the option selected by the user
    // Mine percentage is fixed. 7% for beginner, 13% for Intermediate and 20% for Expert

    private var percentageOfMines: Double = 0.0

    private val difficultLevelBeginner: Double = 0.07
    private val difficultLevelIntermediate: Double = 0.13
    private val difficultLevelExpert: Double = 0.2

    fun getMinesAmount(gameDifficultSelected: Int): Int {
        when (gameDifficultSelected) {
            0 -> {
                percentageOfMines = difficultLevelBeginner
            }
            1 -> {
                percentageOfMines = difficultLevelIntermediate
            }
            2 -> {
                percentageOfMines = difficultLevelExpert
            }
        }
        return ((getColumnsAmount() * getRowsAmount()) * percentageOfMines).toInt()
    }

    fun getColumnsAmount(): Int {
        return floor(boardWidthInDp / blockSize).toInt()
    }

    fun getRowsAmount(): Int {
        return floor(boardHeightInDp / blockSize).toInt()
    }
}