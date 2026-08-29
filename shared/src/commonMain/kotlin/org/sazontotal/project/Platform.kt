package org.sazontotal.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform