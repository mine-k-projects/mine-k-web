package minek.jooq.pagination

import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.SelectLimitStep

class Pagination<E>(
    val page: Int,
    val total: Int,
    val content: List<E>,
    private val offset: Int,
    val lastPage: Int,
    val prevPage: Int,
    val hasPrevious: Boolean,
    val nextPage: Int,
    val hasNext: Boolean,
    val navPrev: Int,
    val hasNavPrev: Boolean,
    val navNext: Int,
    val hasNavNext: Boolean,
    val pages: List<Int>
) {
    val indexedItems by lazy {
        content.mapIndexed { index, e ->
            Entry(
                total - offset - index,
                e
            )
        }
    }

    val firstPage = 1
    val isFirst = page == firstPage
    val isLast = page == lastPage
    val hasContent = content.isNotEmpty()

    companion object {
        fun <R : Record, E> of(
            ctx: DSLContext,
            query: SelectLimitStep<R>,
            forPage: Int,
            perPage: Int = 20,
            perNav: Int = 10,
            mapper: (record: R) -> E
        ): Pagination<E> {
            val total = ctx.fetchCount(query)

            val last = if (total == 0) 1 else intCeil(total, perPage)
            val page = max(min(last, forPage), 1)

            val prev = max(page - 1, 1)
            val hasPrevious = prev != page
            val next = min(page + 1, last)
            val hasNext = next != page

            val navHead = perNav * (intCeil(page, perNav) - 1) + 1
            val navTail = min(last, navHead + perNav - 1)

            val navPrev = max(page - perNav, 1)
            val hasNavPrev = navPrev < navHead
            val navNext = min(page + perNav, last)
            val hasNavNext = navNext > navTail

            val pages = (navHead..navTail).toList()

            val offset = (page - 1) * perPage

            val content = query.limit(offset, perPage).map(mapper)

            return Pagination(
                page,
                total,
                content,
                offset,
                last,
                prev,
                hasPrevious,
                next,
                hasNext,
                navPrev,
                hasNavPrev,
                navNext,
                hasNavNext,
                pages
            )
        }
    }

    data class Entry<E>(val index: Int, val item: E)
}

private fun intCeil(x: Int, y: Int): Int {
    return ceil(x.toDouble() / y).toInt()
}
