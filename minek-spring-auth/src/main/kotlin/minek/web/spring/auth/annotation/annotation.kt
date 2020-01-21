package minek.web.spring.auth.annotation

import java.lang.annotation.Inherited
import kotlin.reflect.KClass
import minek.web.spring.auth.PolicyAuthentication

/**
 * or condition
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@Repeatable
annotation class Authorize(val roles: Array<String> = [])

/**
 * and condition
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@Repeatable
annotation class PolicyAuthorize(val policy: KClass<out PolicyAuthentication>)

/**
 * top level
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class AllowAnonymous
