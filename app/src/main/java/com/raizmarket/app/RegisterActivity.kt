package com.raizmarket.app


import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
        val name = findViewById<TextInputEditText>(R.id.et_name)
        val email = findViewById<TextInputEditText>(R.id.et_email)
        val pass = findViewById<TextInputEditText>(R.id.et_password)
        val pass2 = findViewById<TextInputEditText>(R.id.et_password2)
        val strong = Regex("(?=.*[A-Z])(?=.*\\d).{8,}")

        findViewById<Button>(R.id.btn_register).setOnClickListener {
            listOf(name, email, pass, pass2).forEach { it.showError(null) }
            when {
                name.value().isEmpty() -> name.showError(getString(R.string.ingrese_su_nombre))
                !email.value().contains("@") -> email.showError(getString(R.string.ingrese_un_correo_valido))
                !strong.matches(pass.value()) -> pass.showError(getString(R.string.minimo_8_caracteres_una_mayuscula))
                pass.value() != pass2.value() -> pass2.showError(getString(R.string.las_contrasenas_no_coinciden))
                !findViewById<CheckBox>(R.id.cb_terms).isChecked || !findViewById<CheckBox>(R.id.cb_data).isChecked ->
                    toast(getString(R.string.debe_aceptar_los_terminos_y_la_aut))
                else -> {
                    val seller = findViewById<MaterialButtonToggleGroup>(R.id.toggle_role).checkedButtonId == R.id.btn_role_seller
                    SampleData.users.add(AppUser(name.value(), email.value(), if (seller) "Vendedor" else "Comprador", "Activo"))
                    toast(getString(R.string.cuenta_creada_verifique_su_correo))
                    finish()
                }
            }
        }
    }
}
