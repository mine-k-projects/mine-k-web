package minek.web.spring.web.controller.advice

import minek.web.spring.router.ReverseRouter
import minek.web.spring.web.controller.RequestGlobal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.propertyeditors.StringTrimmerEditor
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.InitBinder
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class GlobalAdviceController {

    @Autowired
    lateinit var g: RequestGlobal

    @Autowired
    lateinit var r: ReverseRouter

    @ModelAttribute("g")
    fun requestGlobal(): RequestGlobal = g

    @ModelAttribute("r")
    fun reverseRouter(): ReverseRouter = r

    @InitBinder
    fun initBinder(binder: WebDataBinder) {
        binder.registerCustomEditor(String::class.java, StringTrimmerEditor(true))
    }
}
