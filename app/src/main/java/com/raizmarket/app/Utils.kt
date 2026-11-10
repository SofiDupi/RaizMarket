package com.raizmarket.app


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/** Adaptador generico: una lista, un layout de item y una funcion que enlaza los datos. */
class SimpleAdapter<T>(
    private val items: List<T>,
    @LayoutRes private val layout: Int,
    private val bind: (View, T) -> Unit
) : RecyclerView.Adapter<SimpleAdapter.VH>() {
    class VH(v: View) : RecyclerView.ViewHolder(v)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(layout, parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: VH, position: Int) = bind(holder.itemView, items[position])
}

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Context.confirm(title: String, msg: String, onYes: () -> Unit) {
    MaterialAlertDialogBuilder(this).setTitle(title).setMessage(msg)
        .setPositiveButton(android.R.string.ok) { _, _ -> onYes() }
        .setNegativeButton(android.R.string.cancel, null).show()
}

fun Context.logout() {
    startActivity(Intent(this, LoginActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
}

/** Colorea la insignia segun el estado (verde, ambar, rojo o gris). */
fun TextView.applyStatus(status: String) {
    text = status
    setBackgroundResource(when (status) {
        "Aprobado", "Activo", "Entregado", "Pagado", "Enviado" -> R.drawable.bg_badge_green
        "Pendiente", "Nuevo", "En camino", "Preparando" -> R.drawable.bg_badge_amber
        "Rechazado", "Suspendido" -> R.drawable.bg_badge_red
        else -> R.drawable.bg_badge_gray
    })
}

fun TextInputEditText.value(): String = text?.toString()?.trim().orEmpty()

fun TextInputEditText.showError(msg: String?) {
    var p = parent
    while (p != null && p !is TextInputLayout) p = p.parent
    (p as? TextInputLayout)?.error = msg
}

fun View.setField(id: Int, value: String) {
    (findViewById<View>(id) as? TextInputEditText)?.setText(value)
}

fun bindProduct(v: View, p: Product) {
    v.findViewById<TextView>(R.id.tv_name).text = p.name
    v.findViewById<TextView>(R.id.tv_seller).text = p.seller
    v.findViewById<TextView>(R.id.tv_price).text = Cop.fmt(p.price)
    (v.findViewById<View>(R.id.tv_distance) as? TextView)?.text = p.distance
    v.setOnClickListener {
        v.context.startActivity(Intent(v.context, ProductDetailActivity::class.java).putExtra("name", p.name))
    }
}
