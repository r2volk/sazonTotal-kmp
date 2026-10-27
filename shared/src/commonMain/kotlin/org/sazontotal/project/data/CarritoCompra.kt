package org.sazontotal.project.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import org.sazontotal.project.data.db.PlatoEntity

// vive aqui para que menu y carrito compartan cantidades, se crea en App y se pasa a ambos.
class CarritoCompra {
    // id del plato -> cuantos pidio. si no esta anotado, es 0.
    val cantidades: SnapshotStateMap<Long, Int> = mutableStateMapOf()

    // copia del plato al agregarlo: conserva nombre, categoria y precio aunque lo borren del menu.
    val platosGuardados: SnapshotStateMap<Long, PlatoEntity> = mutableStateMapOf()

    // id del plato -> nota ("sin cebolla"). solo se guardan las no vacias.
    val notas: SnapshotStateMap<Long, String> = mutableStateMapOf()

    fun cantidadDe(id: Long): Int {
        return cantidades[id] ?: 0
    }

    fun sumar(plato: PlatoEntity) {
        // no se reemplaza la copia mientras el plato siga en el carrito, asi el precio no cambia a mitad de la comanda.
        if (platosGuardados[plato.id] == null) {
            platosGuardados[plato.id] = plato
        }
        cantidades[plato.id] = cantidadDe(plato.id) + 1
    }

    fun restar(id: Long) {
        val actual = cantidadDe(id)
        if (actual > 0) {
            if (actual == 1) {
                cantidades.remove(id)
                platosGuardados.remove(id)
                notas.remove(id)
            } else {
                cantidades[id] = actual - 1
            }
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

    fun total(): Double {
        return platosGuardados.entries.sumOf { (id, plato) ->
            plato.precio * cantidadDe(id)
        }
    }

    fun platosEnCarrito(): List<PlatoEntity> {
        return platosGuardados.values.filter { cantidadDe(it.id) > 0 }
    }

    fun limpiar() {
        cantidades.clear()
        platosGuardados.clear()
        notas.clear()
    }
}
