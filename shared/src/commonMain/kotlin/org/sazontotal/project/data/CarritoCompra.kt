package org.sazontotal.project.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import org.sazontotal.project.data.db.PlatoEntity

// vive aqui y no en una pantalla para que el menu y el carrito vean las
// mismas cantidades. se crea una vez en App y se pasa a las dos.
class CarritoCompra {
    // id del plato -> cuantos pidio. si no esta anotado, es 0.
    val cantidades: SnapshotStateMap<Long, Int> = mutableStateMapOf()

    // id del plato -> nota ("sin cebolla"). solo se guardan las no vacias.
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
