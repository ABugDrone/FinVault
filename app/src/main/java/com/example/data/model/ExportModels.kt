package com.example.data.model

enum class ExportFormat {
    CSV,
    PDF
}

enum class ExportDateRange(val label: String) {
    ALL_TIME("All Time"),
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    LAST_30_DAYS("Last 30 Days"),
    YEAR_TO_DATE("Year to Date"),
    CUSTOM("Custom Range")
}
