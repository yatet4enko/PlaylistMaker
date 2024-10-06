package com.practicum.playlistmaker.features.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.features.settings.domain.api.SettingsInteractor

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitch: SwitchMaterial

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsInteractor = (applicationContext as App).creator.provideSettingsInteractor()

        setContentView(R.layout.activity_settings)

        themeSwitch = findViewById(R.id.theme_swith)

        themeSwitch.isChecked = settingsInteractor.getIsDarkTheme()

        themeSwitch.setOnCheckedChangeListener { switcher, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }

        findViewById<Toolbar>(R.id.toolbar).let {
            title = ""
            setSupportActionBar(it)
            title = getString(R.string.settings)

            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        findViewById<FrameLayout>(R.id.share_app).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, PRACTICUM_URL);
            intent.setType("text/plain");

            val result = runCatching {
                startActivity(intent)
            }

            result.onFailure {
                Toast.makeText(
                    this,
                    getString(R.string.open_messenger_error),
                    Toast.LENGTH_LONG,
                ).show();
            }
        }

        findViewById<FrameLayout>(R.id.write_to_support).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")

                putExtra(Intent.EXTRA_EMAIL, arrayOf(STUDENT_EMAIL))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_message_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message_text))
            }

            startActivity(intent)
        }

        findViewById<FrameLayout>(R.id.user_aggrement).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(USER_AGGREMENT_URL))

            startActivity(intent)
        }
    }

    companion object {
        private const val PRACTICUM_URL = "https://practicum.yandex.ru/android-developer/?utm_source=yandex&utm_medium=cpc&utm_campaign=Yan_Sch_RF_Prog_andrDe_b2c_Gener_Regular_1_460&utm_content=sty_search%3As_none%3Acid_103839289%3Agid_5365194561%3Akw_программист+android%3Apid_49131471084%3Aaid_15590369389%3Acrid_0%3Arid_49131471084%3Ap_1%3Apty_premium%3Amty_syn%3Amkw_%3Adty_desktop%3Acgcid_17978229%3Arn_Симферополь%3Arid_146&utm_term=программист+android&yclid=5110100996876926975"
        private const val STUDENT_EMAIL = "tet4enko.dima@yandex.ru"
        private const val USER_AGGREMENT_URL = "https://yandex.ru/legal/practicum_offer/"
    }
}