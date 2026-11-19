package com.raizmarket.app

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        findViewById<MaterialToolbar>(R.id.toolbar)
            .setNavigationOnClickListener { finish() }

        val name = findViewById<TextInputEditText>(R.id.et_name)
        val email = findViewById<TextInputEditText>(R.id.et_email)
        val pass = findViewById<TextInputEditText>(R.id.et_password)
        val pass2 = findViewById<TextInputEditText>(R.id.et_password2)

        val strong = Regex("(?=.*[A-Z])(?=.*\\d).{8,}")

        findViewById<Button>(R.id.btn_register).setOnClickListener {

            val userName = name.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val password = pass.text.toString()
            val confirmPassword = pass2.text.toString()

            name.error = null
            email.error = null
            pass.error = null
            pass2.error = null

            when {
                userName.isEmpty() -> {
                    name.error = getString(R.string.ingrese_su_nombre)
                }

                !android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches() -> {
                    email.error = getString(R.string.ingrese_un_correo_valido)
                }

                !strong.matches(password) -> {
                    pass.error = getString(R.string.minimo_8_caracteres_una_mayuscula)
                }

                password != confirmPassword -> {
                    pass2.error = getString(R.string.las_contrasenas_no_coinciden)
                }

                !findViewById<CheckBox>(R.id.cb_terms).isChecked ||
                        !findViewById<CheckBox>(R.id.cb_data).isChecked -> {
                    Toast.makeText(
                        this,
                        getString(R.string.debe_aceptar_los_terminos_y_la_aut),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {

                    val seller =
                        findViewById<MaterialButtonToggleGroup>(R.id.toggle_role)
                            .checkedButtonId == R.id.btn_role_seller

                    val role = if (seller) "Vendedor" else "Comprador"

                    auth.createUserWithEmailAndPassword(userEmail, password)
                        .addOnSuccessListener { result ->

                            val user = result.user

                            if (user == null) {
                                Toast.makeText(
                                    this,
                                    "No se pudo crear la cuenta.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@addOnSuccessListener
                            }

                            val userData = hashMapOf(
                                "uid" to user.uid,
                                "name" to userName,
                                "email" to userEmail,
                                "role" to role,
                                "status" to "Activo",
                                "createdAt" to FieldValue.serverTimestamp()
                            )

                            db.collection("users")
                                .document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {

                                    Toast.makeText(
                                        this,
                                        "¡Cuenta creada correctamente!",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    finish()
                                }
                                .addOnFailureListener { error ->

                                    Toast.makeText(
                                        this,
                                        "La cuenta se creó, pero no se pudo guardar el perfil: ${error.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                        .addOnFailureListener { error ->

                            Toast.makeText(
                                this,
                                "No se pudo crear la cuenta: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
        }
    }
}