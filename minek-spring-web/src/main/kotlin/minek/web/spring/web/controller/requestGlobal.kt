package minek.web.spring.web.controller

import minek.web.spring.auth.AuthService
import minek.web.spring.auth.Principal
import minek.web.spring.storage.SessionStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RequestGlobal {

    companion object {
        const val REQUEST_GLOBAL_ALERTS = "REQUEST_GLOBAL_ALERTS"
    }

    @Autowired
    lateinit var sessionStorageService: SessionStorageService

    @Autowired
    lateinit var authService: AuthService

    fun loginUser(): Principal? = authService.principal()

    fun isLogin(): Boolean = loginUser() != null

    fun alerts(): List<String> {
        return sessionStorageService.get(REQUEST_GLOBAL_ALERTS) { listOf() }
    }

    fun alert(message: String) {
        val alerts = alerts().toMutableList()
        alerts.add(message)
        sessionStorageService.set(REQUEST_GLOBAL_ALERTS, alerts)
    }

    fun popAlerts(): List<String> {
        return sessionStorageService.pop(REQUEST_GLOBAL_ALERTS) ?: emptyList()
    }
}
