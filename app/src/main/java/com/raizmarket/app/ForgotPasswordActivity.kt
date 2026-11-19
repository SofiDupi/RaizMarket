package com.raizmarket.app

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        findViewById<MaterialToolbar>(R.id.toolbar)
            .setNavigationOnClickListener {
                finish()
            }

        val email = findViewById<TextInputEditText>(R.id.et_email)

        findViewById<Button>(R.id.btn_send).setOnClickListener {

            val userEmail = email.text.toString().trim()

            email.error = null

            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(userEmail).matches()) {

                email.error = "Ingresa un correo válido"

            } else {

                auth.sendPasswordResetEmail(userEmail)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Si el correo está registrado, recibirás un enlace de recuperación.",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "No se pudo procesar la solicitud. Intenta nuevamente.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }
}
