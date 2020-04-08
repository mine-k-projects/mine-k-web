package minek.web.spring.web.extension

import javax.servlet.http.HttpServletRequest

fun HttpServletRequest.isPost(): Boolean = method == "POST"
fun HttpServletRequest.isGet(): Boolean = method == "GET"
fun HttpServletRequest.springSecurityLastException(): Exception? {
    return session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") as Exception?
}
