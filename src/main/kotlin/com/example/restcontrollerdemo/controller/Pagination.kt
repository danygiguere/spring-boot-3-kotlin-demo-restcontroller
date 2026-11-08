package com.example.restcontrollerdemo.controller

/** Keyset pagination bounds, shared by every list endpoint. */
internal object Pagination {
    const val DEFAULT_LIMIT = "20"
    const val MAX_LIMIT = 100L

    /** Newest-first lists: no cursor means "from the newest". */
    fun before(beforeId: Long?): Long = beforeId ?: Long.MAX_VALUE

    /** Oldest-first lists: no cursor means "from the oldest". */
    fun after(afterId: Long?): Long = afterId ?: 0
}
