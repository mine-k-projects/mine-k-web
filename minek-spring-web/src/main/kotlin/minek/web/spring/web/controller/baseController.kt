package minek.web.spring.web.controller

import javax.servlet.http.HttpServletRequest
import minek.web.spring.router.AbstractUriBuilder
import minek.web.spring.router.ReverseRouter
import minek.web.spring.web.RequestGlobal
import minek.web.spring.web.RequestMessage
import minek.web.spring.web.exception.BaseException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.validation.BindingResult
import org.springframework.validation.Validator

abstract class BaseController {

    @Autowired
    lateinit var g: RequestGlobal

    @Autowired
    lateinit var r: ReverseRouter

    @Autowired
    lateinit var message: RequestMessage

    @Autowired
    lateinit var request: HttpServletRequest

    @Autowired
    @Qualifier("mvcValidator")
    lateinit var validator: Validator

    protected open fun isValidOnPost(bindingResult: BindingResult): Boolean {
        return isPost() && !bindingResult.hasErrors()
    }

    protected open fun isPost(): Boolean {
        return "POST".equals(request.method, ignoreCase = true)
    }

    protected open fun isGet(): Boolean {
        return "GET".equals(request.method, ignoreCase = true)
    }

    protected open fun validateOnPost(form: Any, bindingResult: BindingResult): Boolean {
        return isPost() && validate(form, bindingResult)
    }

    protected open fun validate(form: Any, bindingResult: BindingResult): Boolean {
        validator.validate(form, bindingResult)
        return !bindingResult.hasErrors()
    }

    fun redirect(url: String): String = "redirect:$url"

    fun redirect(uriBuilder: AbstractUriBuilder): String = redirect(uriBuilder.build())

    protected fun badRequest(): Unit = throw BaseException.BadRequest

    protected fun unauthorized(): Unit = throw BaseException.Unauthorized

    protected fun notFound(): Unit = throw BaseException.NotFound
}
