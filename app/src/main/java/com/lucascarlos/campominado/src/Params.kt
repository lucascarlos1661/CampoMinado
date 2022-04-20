package com.lucascarlos.campominado.src

import android.content.res.Resources
import android.util.DisplayMetrics
import kotlin.math.floor

class Params {

    private val blockSize: Int = 30
    private val headerRatio: Double = 0.15
    private val marginHorizontalBoard = 5
    private val marginBottomBoard = 6
    private var percentageOfMines: Double = 0.0

    //The difficulty level will be calculated through the option selected by the user
    //Mine percentage is fixed. 7% for beginner, 13% for Intermediate and 20% for Expert

    private val difficultLevelBeginner: Double = 0.07
    private val difficultLevelIntermediate: Double = 0.13
    private val difficultLevelExpert: Double = 0.2

    private var screenWidthInDp: Float = 0F
    private var screenHeightInDp: Float = 0F

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
        val width: Float = getScreenWidth() - marginHorizontalBoard
        return floor((width / blockSize).toDouble()).toInt()
    }

    fun getRowsAmount(): Int {
        val totalHeight = getScreenHeight()
        val boardHeight = totalHeight * (1 - headerRatio) - marginBottomBoard
        return floor(boardHeight / blockSize).toInt()
    }

    private fun getScreenWidth(): Float {
        val widthPixels: Int = Resources.getSystem().displayMetrics.widthPixels
        screenWidthInDp = convertPixelsToDp(widthPixels)
        return screenWidthInDp
    }

    private fun getScreenHeight(): Float {
        val heightPixels: Int = Resources.getSystem().displayMetrics.heightPixels
        screenHeightInDp = convertPixelsToDp(heightPixels)
        return screenHeightInDp
    }

    private fun convertPixelsToDp(px: Int): Float {
        val systemDensityDpi: Int = Resources.getSystem().displayMetrics.densityDpi
        return px / (systemDensityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}