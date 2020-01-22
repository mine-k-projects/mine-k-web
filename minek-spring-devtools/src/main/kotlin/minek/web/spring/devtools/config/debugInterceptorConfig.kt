package minek.web.spring.devtools.config

import minek.web.spring.devtools.interceptor.DebugInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class DebugInterceptorConfig : WebMvcConfigurer {

    @Autowired
    lateinit var debugInterceptor: DebugInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(debugInterceptor)
        super.addInterceptors(registry)
    }
}
