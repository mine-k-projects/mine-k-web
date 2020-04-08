package minek.web.spring.web

import javax.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.web.servlet.LocaleResolver

@Component
class RequestMessage {

    @Autowired
    lateinit var messageSource: MessageSource

    @Autowired
    lateinit var localeResolver: LocaleResolver

    @Autowired
    lateinit var request: HttpServletRequest

    fun text(code: String, vararg args: Any): String {
        return messageSource.getMessage(code, args, localeResolver.resolveLocale(request))
    }

    fun enum(value: Enum<*>): String {
        return text("${value.javaClass.name}.${value.name}")
    }
}
