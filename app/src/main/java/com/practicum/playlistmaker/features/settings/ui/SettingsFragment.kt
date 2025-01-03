package com.practicum.playlistmaker.features.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val viewModel by viewModel<SettingsViewModel>()

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.themeSwitch.isChecked = viewModel.settingsState.value?.isDarkTheme == true

        binding.themeSwitch.setOnCheckedChangeListener { switcher, isChecked ->
            viewModel.onDarkThemeSwitch(isChecked)
        }

        binding.shareApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, PRACTICUM_URL);
            intent.setType("text/plain");

            val result = runCatching {
                startActivity(intent)
            }

            result.onFailure {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.open_messenger_error),
                    Toast.LENGTH_LONG,
                ).show();
            }
        }

        binding.writeToSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")

                putExtra(Intent.EXTRA_EMAIL, arrayOf(STUDENT_EMAIL))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_message_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message_text))
            }

            startActivity(intent)
        }

        binding.userAggrement.setOnClickListener {
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