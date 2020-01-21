package minek.web.spring.storage.exception

class StorageException : RuntimeException {
    constructor(message: String, cause: Exception?) : super(message, cause)
    constructor(message: String) : super(message)
    constructor(cause: Exception) : super(cause)
}

class FileNotFoundException : RuntimeException {
    constructor(message: String, cause: Exception?) : super(message, cause)
    constructor(message: String) : super(message)
    constructor(cause: Exception) : super(cause)
}
