// Package declaration indicating the location of the file within the project
package com.project.assign2.data.network.model

// Importing Serializable interface from Java IO package
import java.io.Serializable

// Data class representing a Todo item
data class Todo(
    val id: String = "", // Unique identifier for the Todo item
    val name: String = "", // Name or description of the Todo item
    val createdBy: User = User(), // User who created the Todo item, defaulting to an empty User object
    val done: Boolean = false // Flag indicating whether the Todo item is done or not, defaulting to false
) : Serializable // Implementing Serializable interface to make the Todo class serializable
