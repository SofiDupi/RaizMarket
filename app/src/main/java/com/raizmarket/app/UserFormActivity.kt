package com.raizmarket.app


import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

/** Crear / editar / suspender / eliminar usuarios (solo administrador). */
class UserFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_form)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        val name = findViewById<TextInputEditText>(R.id.et_name)
        val email = findViewById<TextInputEditText>(R.id.et_email)
        val role = findViewById<AutoCompleteTextView>(R.id.dd_role)
        val active = findViewById<MaterialSwitch>(R.id.sw_active)
        val suspend = findViewById<Button>(R.id.btn_suspend)
        val delete = findViewById<Button>(R.id.btn_delete)
        val roles = listOf("Comprador", "Vendedor", "Administrador")
        role.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, roles))
        role.setText(roles[0], false)

        val user = SampleData.users.getOrNull(intent.getIntExtra("index", -1))
        if (user != null) {
            toolbar.title = getString(R.string.editar_usuario)
            name.setText(user.name); email.setText(user.email); role.setText(user.role, false)
            active.isChecked = user.status == "Activo"
        } else {
            suspend.visibility = android.view.View.GONE
            delete.visibility = android.view.View.GONE
        }

        findViewById<Button>(R.id.btn_save).setOnClickListener {
            name.showError(null); email.showError(null)
            val duplicate = SampleData.users.any { it !== user && it.email.equals(email.value(), true) }
            when {
                name.value().isEmpty() -> name.showError(getString(R.string.ingrese_el_nombre))
                !email.value().contains("@") -> email.showError(getString(R.string.ingrese_un_correo_valido))
                duplicate -> email.showError(getString(R.string.ese_correo_ya_esta_registrado))
                else -> {
                    val status = if (active.isChecked) "Activo" else "Suspendido"
                    if (user != null) { user.name = name.value(); user.email = email.value(); user.role = role.text.toString(); user.status = status }
                    else SampleData.users.add(AppUser(name.value(), email.value(), role.text.toString(), status))
                    toast(getString(R.string.usuario_guardado))
                    finish()
                }
            }
        }
        suspend.setOnClickListener {
            user?.status = "Suspendido"; toast(getString(R.string.usuario_suspendido)); finish()
        }
        delete.setOnClickListener {
            confirm(getString(R.string.eliminar_usuario), getString(R.string.los_datos_personales_se_anonimizar)) {
                user?.let { SampleData.users.remove(it) }; finish()
            }
        }
    }
}
