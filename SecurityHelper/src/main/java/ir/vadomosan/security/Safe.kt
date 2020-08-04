package ir.vadomosan.security

class Safe(
    private val Rooted: Boolean,
    private val Emulator: Boolean,
    private val SignatureValid: Boolean,
    private val Debuggable: Boolean,
    private val HookDetected: Boolean,
    private val vpn: Boolean
) {
    val isSafe: Boolean
        get() = Rooted && Emulator && SignatureValid && Debuggable && HookDetected && vpn

}