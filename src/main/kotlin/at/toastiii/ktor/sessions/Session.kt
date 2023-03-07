package at.toastiii.ktor.sessions

interface Session : Cloneable {
    public override fun clone(): Session
}