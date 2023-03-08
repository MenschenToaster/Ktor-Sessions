package com.example

import at.toastiii.ktor.sessions.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import at.toastiii.ktor.sessions.storage.MemorySessionStorage

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        data class MySession(var count: Int = 0) : Session {
            override fun clone(): Session = copy()
        }

        install(SessionPlugin) {
            cookie<MySession>("session", MemorySessionStorage())
        }
        get("/") {
            val session = call.sessions.getOrSet<MySession> { MySession() }
            session.count++

            call.respondText("Hello World! Count" + session.count)
        }
        get("/read") {
            val session = call.sessions.get<MySession>() ?: return@get call.respondText("No session")

            call.respondText("Hello World! Count" + session.count)
        }
    }
}
