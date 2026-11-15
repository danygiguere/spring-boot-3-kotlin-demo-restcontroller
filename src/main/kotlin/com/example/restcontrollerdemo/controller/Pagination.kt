package com.example.restcontrollerdemo.controller

/** Keyset pagination bounds, shared by every list endpoint. */
internal object Pagination {
    const val DEFAULT_LIMIT = "20"
    const val MAX_LIMIT = 100L
    const val CURSOR_DESCRIPTION =
        "`nextCursor` of the previous page; omit for the first page. Null `nextCursor` means the last page."
    const val LIMIT_DESCRIPTION = "Page size, 1 to 100"

    /** Newest-first lists: no cursor means "from the newest". */
    fun before(cursor: Long?): Long = cursor ?: Long.MAX_VALUE

    /** Oldest-first lists: no cursor means "from the oldest". */
    fun after(cursor: Long?): Long = cursor ?: 0
}
