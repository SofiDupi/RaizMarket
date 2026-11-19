package com.raizmarket.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val email = findViewById<TextInputEditText>(R.id.et_email)
        val pass = findViewById<TextInputEditText>(R.id.et_password)

        findViewById<Button>(R.id.btn_login).setOnClickListener {

            val userEmail = email.text.toString().trim()
            val password = pass.text.toString()

            email.error = null
            pass.error = null

            when {
                !android.util.Patterns.EMAIL_ADDRESS
                    .matcher(userEmail).matches() -> {
                    email.error = "Ingresa un correo válido"
                }

                password.isEmpty() -> {
                    pass.error = "Ingresa tu contraseña"
                }

                else -> {
                    loginUser(userEmail, password)
                }
            }
        }

        findViewById<Button>(R.id.btn_google).setOnClickListener {
            Toast.makeText(
                this,
                "El inicio con Google todavía no está disponible",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<View>(R.id.ll_fingerprint).setOnClickListener {
            Toast.makeText(
                this,
                "Primero debemos configurar el acceso biométrico seguro",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<View>(R.id.tv_forgot).setOnClickListener {
            startActivity(
                Intent(this, ForgotPasswordActivity::class.java)
            )
        }

        findViewById<View>(R.id.tv_register).setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }

    private fun loginUser(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val user = result.user

                if (user == null) {
                    auth.signOut()
                    showMessage("No se pudo verificar el usuario")
                    return@addOnSuccessListener
                }

                db.collection("users")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->

                        if (!document.exists()) {
                            auth.signOut()
                            showMessage("No se encontró el perfil del usuario")
                            return@addOnSuccessListener
                        }

                        val role = document.getString("role")

                        val userRole = when (role) {
                            "Comprador" -> Role.BUYER
                            "Vendedor" -> Role.SELLER
                            "Administrador" -> Role.ADMIN
                            else -> null
                        }

                        if (userRole == null) {
                            auth.signOut()
                            showMessage("El rol del usuario no es válido")
                            return@addOnSuccessListener
                        }

                        Session.role = userRole
                        goHome(userRole)
                    }
                    .addOnFailureListener { error ->
                        auth.signOut()
                        showMessage(
                            "Error al consultar el perfil: ${error.message}"
                        )
                    }
            }
            .addOnFailureListener { error ->
                showMessage(
                    "No se pudo iniciar sesión: ${error.message}"
                )
            }
    }

    private fun goHome(role: Role) {

        val target = when (role) {
            Role.BUYER -> BuyerActivity::class.java
            Role.SELLER -> SellerActivity::class.java
            Role.ADMIN -> AdminActivity::class.java
        }

        startActivity(
            Intent(this, target).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        )

        finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
