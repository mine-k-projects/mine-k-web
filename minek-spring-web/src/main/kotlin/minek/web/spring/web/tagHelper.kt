package minek.web.spring.web

import javax.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TagHelper {

    companion object {
        private const val TAG_HELPER_SCRIPTS = "TAG_HELPER_SCRIPTS"
        private const val TAG_HELPER_HEADS = "TAG_HELPER_HEADS"
    }

    @Autowired
    lateinit var request: HttpServletRequest

    fun pushHeads(body: String) {
        getItems(TAG_HELPER_HEADS).add(body)
    }

    fun popHeads(): List<String> = popItems(TAG_HELPER_HEADS)

    fun pushScript(body: String) {
        getItems(TAG_HELPER_SCRIPTS).add(body)
    }

    fun popScripts(): List<String> = popItems(TAG_HELPER_SCRIPTS)

    private fun popItems(attr: String): List<String> {
        val scripts = getItems(attr)
        request.setAttribute(attr, mutableListOf<String>())
        return scripts
    }

    @Suppress("UNCHECKED_CAST")
    private fun getItems(attr: String): MutableList<String> {
        val attribute = request.getAttribute(attr) as? MutableList<String>
        return attribute ?: mutableListOf<String>().apply {
            request.setAttribute(attr, this)
        }
    }
}
