package com.raizmarket.app


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class CheckoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
        val rgShip = findViewById<RadioGroup>(R.id.rg_shipping)
        val rgPay = findViewById<RadioGroup>(R.id.rg_payment)
        val card = findViewById<View>(R.id.ll_card)
        val pay = findViewById<Button>(R.id.btn_pay)
        val address = findViewById<TextInputEditText>(R.id.et_address)
        val number = findViewById<TextInputEditText>(R.id.et_card)
        val expiry = findViewById<TextInputEditText>(R.id.et_expiry)
        val cvv = findViewById<TextInputEditText>(R.id.et_cvv)

        fun shipping() = if (rgShip.checkedRadioButtonId == R.id.rb_express) 15000L else 8000L
        fun total() = SampleData.cartSubtotal() + SampleData.cartIva() + shipping()
        fun refresh() {
            card.visibility = if (rgPay.checkedRadioButtonId == R.id.rb_card) View.VISIBLE else View.GONE
            pay.text = getString(R.string.pagar) + " " + Cop.fmt(total())
        }
        rgShip.setOnCheckedChangeListener { _, _ -> refresh() }
        rgPay.setOnCheckedChangeListener { _, _ -> refresh() }
        refresh()

        pay.setOnClickListener {
            listOf(address, number, expiry, cvv).forEach { it.showError(null) }
            val needCard = rgPay.checkedRadioButtonId == R.id.rb_card
            when {
                address.value().isEmpty() -> address.showError(getString(R.string.ingrese_la_direccion_de_entrega))
                needCard && number.value().length < 12 -> number.showError(getString(R.string.numero_de_tarjeta_invalido))
                needCard && expiry.value().length < 4 -> expiry.showError(getString(R.string.fecha_invalida))
                needCard && cvv.value().length < 3 -> cvv.showError(getString(R.string.cvv_invalido))
                else -> {
                    val id = 1043 + SampleData.buyerOrders.size
                    SampleData.buyerOrders.add(0, Order("Pedido #" + id, "Hoy · " + SampleData.cart.size + " ítems", total(), "Pagado"))
                    SampleData.cart.clear()
                    MaterialAlertDialogBuilder(this).setTitle(getString(R.string.pago_aprobado))
                        .setMessage(getString(R.string.recibira_el_comprobante_y_la_factu))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ver_mis_pedidos)) { _, _ ->
                            startActivity(Intent(this, BuyerActivity::class.java).putExtra("open", "orders")
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
                            finish()
                        }.show()
                }
            }
        }
    }
}
