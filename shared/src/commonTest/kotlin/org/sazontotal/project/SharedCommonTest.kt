package org.sazontotal.project

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.sazontotal.project.data.db.esPinEmpleadoValido

class SharedCommonTest {

    @Test
    fun pinMustContainExactlyFourAsciiDigits() {
        assertTrue(esPinEmpleadoValido("0427"))
        assertFalse(esPinEmpleadoValido("427"))
        assertFalse(esPinEmpleadoValido("04275"))
        assertFalse(esPinEmpleadoValido("04a7"))
        assertFalse(esPinEmpleadoValido("０４２７"))
    }
}
