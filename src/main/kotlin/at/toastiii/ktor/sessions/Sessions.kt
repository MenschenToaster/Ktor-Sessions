package at.toastiii.ktor.sessions

import io.ktor.server.application.*

val Session: RouteScopedPlugin<SessionsConfig> = createRouteScopedPlugin("Sessions", ::SessionsConfig) {

}