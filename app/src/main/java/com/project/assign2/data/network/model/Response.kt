// Package declaration indicating the location of the file within the project
package com.project.assign2.data.network.model

// Sealed class representing different types of responses
sealed class Response<out T> {

    // Data class representing a successful response with data of type T
    data class Success<out T>(val data: T) : Response<T>()

    // Object representing a loading state with no associated data
    object Loading : Response<Nothing>()

    // Data class representing an error response with an optional Exception
    data class Error(val e: Exception?) : Response<Nothing>()
}
