package ir.vadomosan.security

import android.content.Context
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.mockito.Mock

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Mock
    var mMockContext: Context? = null

    @Test
    fun checkRoot() {
        val safeBuilder = SafeBuilder(mMockContext)
        assertTrue(safeBuilder.checkRoot(true).check().isSafe)
    }
}