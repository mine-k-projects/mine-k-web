package minek.jooq.pagination

import kotlin.math.ceil
import kotlin.math.min
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.SelectLimitStep

class Pagination<E>(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val content: List<E>,
    private val offset: Int,
    private val navSize: Int
) {
    val indexedItems by lazy { content.mapIndexed { index, e ->
        Entry(
            total - offset - index,
            e
        )
    } }

    val totalPages = if (pageSize == 0) 1 else ceil(total.toDouble() / pageSize).toInt()
    val hasPrevious = page > 1
    val hasNext = page < totalPages
    val isFirst = !hasPrevious
    val isLast = !hasNext
    val hasContent = content.isNotEmpty()
    val pages by lazy {
        val navHead = navSize * (ceil(page.toDouble() / navSize).toInt() - 1) + 1
        val navTail = min(totalPages, navHead + navSize - 1)
        (navHead..navTail).toList()
    }

    companion object {
        fun <R : Record, E> of(
            ctx: DSLContext,
            query: SelectLimitStep<R>,
            page: Int,
            pageSize: Int = 20,
            navSize: Int = 10,
            mapper: (record: R) -> E
        ): Pagination<E> {
            val total = ctx.fetchCount(query)
            val offset = (page - 1) * pageSize
            val content = query.limit(offset, pageSize).map(mapper)
            return Pagination(
                page,
                pageSize,
                total,
                content,
                offset,
                navSize
            )
        }
    }

    data class Entry<E>(val index: Int, val item: E)
}
