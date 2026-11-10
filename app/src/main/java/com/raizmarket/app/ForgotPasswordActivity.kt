package com.raizmarket.app


import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
        val email = findViewById<TextInputEditText>(R.id.et_email)
        findViewById<Button>(R.id.btn_send).setOnClickListener {
            if (!email.value().contains("@")) {
                email.showError(getString(R.string.ingrese_un_correo_valido))
            } else {
                // Respuesta generica para evitar enumeracion de usuarios.
                toast(getString(R.string.si_el_correo_esta_registrado_recib))
                finish()
            }
        }
    }
}
