package com.raizmarket.app


import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

/** Crear / editar producto con camara, galeria y ubicacion de despacho. */
class ProductFormActivity : AppCompatActivity() {
    private lateinit var photo: ImageView

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bmp?.let { show { iv -> iv.setImageBitmap(it) } }
    }
    private val pickPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { show { iv -> iv.setImageURI(it) } }
    }
    private fun show(set: (ImageView) -> Unit) { photo.imageTintList = null; photo.setPadding(0, 0, 0, 0); set(photo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_form)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        photo = findViewById(R.id.iv_photo)
        val name = findViewById<TextInputEditText>(R.id.et_name)
        val desc = findViewById<TextInputEditText>(R.id.et_desc)
        val price = findViewById<TextInputEditText>(R.id.et_price)
        val stock = findViewById<TextInputEditText>(R.id.et_stock)
        val category = findViewById<AutoCompleteTextView>(R.id.dd_category)
        val location = findViewById<TextView>(R.id.tv_location)
        category.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, SampleData.categories))

        val index = intent.getIntExtra("index", -1)
        val editing = SampleData.sellerProducts.getOrNull(index)
        if (editing != null) {
            toolbar.title = getString(R.string.editar_producto)
            name.setText(editing.name); price.setText(editing.price.toString()); stock.setText(editing.stock.toString())
            category.setText(editing.category, false)
            findViewById<Button>(R.id.btn_publish).text = getString(R.string.guardar_cambios)
        }

        findViewById<Button>(R.id.btn_camera).setOnClickListener { takePhoto.launch(null) }
        findViewById<Button>(R.id.btn_gallery).setOnClickListener { pickPhoto.launch("image/*") }
        findViewById<Button>(R.id.btn_location).setOnClickListener {
            // Demo: la geolocalizacion real (FusedLocationProvider + permiso) se integra en la siguiente fase.
            location.text = "Lat 2,927 · Lon -75,281"
            toast(getString(R.string.ubicacion_de_despacho_registrada))
        }
        findViewById<Button>(R.id.btn_publish).setOnClickListener {
            listOf(name, price, stock).forEach { it.showError(null) }
            val p = price.value().toLongOrNull(); val s = stock.value().toIntOrNull()
            when {
                name.value().length < 3 -> name.showError(getString(R.string.ingrese_un_nombre_minimo_3_letras))
                p == null || p <= 0 -> price.showError(getString(R.string.precio_invalido))
                s == null || s < 0 -> stock.showError(getString(R.string.stock_invalido))
                else -> {
                    val cat = category.text.toString().ifEmpty { "Café" }
                    if (editing != null) {
                        editing.name = name.value(); editing.price = p!!; editing.stock = s!!; editing.category = cat
                        editing.status = "Pendiente" // los cambios sensibles vuelven a moderacion
                    } else {
                        SampleData.sellerProducts.add(0, Product(name.value(), p!!, "Finca La Esperanza", s!!, "Pendiente", category = cat))
                    }
                    toast(getString(R.string.producto_enviado_a_moderacion))
                    finish()
                }
            }
        }
    }
}
