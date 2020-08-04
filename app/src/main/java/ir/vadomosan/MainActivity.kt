package ir.vadomosan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.vadomosan.security.R
import ir.vadomosan.security.SafeBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val safe = SafeBuilder(this)
            .checkRoot(true)
            .checkEmulator(true)
            .checkSignature("", true)
            .checkDebuggable(true)
            .checkHookDetected(true)
            .checkVPN(true)
            .check()

        val s = safe.isSafe
        s

    }
}