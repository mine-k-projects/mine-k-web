package minek.sample.app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

@Configuration
class RetrofitConfig {

    @Bean
    fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://httpbin.org")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
    }
}
