package com.videobes.liveplayer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class SetupActivity : AppCompatActivity() {

    private lateinit var checkAccept: CheckBox
    private lateinit var checkDecline: CheckBox
    private lateinit var btnContinue: Button
    private lateinit var btnAbort: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        // Se o usuário já aceitou o modo kiosk, pula direto
        if (Prefs.isKioskAccepted(this)) {
            startActivity(Intent(this, PlayerActivity::class.java))
            finish()
            return
        }

        checkAccept = findViewById(R.id.checkAccept)
        checkDecline = findViewById(R.id.checkDecline)
        btnContinue = findViewById(R.id.btnContinue)
        btnAbort = findViewById(R.id.btnAbort)

        // Começam desabilitados
        btnContinue.isEnabled = false
        btnAbort.isEnabled = false

        checkAccept.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkDecline.isChecked = false
                btnContinue.isEnabled = true
                btnAbort.isEnabled = false
            } else {
                btnContinue.isEnabled = false
            }
        }

        checkDecline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkAccept.isChecked = false
                btnAbort.isEnabled = true
                btnContinue.isEnabled = false
            } else {
                btnAbort.isEnabled = false
            }
        }

        btnContinue.setOnClickListener {
            // Salva que o usuário ACEITOU o modo kiosk
            Prefs.setKioskAccepted(this, true)

            // Agora vai para o Player, onde será feita configuração de mídia
            startActivity(Intent(this, PlayerActivity::class.java))
            finish()
        }

        btnAbort.setOnClickListener {
            finishAffinity()
        }
    }
}
