package minek.web.spring.boot.retrofit.autoconfigure

import minek.web.spring.boot.retrofit.config.RetrofitServiceRegistrar
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Import(RetrofitServiceRegistrar::class)
annotation class RetrofitServiceScan(val value: Array<String>)
