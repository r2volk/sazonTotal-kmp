package org.sazontotal.project.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import org.sazontotal.project.data.db.PlatoEntity

// El carrito vive aquí (no en una pantalla) para que el Menú y el Carrito
// vean las mismas cantidades: lo que sumas en uno aparece en el otro.
// Se crea una sola vez en App y se pasa a ambas pantallas.
class CarritoCompra {
    // id del plato -> cuántos pidió. Si no está anotado, es 0.
    val cantidades: SnapshotStateMap<Long, Int> = mutableStateMapOf()

    // id del plato -> nota ("sin cebolla"). Solo se guardan las no vacías.
    val notas: SnapshotStateMap<Long, String> = mutableStateMapOf()

    fun cantidadDe(id: Long): Int {
        return cantidades[id] ?: 0
    }

    fun sumar(id: Long) {
        cantidades[id] = cantidadDe(id) + 1
    }

    fun restar(id: Long) {
        val actual = cantidadDe(id)
        if (actual > 0) {
            cantidades[id] = actual - 1
        }
    }

    fun notaDe(id: Long): String {
        return notas[id] ?: ""
    }

    fun ponerNota(id: Long, nota: String) {
        if (nota.isBlank()) {
            notas.remove(id)
        } else {
            notas[id] = nota
        }
    }

    fun totalItems(): Int {
        return cantidades.values.sum()
    }

    fun subtotal(platos: List<PlatoEntity>): Double {
        return platos.sumOf { plato -> plato.precio * cantidadDe(plato.id) }
    }

    fun limpiar() {
        cantidades.clear()
        notas.clear()
    }
}
