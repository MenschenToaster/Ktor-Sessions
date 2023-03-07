package at.toastiii.ktor.sessions

class SessionInstance<S : Any>(val provider: SessionProvider<S>, val incoming: Boolean) {

    //TODO: Lazy data loading

}