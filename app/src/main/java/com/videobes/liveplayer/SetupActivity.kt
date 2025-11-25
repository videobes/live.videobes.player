package com.videobes.liveplayer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SetupActivity : AppCompatActivity() {

    private lateinit var checkAccept: CheckBox
    private lateinit var checkDecline: CheckBox
    private lateinit var btnContinue: Button
    private lateinit var btnAbort: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        // Se já aceitou kiosk uma vez, pula
        if (Prefs.isSetupDone(this)) {
            startActivity(Intent(this, PlayerActivity::class.java))
            finish()
            return
        }

        checkAccept = findViewById(R.id.checkAccept)
        checkDecline = findViewById(R.id.checkDecline)
        btnContinue = findViewById(R.id.btnContinue)
        btnAbort = findViewById(R.id.btnAbort)

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
            Prefs.setSetupDone(this, true)
            Prefs.setKioskEnabled(this, true)

            val i = Intent(this, PlayerActivity::class.java)
            startActivity(i)
            finish()
        }

        btnAbort.setOnClickListener {
            finishAffinity()
        }
    }
}
