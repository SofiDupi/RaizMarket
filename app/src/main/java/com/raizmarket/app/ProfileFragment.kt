package com.raizmarket.app


import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Perfil compartido por comprador y vendedor: foto (camara/galeria), datos, consentimientos y salida. */
open class ProfileFragment(layout: Int) : Fragment(layout) {
    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bmp?.let { showAvatar { iv -> iv.setImageBitmap(it) } }
    }
    private val pickPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { showAvatar { iv -> iv.setImageURI(it) } }
    }

    private fun showAvatar(set: (ImageView) -> Unit) {
        val iv = view?.findViewById<ImageView>(R.id.iv_avatar) ?: return
        iv.imageTintList = null
        iv.setPadding(0, 0, 0, 0)
        set(iv)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (Session.role == Role.SELLER) {
            view.setField(R.id.et_name, "Finca La Esperanza")
            view.setField(R.id.et_desc, "Café de altura del Huila")
            view.setField(R.id.et_nit, "900.123.456-7")
            view.setField(R.id.et_account, "Bancolombia ****4521")
        } else {
            view.setField(R.id.et_name, "Laura Gómez")
            view.setField(R.id.et_phone, "300 123 4567")
            view.setField(R.id.et_address, "Cra 15 # 85-20, Bogotá")
        }
        val ctx = requireContext()
        view.findViewById<Button>(R.id.btn_photo).setOnClickListener {
            MaterialAlertDialogBuilder(ctx).setTitle(getString(R.string.foto_de_perfil))
                .setItems(arrayOf(getString(R.string.camara), getString(R.string.galeria))) { _, which ->
                    if (which == 0) takePhoto.launch(null) else pickPhoto.launch("image/*")
                }.show()
        }
        view.findViewById<Button>(R.id.btn_save).setOnClickListener { ctx.toast(getString(R.string.cambios_guardados)) }
        view.findViewById<Button>(R.id.btn_logout).setOnClickListener { ctx.logout() }
        (view.findViewById<View>(R.id.btn_consents) as? Button)?.setOnClickListener {
            MaterialAlertDialogBuilder(ctx).setTitle(getString(R.string.consentimientos))
                .setMultiChoiceItems(arrayOf(getString(R.string.tratamiento_de_datos), getString(R.string.notificaciones_y_marketing), getString(R.string.ubicacion)),
                    booleanArrayOf(true, true, false), null)
                .setPositiveButton(getString(R.string.guardar)) { _, _ -> ctx.toast(getString(R.string.consentimientos_actualizados)) }
                .setNegativeButton(android.R.string.cancel, null).show()
        }
        (view.findViewById<View>(R.id.btn_delete) as? Button)?.setOnClickListener {
            ctx.confirm(getString(R.string.eliminar_cuenta), getString(R.string.esta_accion_solicita_la_supresion)) {
                ctx.confirm(getString(R.string.confirmacion_final), getString(R.string.seguro_que_desea_eliminar_su_cuent)) { ctx.logout() }
            }
        }
    }
}
