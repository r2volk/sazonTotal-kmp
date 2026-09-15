package org.sazontotal.project.models

import org.sazontotal.project.enums.UserRole

data class User(
    val id: String,
    val nombre: String,
    val pin: String,
    val rol: UserRole
)
