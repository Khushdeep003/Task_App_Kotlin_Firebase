// Package declaration indicating the location of the file within the project
package com.project.assign2.data.network.model

// Data class representing authentication information
data class Auth(
    val password: String, // Field to store the password
    val authGoogle: Boolean, // Field indicating if authentication is done using Google
    val authEmail: Boolean // Field indicating if authentication is done using email
)
