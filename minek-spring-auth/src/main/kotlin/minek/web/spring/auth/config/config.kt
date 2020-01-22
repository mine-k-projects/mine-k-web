package minek.web.spring.auth.config

import minek.web.spring.auth.AuthorizationHandlerInterceptorAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class AuthConfig : WebMvcConfigurer {

    @Autowired
    lateinit var authorizationHandlerInterceptorAdapter: AuthorizationHandlerInterceptorAdapter

    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)
        registry.addInterceptor(authorizationHandlerInterceptorAdapter)
    }
}
