package minek.web.spring.router

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod
import minek.core.extension.dropLast
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UrlPathHelper

abstract class AbstractUriBuilder {

    private data class Param(val replace: Boolean, val name: String, val values: List<Any?>)

    abstract val uriComponentsBuilder: UriComponentsBuilder
    private val queryParams: MutableList<Param> = mutableListOf()

    fun queryParam(name: String, vararg values: Any?): AbstractUriBuilder {
        this.queryParams.add(Param(false, name, values.toList()))
        return this
    }

    fun replaceQueryParam(name: String, vararg values: Any?): AbstractUriBuilder {
        this.queryParams.add(Param(true, name, values.toList()))
        return this
    }

    protected fun bindParams(cloneBuilder: UriComponentsBuilder): UriComponentsBuilder {
        return queryParams
            .fold(cloneBuilder, { builder, (replace, name, values) ->
                if (replace) {
                    builder.replaceQueryParam(name, values)
                } else {
                    builder.queryParam(name, values)
                }
            })
    }

    abstract fun build(): String

    override fun toString(): String = build()
}

class UriBuilder(path: String) : AbstractUriBuilder() {

    companion object {
        private val PATTERN = Pattern.compile("\\{(.*?)}")
    }

    override val uriComponentsBuilder: UriComponentsBuilder = UriComponentsBuilder.fromPath(path)
    private val uriVariables = mutableMapOf<String, Any?>()
    private var excludeRegex = false

    fun withoutRegex(): UriBuilder {
        excludeRegex = true
        return this
    }

    fun arg(key: String, value: Any?): UriBuilder {
        uriVariables[key] = value
        return this
    }

    fun args(args: Map<String, Any?>): UriBuilder {
        uriVariables.putAll(args)
        return this
    }

    fun args(vararg args: Pair<String, Any?>): UriBuilder {
        uriVariables.putAll(args.toMap())
        return this
    }

    override fun build(): String {
        val uri = bindParams(uriComponentsBuilder.cloneBuilder())
            .uriVariables(uriVariables)
//            .buildAndExpand(uriVariables)
            .encode(StandardCharsets.UTF_8)
            .toUriString()

        return if (excludeRegex) {
            removeRegex(uri)
        } else {
            uri
        }
    }

    private fun removeRegex(uri: String): String {
        val matcher = PATTERN.matcher(uri)
        val sb = StringBuffer()
        while (matcher.find()) {
            val findText = matcher.group().removePrefix("{").removeSuffix("}")
            val paramName = if (findText.indexOf(":") != -1) {
                findText.split(":")[0]
            } else {
                findText
            }
            matcher.appendReplacement(sb, "{$paramName}")
        }
        matcher.appendTail(sb)
        return sb.toString()
    }
}

class CurrentUriBuilder(private val request: HttpServletRequest) : AbstractUriBuilder() {
    override val uriComponentsBuilder: UriComponentsBuilder =
        UriComponentsBuilder.fromUriString(UrlPathHelper().getOriginatingRequestUri(request))
    private var includeQueryString: Boolean = false

    fun withQueryString(): CurrentUriBuilder {
        this.includeQueryString = true
        return this
    }

    override fun build(): String {
        val cloneBuilder = uriComponentsBuilder.cloneBuilder().let {
            if (includeQueryString) {
                it.query(request.queryString)
            } else {
                it
            }
        }
        return bindParams(cloneBuilder)
            .build()
            .encode(StandardCharsets.UTF_8)
            .toUriString()
    }
}

@Component
class ReverseRouter {

    @Autowired
    lateinit var requestMappingHandlerMapping: RequestMappingHandlerMapping

    @Autowired
    lateinit var request: HttpServletRequest

    companion object {
        private const val SEPARATOR = "."
    }

    fun currentUrlFor(): CurrentUriBuilder = CurrentUriBuilder(request)

    fun referer(): String? = request.getHeader("referer")

    fun referer(defaultUri: String): String? = referer() ?: defaultUri

    private val cache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .build(object : CacheLoader<String, Optional<RequestMappingInfo>>() {
            override fun load(key: String): Optional<RequestMappingInfo> {
                val split = key.split(SEPARATOR)
                val (controllerName, methodName) = split[0].toLowerCase() to split[1]
                val handler = requestMappingHandlerMapping.handlerMethods
                    .filter { (_, handler) ->
                        val className = handler.beanType.simpleName.toLowerCase()
                        (controllerName == className || controllerName == className.dropLast("controller")) &&
                                methodName == handler.method.name
                    }
                    .toList()
                    .firstOrNull()
                return Optional.ofNullable(handler?.first)
            }
        })

    /**
     * the controllerName for the pattern ignores case and the suffix "Controller" is optional
     * @param pattern controllerName.methodName (example : main.index, mainController.index, MainController.index)
     */
    fun urlFor(pattern: String): UriBuilder {
        if (pattern.startsWith("/")) {
            return UriBuilder(request.contextPath + pattern)
        }

        val split = pattern.split(SEPARATOR)
        if (split.size != 2) {
            throw RuntimeException("pattern error")
        }

        val optional = cache.get(pattern)
        if (optional.isPresent) {
            return UriBuilder(optional.get().patternsCondition.patterns.toList()[0])
        }
        return UriBuilder("/$pattern")
    }

    fun urlFor2(function: KFunction<*>): UriBuilder {
        val methodName = function.javaMethod!!
        val controllerName = methodName.declaringClass.simpleName
        return urlFor(controllerName.decapitalize() + SEPARATOR + methodName.name)
    }
}
