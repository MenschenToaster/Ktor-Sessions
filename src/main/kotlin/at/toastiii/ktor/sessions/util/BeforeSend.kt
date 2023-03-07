package at.toastiii.ktor.sessions.util

import io.ktor.server.application.*
import io.ktor.server.response.*

internal object BeforeSend : Hook<suspend (ApplicationCall) -> Unit> {
    override fun install(pipeline: ApplicationCallPipeline, handler: suspend (ApplicationCall) -> Unit) {
        pipeline.sendPipeline.intercept(ApplicationSendPipeline.Before) {
            handler(call)
        }
    }
}