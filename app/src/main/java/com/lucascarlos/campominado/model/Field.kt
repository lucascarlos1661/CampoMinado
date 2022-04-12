package com.lucascarlos.campominado.model

data class Field(
    val column: Int,
    val row: Int,
    var opened: Boolean,
    var flagged: Boolean,
    var mined: Boolean,
    var exploded: Boolean,
    var nearMines: Int
)