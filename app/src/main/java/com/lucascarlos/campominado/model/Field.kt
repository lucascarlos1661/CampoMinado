package com.lucascarlos.campominado.model

data class Field(
    val row: Int,
    val column: Int,
    val opened: Boolean,
    val flagged: Boolean,
    var mined: Boolean,
    val exploded: Boolean,
    val nearMines: Int
)