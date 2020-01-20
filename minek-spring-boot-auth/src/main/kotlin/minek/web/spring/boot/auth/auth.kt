package minek.web.spring.boot.auth

interface Principal {
    val username: String
    val role: String
}
