package org.sazontotal.project.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import org.sazontotal.project.data.db.PlatoEntity
import kotlin.random.Random

// vive aqui para que menu y carrito compartan cantidades, se crea en App y se pasa a ambos.
class CarritoCompra {
    var solicitudId: String = nuevaSolicitudId()
        private set

    private fun nuevaSolicitudId(): String = "${Random.nextLong()}-${Random.nextLong()}"
    // cantidad pedida de cada plato. si no aparece, es 0.
    val cantidades: SnapshotStateMap<Long, Int> = mutableStateMapOf()

    // copia del plato al agregarlo: conserva nombre, categoria y precio aunque lo borren del menu.
    val platosGuardados: SnapshotStateMap<Long, PlatoEntity> = mutableStateMapOf()

    var notaGeneral by mutableStateOf("")

    fun cantidadDe(id: Long): Int {
        return cantidades[id] ?: 0
    }

    fun sumar(plato: PlatoEntity) {
        // mientras el plato siga en el carrito no se reemplaza su copia, asi el precio no cambia.
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
            } else {
                cantidades[id] = actual - 1
            }
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
        notaGeneral = ""
        solicitudId = nuevaSolicitudId()
    }
}
