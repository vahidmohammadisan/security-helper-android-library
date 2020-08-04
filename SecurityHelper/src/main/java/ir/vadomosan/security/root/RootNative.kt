package ir.vadomosan.security.root

import android.util.Log

/**
 * Created by mat on 19/06/15.
 */
class RootNative {
    companion object {
        private var libraryLoaded = false

        /**
         * Loads the C/C++ libraries statically
         */
        init {
            try {
                System.loadLibrary("tool-checker")
                libraryLoaded = true
            } catch (e: UnsatisfiedLinkError) {
                Log.w("TAG", e)
            }
        }
    }

    fun wasNativeLibraryLoaded(): Boolean {
        return libraryLoaded
    }

    external fun checkForRoot(pathArray: Array<String?>): Int
    external fun setLogDebugMessages(logDebugMessages: Boolean): Int
}