package com.raizmarket.app

import java.text.NumberFormat
import java.util.Locale

enum class Role {
    BUYER,
    SELLER,
    ADMIN
}

/** Estado de sesión del usuario autenticado. */
object Session {
    var role: Role = Role.BUYER
}

/** Modelo de producto de Raíz Market. */
data class Product(
    var name: String,
    var price: Long,
    var seller: String,
    var stock: Int = 10,
    var status: String = "Aprobado",
    var km: Double = 1.2,
    var category: String = "Café"
) {
    val distance: String
        get() = String.format(Locale("es", "CO"), "%.1f km", km)
}

data class CartItem(
    val product: Product,
    var qty: Int
)

data class Order(
    val id: String,
    val meta: String,
    val total: Long,
    var status: String,
    val customer: String = ""
)

/** Modelo de usuario. El campo id almacena el UID de Firebase. */
data class AppUser(
    var name: String,
    var email: String,
    var role: String,
    var status: String,
    var id: String = ""
)

data class Txn(
    val ref: String,
    val meta: String,
    val amount: Long,
    val status: String
)

/** Formato de valores monetarios en pesos colombianos. */
object Cop {
    fun fmt(v: Long): String =
        "$" + NumberFormat.getInstance(Locale("es", "CO")).format(v)
}

/** Datos de ejemplo utilizados mientras se conectan las pantallas con Firebase. */
object SampleData {

    val featured = mutableListOf(
        Product("Café Huila 500g", 32000, "Finca La Esperanza", 24, km = 1.2),
        Product("Mochila wayuu", 85000, "Taller Wayuu", 5, km = 3.4, category = "Artesanías"),
        Product("Panela orgánica", 9500, "Trapiche Sol", 40, km = 2.8, category = "Miel y panela"),
        Product("Miel de abeja", 18000, "Apiario Sol", 12, km = 4.1, category = "Miel y panela"),
        Product("Café Nariño 250g", 19000, "Cooperativa Nariño", 8, km = 5.0),
        Product("Ruana boyacense", 120000, "Telares Tunja", 3, km = 6.3, category = "Textiles")
    )

    val cart = mutableListOf<CartItem>()

    val sellerProducts = mutableListOf(
        Product("Café Huila 500g", 32000, "Finca La Esperanza", 24, "Aprobado"),
        Product("Café Nariño 250g", 19000, "Finca La Esperanza", 8, "Aprobado"),
        Product("Mochila wayuu", 85000, "Finca La Esperanza", 5, "Pendiente", category = "Artesanías"),
        Product("Panela orgánica", 9500, "Finca La Esperanza", 0, "Oculto", category = "Miel y panela")
    )

    val buyerOrders = mutableListOf(
        Order("Pedido #1042", "13 oct · 3 ítems", 103800, "En camino"),
        Order("Pedido #1031", "02 oct · 1 ítem", 32000, "Entregado"),
        Order("Pedido #1008", "18 sep · 2 ítems", 27500, "Entregado")
    )

    val sellerOrders = mutableListOf(
        Order("#1042", "Bogotá · 3 ítems", 103800, "Nuevo", "Laura G."),
        Order("#1041", "Cali · 1 ítem", 32000, "Nuevo", "Carlos P."),
        Order("#1039", "Medellín · 2 ítems", 48000, "Preparando", "Ana R."),
        Order("#1030", "Bogotá · 1 ítem", 19000, "Enviado", "Juan M.")
    )

    val users = mutableListOf(
        AppUser("Laura Gómez", "laura@correo.com", "Comprador", "Activo"),
        AppUser("Finca La Esperanza", "finca@correo.com", "Vendedor", "Activo"),
        AppUser("Carlos Pérez", "carlos@correo.com", "Vendedor", "Suspendido"),
        AppUser("Ana Ríos", "ana@correo.com", "Comprador", "Activo"),
        AppUser("Admin Raíz", "admin@raizmarket.co", "Administrador", "Activo")
    )

    val moderation = mutableListOf(
        Product("Mochila wayuu", 85000, "Finca Wayuu SAS", status = "Pendiente", category = "Artesanías"),
        Product("Miel de abeja", 18000, "Apiario Sol", status = "Pendiente", category = "Miel y panela"),
        Product("Ruana boyacense", 120000, "Telares Tunja", status = "Pendiente", category = "Textiles"),
        Product("Café Huila 500g", 32000, "Finca La Esperanza", status = "Aprobado"),
        Product("Panela falsa", 5000, "Vendedor X", status = "Rechazado")
    )

    val transactions = listOf(
        Txn("RM-1042", "Tarjeta · 13 oct", 103800, "Aprobado"),
        Txn("RM-1041", "PSE · 13 oct", 32000, "Aprobado"),
        Txn("RM-1040", "Tarjeta · 12 oct", 48000, "Rechazado"),
        Txn("RM-1039", "Nequi · 12 oct", 48000, "Pendiente")
    )

    val categories = listOf(
        "Café",
        "Artesanías",
        "Miel y panela",
        "Textiles",
        "Alimentos",
        "Cosmética natural"
    )

    fun find(name: String): Product? =
        featured.firstOrNull { it.name == name }

    fun cartSubtotal(): Long =
        cart.sumOf { it.product.price * it.qty }

    fun cartIva(): Long =
        cartSubtotal() * 5 / 100
}