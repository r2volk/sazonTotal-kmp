package org.sazontotal.project

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.sazontotal.project.data.db.esPinEmpleadoValido
import org.sazontotal.project.data.crearDestinoDelivery
import org.sazontotal.project.data.destinoPedido
import org.sazontotal.project.data.direccionDelivery
import org.sazontotal.project.data.esDelivery
import org.sazontotal.project.data.transicionPedidoPermitida
import kotlin.test.assertEquals
import org.sazontotal.project.data.maps.costoDelivery

class SharedCommonTest {

    @Test
    fun deliveryCobraTresSolesPorKilometroIniciado() {
        assertEquals(3.0, costoDelivery(0.001))
        assertEquals(3.0, costoDelivery(0.5))
        assertEquals(3.0, costoDelivery(1.0))
        assertEquals(6.0, costoDelivery(1.001))
        assertEquals(6.0, costoDelivery(1.5))
        assertEquals(6.0, costoDelivery(2.0))
        assertEquals(9.0, costoDelivery(2.001))
    }

    @Test
    fun deliveryConservaDireccionYSeDistingueDeMesa() {
        val destino = crearDestinoDelivery("  Av. Larco 512  ")
        assertTrue(esDelivery(destino))
        assertEquals("Av. Larco 512", direccionDelivery(destino))
        assertEquals("Delivery", destinoPedido(destino))
        assertFalse(esDelivery("4"))
        assertEquals("Mesa 4", destinoPedido("4"))
    }

    @Test
    fun deliveryDebeSalirAntesDeSerEntregado() {
        val delivery = crearDestinoDelivery("Av. Larco 512")
        assertTrue(transicionPedidoPermitida(delivery, "PENDIENTE", "LISTO"))
        assertTrue(transicionPedidoPermitida(delivery, "LISTO", "EN_CAMINO"))
        assertFalse(transicionPedidoPermitida(delivery, "LISTO", "ENTREGADO"))
        assertTrue(transicionPedidoPermitida(delivery, "EN_CAMINO", "ENTREGADO"))
        assertFalse(transicionPedidoPermitida("4", "LISTO", "EN_CAMINO"))
        assertTrue(transicionPedidoPermitida("4", "LISTO", "ENTREGADO"))
        assertFalse(transicionPedidoPermitida(delivery, "ANULADO", "ENTREGADO"))
    }

    @Test
    fun pinMustContainExactlyFourAsciiDigits() {
        assertTrue(esPinEmpleadoValido("0427"))
        assertFalse(esPinEmpleadoValido("427"))
        assertFalse(esPinEmpleadoValido("04275"))
        assertFalse(esPinEmpleadoValido("04a7"))
        assertFalse(esPinEmpleadoValido("０４２７"))
    }
}
