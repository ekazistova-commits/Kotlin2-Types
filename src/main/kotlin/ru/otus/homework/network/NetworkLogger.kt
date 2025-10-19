@file:Suppress("unused")

package ru.otus.homework.network

import ru.otus.homework.Shape
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

/**
 * Известный вам список ошибок
 */
sealed class ApiException(message: String) : Throwable(message) {
    object NotAuthorized : ApiException("Not authorized") {
        private fun readResolve(): Any = NotAuthorized
    }

    object NetworkException : ApiException("Not connected") {
        private fun readResolve(): Any = NetworkException
    }

    object UnknownException : ApiException("Unknown exception") {
        private fun readResolve(): Any = UnknownException
    }
}

class ErrorLogger<in E : Throwable>(private val errorClass: Class<E>) {
    private val errors = mutableListOf<Pair<LocalDateTime, Throwable>>()

    fun log(response: NetworkResponse<*, *>) {
        if (response is Failure<*>) {
            val error = response.error
            if (error is Throwable && errorClass.isInstance(error)) {
                errors.add(response.responseDateTime to error)
            }
        }
    }

    fun dumpLog() {
        errors.forEach { (date, error) ->
            println("Error at $date: ${error.message}")
        }
    }
}
inline fun <reified E : Throwable> createErrorLogger(): ErrorLogger<E> {
    return ErrorLogger(E::class.java)
                }




fun processThrowables(logger: ErrorLogger<Throwable>) {
    logger.log(Success("Success"))
    Thread.sleep(100)
    logger.log(Success(Shape()))
    Thread.sleep(100)
    logger.log(Failure(IllegalArgumentException("Something unexpected")))

    logger.dumpLog()
                }

fun processApiErrors(apiExceptionLogger: ErrorLogger<ApiException>) {
    apiExceptionLogger.log(Success("Success"))
    Thread.sleep(100)
    apiExceptionLogger.log(Success(Shape()))
    Thread.sleep(100)
    apiExceptionLogger.log(Failure(ApiException.NetworkException))

    apiExceptionLogger.dumpLog()
                }

fun main() {
    val logger = createErrorLogger<Throwable>()

    println("Processing Throwable:")
    processThrowables(logger)

    println("Processing Api:")
    processApiErrors(logger)
 }

