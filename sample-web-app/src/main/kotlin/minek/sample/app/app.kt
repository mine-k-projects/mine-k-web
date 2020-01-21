package minek.sample.app

import minek.web.spring.retrofit.autoconfigure.RetrofitServiceScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["minek"])
@RetrofitServiceScan(["minek"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
