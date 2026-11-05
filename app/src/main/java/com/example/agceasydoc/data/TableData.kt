package com.example.agceasydoc.data

data class TableData(
    val title: String = "",
    val rows: Int,
    val columns: Int,
    val cells: List<List<String>>
)

fun emptyTable(rows: Int, columns: Int): TableData =
    TableData(
        rows = rows,
        columns = columns,
        cells = List(rows) { List(columns) { "" } }
    )
