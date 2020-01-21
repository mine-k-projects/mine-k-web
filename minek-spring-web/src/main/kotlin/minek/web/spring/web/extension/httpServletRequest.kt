package minek.web.spring.web.extension

import javax.servlet.http.HttpServletRequest

fun HttpServletRequest.isPost(): Boolean = method == "POST"
fun HttpServletRequest.isGet(): Boolean = method == "GET"