package at.toastiii.ktor.sessions.util

import java.time.Duration
import java.util.Date

operator fun Date.plus(duration: Duration): Date {
    return Date(time + duration.toMillis())
}