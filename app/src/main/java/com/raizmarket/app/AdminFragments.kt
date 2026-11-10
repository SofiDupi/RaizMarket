package com.raizmarket.app


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminDashboardFragment : Fragment(R.layout.fragment_admin_dashboard) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tv_pending).text = SampleData.moderation.count { it.status == "Pendiente" }.toString()
    }
}

/** CRUD de usuarios: listar con filtro por rol, crear, editar y eliminar. */
class AdminUsersFragment : Fragment(R.layout.fragment_admin_users) {
    private lateinit var rv: RecyclerView
    private lateinit var chips: ChipGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv = view.findViewById(R.id.rv_users)
        rv.layoutManager = LinearLayoutManager(requireContext())
        chips = view.findViewById(R.id.cg_roles)
        chips.setOnCheckedStateChangeListener { _, _ -> render() }
        view.findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener { openForm(-1) }
    }

    override fun onResume() { super.onResume(); render() }

    private fun openForm(index: Int) =
        startActivity(Intent(requireContext(), UserFormActivity::class.java).putExtra("index", index))

    private fun render() {
        val role = when (chips.checkedChipId) {
            R.id.c_buyer -> "Comprador"; R.id.c_seller -> "Vendedor"; R.id.c_admin -> "Administrador"; else -> null
        }
        val list = SampleData.users.filter { role == null || it.role == role }
        rv.adapter = SimpleAdapter(list, R.layout.item_user) { v, u ->
            v.findViewById<TextView>(R.id.tv_initial).text = u.name.take(1).uppercase()
            v.findViewById<TextView>(R.id.tv_name).text = u.name
            v.findViewById<TextView>(R.id.tv_meta).text = u.email + " · " + u.role
            v.findViewById<TextView>(R.id.tv_status).applyStatus(u.status)
            v.findViewById<View>(R.id.btn_edit).setOnClickListener { openForm(SampleData.users.indexOfFirst { it === u }) }
            v.findViewById<View>(R.id.btn_delete).setOnClickListener {
                requireContext().confirm(getString(R.string.eliminar_usuario), getString(R.string.desea_eliminar_a) + " " + u.name + "?") {
                    SampleData.users.remove(u); render()
                }
            }
        }
    }
}

/** Moderacion de productos: aprobar o rechazar con motivo obligatorio. */
class AdminProductsFragment : Fragment(R.layout.fragment_admin_products) {
    private lateinit var rv: RecyclerView
    private lateinit var chips: ChipGroup

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv = view.findViewById(R.id.rv_moderation)
        rv.layoutManager = LinearLayoutManager(requireContext())
        chips = view.findViewById(R.id.cg_status)
        chips.setOnCheckedStateChangeListener { _, _ -> render() }
        render()
    }

    private fun render() {
        val status = when (chips.checkedChipId) { R.id.c_approved -> "Aprobado"; R.id.c_rejected -> "Rechazado"; else -> "Pendiente" }
        val list = SampleData.moderation.filter { it.status == status }
        rv.adapter = SimpleAdapter(list, R.layout.item_moderation) { v, p ->
            v.findViewById<TextView>(R.id.tv_name).text = p.name
            v.findViewById<TextView>(R.id.tv_meta).text = p.seller + " · " + Cop.fmt(p.price)
            v.findViewById<TextView>(R.id.tv_status).applyStatus(p.status)
            val actions = if (p.status == "Pendiente") View.VISIBLE else View.GONE
            v.findViewById<View>(R.id.btn_approve).visibility = actions
            v.findViewById<View>(R.id.btn_reject).visibility = actions
            v.findViewById<View>(R.id.btn_approve).setOnClickListener {
                p.status = "Aprobado"; requireContext().toast(getString(R.string.producto_aprobado_y_publicado)); render()
            }
            v.findViewById<View>(R.id.btn_reject).setOnClickListener { askReason(p) }
        }
    }

    private fun askReason(p: Product) {
        val reason = EditText(requireContext()).apply { hint = getString(R.string.motivo_obligatorio); setPadding(48, 32, 48, 32) }
        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.rechazar_con_motivo)).setView(reason)
            .setPositiveButton(getString(R.string.rechazar)) { _, _ ->
                if (reason.text.isNullOrBlank()) requireContext().toast(getString(R.string.debe_escribir_un_motivo))
                else { p.status = "Rechazado"; requireContext().toast(getString(R.string.producto_rechazado_se_notifico_al)); render() }
            }
            .setNegativeButton(android.R.string.cancel, null).show()
    }
}

class AdminReportsFragment : Fragment(R.layout.fragment_admin_reports) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_transactions)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = SimpleAdapter(SampleData.transactions, R.layout.item_transaction) { v, t ->
            v.findViewById<TextView>(R.id.tv_ref).text = t.ref
            v.findViewById<TextView>(R.id.tv_meta).text = t.meta
            v.findViewById<TextView>(R.id.tv_amount).text = Cop.fmt(t.amount)
            v.findViewById<TextView>(R.id.tv_status).applyStatus(t.status)
        }
        view.findViewById<Button>(R.id.btn_csv).setOnClickListener { requireContext().toast(getString(R.string.reporte_csv_generado_sin_datos_de)) }
        view.findViewById<Button>(R.id.btn_pdf).setOnClickListener { requireContext().toast(getString(R.string.reporte_pdf_generado_sin_datos_de)) }
    }
}
