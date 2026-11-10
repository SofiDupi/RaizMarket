package com.raizmarket.app


import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        val product = SampleData.find(intent.getStringExtra("name") ?: "") ?: SampleData.featured.first()
        var qty = 1
        findViewById<TextView>(R.id.tv_name).text = product.name
        findViewById<TextView>(R.id.tv_price).text = Cop.fmt(product.price) + " COP"
        findViewById<TextView>(R.id.tv_seller).text = getString(R.string.vendido_por) + " " + product.seller
        val tvQty = findViewById<TextView>(R.id.tv_qty)
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<Button>(R.id.btn_minus).setOnClickListener { if (qty > 1) { qty--; tvQty.text = qty.toString() } }
        findViewById<Button>(R.id.btn_plus).setOnClickListener {
            if (qty < product.stock) { qty++; tvQty.text = qty.toString() } else toast(getString(R.string.no_hay_mas_unidades_disponibles))
        }
        findViewById<Button>(R.id.btn_add).setOnClickListener {
            val existing = SampleData.cart.firstOrNull { it.product === product }
            if (existing != null) existing.qty = minOf(product.stock, existing.qty + qty)
            else SampleData.cart.add(CartItem(product, qty))
            toast(getString(R.string.producto_agregado_al_carrito))
            finish()
        }
    }
}
