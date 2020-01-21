package minek.web.spring.boot.storage.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "minek.storage")
class StorageProperties(val location: String)