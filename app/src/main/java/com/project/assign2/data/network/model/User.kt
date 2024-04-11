// Package declaration indicating the location of the file within the project
package com.project.assign2.data.network.model

// Data class representing a User
data class User(
    var userId: String = "", // Unique identifier for the user
    var userEmail: String? = "" // Email associated with the user (nullable)
)
