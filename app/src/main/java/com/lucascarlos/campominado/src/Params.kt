package com.lucascarlos.campominado.src

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import kotlin.math.floor

class Params() {

    private val blockSize: Int = 30
    val fontSize: Int = 15
    private val headerRatio: Double = 0.15
    val difficultLevel: Double = 0.1
    private val marginHorizontalBoard = 5

    private var screenWidthInDp: Float = 0F
    private var screenHeightInDp: Float = 0F

    fun getColumnsAmount(context: Context): Int {
        val width: Float = getScreenWidth(context) - marginHorizontalBoard
        return floor((width / blockSize).toDouble()).toInt()
    }

    fun getRowsAmount(context: Context): Int {
        val totalHeight = getScreenHeight(context)
        val boardHeight = totalHeight * (1 - headerRatio)
        return floor(boardHeight / blockSize).toInt()
    }

    private fun getScreenWidth(context: Context): Float {
        val widthPixels: Int = Resources.getSystem().displayMetrics.widthPixels
        screenWidthInDp = convertPixelsToDp(widthPixels, context)
        return screenWidthInDp
    }

    private fun getScreenHeight(context: Context): Float {
        val heightPixels: Int = Resources.getSystem().displayMetrics.heightPixels
        screenWidthInDp = convertPixelsToDp(heightPixels, context)
        return screenWidthInDp
    }

    private fun convertPixelsToDp(px: Int, context: Context): Float =
        px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

}