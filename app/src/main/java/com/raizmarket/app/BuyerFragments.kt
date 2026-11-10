package com.raizmarket.app


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup

/** Inicio: categorias, banner y productos destacados con filtro por texto y categoria. */
class BuyerHomeFragment : Fragment(R.layout.fragment_buyer_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_featured)
        rv.layoutManager = GridLayoutManager(requireContext(), 2)
        val search = view.findViewById<EditText>(R.id.et_search)
        val chips = view.findViewById<ChipGroup>(R.id.cg_categories)
        val byChip = mapOf(R.id.c_cafe to "Café", R.id.c_art to "Artesanías", R.id.c_miel to "Miel y panela", R.id.c_text to "Textiles")

        fun render() {
            val cat = byChip[chips.checkedChipId]
            val q = search.text.toString().trim().lowercase()
            val list = SampleData.featured.filter { (cat == null || it.category == cat) && it.name.lowercase().contains(q) }
            rv.adapter = SimpleAdapter(list, R.layout.item_product) { v, p -> bindProduct(v, p) }
        }
        chips.setOnCheckedStateChangeListener { _, _ -> render() }
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { render() }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })
        view.findViewById<TextView>(R.id.tv_see_all).setOnClickListener { (activity as? BuyerActivity)?.let {
            it.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_nav).selectedItemId = R.id.nav_search } }
        render()
    }
}

/** Catalogo / cercanos: busqueda y ordenamiento por distancia, precio o categoria. */
class CatalogFragment : Fragment(R.layout.fragment_catalog) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_products)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val search = view.findViewById<EditText>(R.id.et_search)
        val chips = view.findViewById<ChipGroup>(R.id.cg_filters)

        fun render() {
            val q = search.text.toString().trim().lowercase()
            var list = SampleData.featured.filter { it.name.lowercase().contains(q) }
            list = when (chips.checkedChipId) {
                R.id.c_price -> list.sortedBy { it.price }
                R.id.c_cat -> list.sortedBy { it.category }
                else -> list.sortedBy { it.km }
            }
            rv.adapter = SimpleAdapter(list, R.layout.item_product_row) { v, p -> bindProduct(v, p) }
        }
        chips.setOnCheckedStateChangeListener { _, _ -> render() }
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { render() }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })
        render()
    }
}

/** Carrito: cantidades, subtotales, IVA, envio y total. */
class CartFragment : Fragment(R.layout.fragment_cart) {
    private lateinit var root: View
    private lateinit var rv: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        root = view
        rv = view.findViewById(R.id.rv_cart)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = SimpleAdapter(SampleData.cart, R.layout.item_cart) { v, item ->
            v.findViewById<TextView>(R.id.tv_name).text = item.product.name
            v.findViewById<TextView>(R.id.tv_price).text = Cop.fmt(item.product.price)
            v.findViewById<TextView>(R.id.tv_qty).text = item.qty.toString()
            v.findViewById<View>(R.id.btn_plus).setOnClickListener {
                if (item.qty < item.product.stock) { item.qty++; refresh() } else requireContext().toast(getString(R.string.no_hay_mas_unidades_disponibles))
            }
            v.findViewById<View>(R.id.btn_minus).setOnClickListener { if (item.qty > 1) { item.qty--; refresh() } }
            v.findViewById<View>(R.id.btn_remove).setOnClickListener { SampleData.cart.remove(item); refresh() }
        }
        view.findViewById<Button>(R.id.btn_clear).setOnClickListener {
            requireContext().confirm(getString(R.string.vaciar_carrito), getString(R.string.desea_quitar_todos_los_productos)) { SampleData.cart.clear(); refresh() }
        }
        view.findViewById<Button>(R.id.btn_checkout).setOnClickListener {
            if (SampleData.cart.isEmpty()) requireContext().toast(getString(R.string.agregue_productos_antes_de_pagar))
            else startActivity(android.content.Intent(requireContext(), CheckoutActivity::class.java))
        }
    }

    override fun onResume() { super.onResume(); refresh() }

    private fun refresh() {
        rv.adapter?.notifyDataSetChanged()
        val empty = SampleData.cart.isEmpty()
        val sub = SampleData.cartSubtotal(); val iva = SampleData.cartIva(); val ship = if (empty) 0L else 8000L
        root.findViewById<TextView>(R.id.tv_subtotal).text = Cop.fmt(sub)
        root.findViewById<TextView>(R.id.tv_iva).text = Cop.fmt(iva)
        root.findViewById<TextView>(R.id.tv_shipping).text = Cop.fmt(ship)
        root.findViewById<TextView>(R.id.tv_total).text = Cop.fmt(sub + iva + ship)
        root.findViewById<View>(R.id.tv_empty).visibility = if (empty) View.VISIBLE else View.GONE
    }
}

/** Historial de pedidos del comprador, retracto y factura. */
class BuyerOrdersFragment : Fragment(R.layout.fragment_buyer_orders) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_orders)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val chips = view.findViewById<ChipGroup>(R.id.cg_orders)
        fun render() {
            val list = when (chips.checkedChipId) {
                R.id.c_progress -> SampleData.buyerOrders.filter { it.status != "Entregado" }
                R.id.c_done -> SampleData.buyerOrders.filter { it.status == "Entregado" }
                else -> SampleData.buyerOrders
            }
            rv.adapter = SimpleAdapter(list, R.layout.item_order) { v, o ->
                v.findViewById<TextView>(R.id.tv_id).text = o.id
                v.findViewById<TextView>(R.id.tv_meta).text = o.meta
                v.findViewById<TextView>(R.id.tv_total).text = Cop.fmt(o.total)
                v.findViewById<TextView>(R.id.tv_status).applyStatus(o.status)
            }
        }
        chips.setOnCheckedStateChangeListener { _, _ -> render() }
        view.findViewById<Button>(R.id.btn_retract).setOnClickListener { requireContext().toast(getString(R.string.solicitud_de_retracto_registrada_5)) }
        view.findViewById<Button>(R.id.btn_invoice).setOnClickListener { requireContext().toast(getString(R.string.la_factura_electronica_se_enviara)) }
        render()
    }
}

class BuyerProfileFragment : ProfileFragment(R.layout.fragment_buyer_profile)
