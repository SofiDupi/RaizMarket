package com.raizmarket.app


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val email = findViewById<TextInputEditText>(R.id.et_email)
        val pass = findViewById<TextInputEditText>(R.id.et_password)

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            email.showError(null); pass.showError(null)
            when {
                !email.value().contains("@") -> email.showError(getString(R.string.ingrese_un_correo_valido))
                pass.value().isEmpty() -> pass.showError(getString(R.string.ingrese_su_contrasena))
                else -> goHome()
            }
        }
        findViewById<Button>(R.id.btn_google).setOnClickListener {
            toast(getString(R.string.google_sign_in_se_conectara_con_fi))
            goHome()
        }
        findViewById<View>(R.id.ll_fingerprint).setOnClickListener { askBiometric() }
        findViewById<View>(R.id.tv_forgot).setOnClickListener { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }
        findViewById<View>(R.id.tv_register).setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }

    private fun selectedRole(): Role = when (findViewById<ChipGroup>(R.id.cg_role).checkedChipId) {
        R.id.chip_seller -> Role.SELLER
        R.id.chip_admin -> Role.ADMIN
        else -> Role.BUYER
    }

    /** Carga el grafo de navegacion segun el rol (en produccion el rol viene del token firmado). */
    private fun goHome() {
        Session.role = selectedRole()
        val target = when (Session.role) {
            Role.BUYER -> BuyerActivity::class.java
            Role.SELLER -> SellerActivity::class.java
            Role.ADMIN -> AdminActivity::class.java
        }
        startActivity(Intent(this, target).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        finish()
    }

    private fun askBiometric() {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
        if (BiometricManager.from(this).canAuthenticate(authenticators) != BiometricManager.BIOMETRIC_SUCCESS) {
            toast(getString(R.string.este_dispositivo_no_tiene_una_huel))
            return
        }
        val prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) { goHome() }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON && errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        toast(errString.toString())
                    }
                }
            })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.verificacion_biometrica))
            .setSubtitle(getString(R.string.toque_el_sensor_de_huella))
            .setAllowedAuthenticators(authenticators)
            .setNegativeButtonText(getString(R.string.usar_contrasena))
            .build()
        prompt.authenticate(info)
    }
}
