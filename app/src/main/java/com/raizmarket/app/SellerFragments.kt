package com.raizmarket.app


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SellerDashboardFragment : Fragment(R.layout.fragment_seller_dashboard) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tv_active).text = SampleData.sellerProducts.count { it.status == "Aprobado" }.toString()
        view.findViewById<TextView>(R.id.tv_low).text = SampleData.sellerProducts.count { it.stock <= 5 }.toString()
        view.findViewById<TextView>(R.id.tv_new_orders).text = SampleData.sellerOrders.count { it.status == "Nuevo" }.toString()
        view.findViewById<Button>(R.id.btn_withdraw).setOnClickListener { requireContext().toast(getString(R.string.solicitud_de_retiro_enviada)) }
    }
}

/** CRUD de productos del vendedor: listar, crear, editar y eliminar. */
class SellerProductsFragment : Fragment(R.layout.fragment_seller_products) {
    private lateinit var rv: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv = view.findViewById(R.id.rv_products)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = SimpleAdapter(SampleData.sellerProducts, R.layout.item_seller_product) { v, p ->
            v.findViewById<TextView>(R.id.tv_name).text = p.name
            v.findViewById<TextView>(R.id.tv_stock).text = "Stock " + p.stock
            v.findViewById<TextView>(R.id.tv_price).text = Cop.fmt(p.price)
            v.findViewById<TextView>(R.id.tv_status).applyStatus(p.status)
            v.findViewById<View>(R.id.btn_edit).setOnClickListener { openForm(SampleData.sellerProducts.indexOf(p)) }
            v.findViewById<View>(R.id.btn_delete).setOnClickListener {
                requireContext().confirm(getString(R.string.eliminar_producto), getString(R.string.desea_eliminar) + " " + p.name + "?") {
                    SampleData.sellerProducts.remove(p); rv.adapter?.notifyDataSetChanged()
                }
            }
        }
        view.findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener { openForm(-1) }
    }

    override fun onResume() { super.onResume(); rv.adapter?.notifyDataSetChanged() }

    private fun openForm(index: Int) =
        startActivity(Intent(requireContext(), ProductFormActivity::class.java).putExtra("index", index))
}

class SellerOrdersFragment : Fragment(R.layout.fragment_seller_orders) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_orders)
        rv.layoutManager = LinearLayoutManager(requireContext())
        val chips = view.findViewById<ChipGroup>(R.id.cg_orders)
        fun render() {
            val list = when (chips.checkedChipId) {
                R.id.c_progress -> SampleData.sellerOrders.filter { it.status == "Preparando" }
                R.id.c_sent -> SampleData.sellerOrders.filter { it.status == "Enviado" }
                else -> SampleData.sellerOrders.filter { it.status == "Nuevo" || it.status == "Pagado" }
            }
            rv.adapter = SimpleAdapter(list, R.layout.item_seller_order) { v, o ->
                v.findViewById<TextView>(R.id.tv_title).text = o.id + " · " + o.customer
                v.findViewById<TextView>(R.id.tv_meta).text = o.meta
                v.findViewById<TextView>(R.id.tv_total).text = Cop.fmt(o.total)
                v.findViewById<TextView>(R.id.tv_status).applyStatus(o.status)
                val ship = v.findViewById<Button>(R.id.btn_ship)
                ship.visibility = if (o.status == "Enviado") View.GONE else View.VISIBLE
                ship.setOnClickListener {
                    o.status = "Enviado"
                    requireContext().toast(getString(R.string.pedido_marcado_como_enviado))
                    render()
                }
            }
        }
        chips.setOnCheckedStateChangeListener { _, _ -> render() }
        render()
    }
}

class SellerProfileFragment : ProfileFragment(R.layout.fragment_seller_profile)
