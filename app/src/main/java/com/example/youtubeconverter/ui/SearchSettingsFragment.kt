package com.example.youtubeconverter.ui

import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.youtubeconverter.R
import android.os.Bundle as Bundle1

class SearchSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle1?, rootKey: String?) {
        setPreferencesFromResource(R.xml.search_settings, rootKey)

        /*val languagePref: MultiSelectListPreference? = findPreference(
            getString(R.string.video_length_pref)
        )

        languagePref?.summaryProvider = Preference.SummaryProvider<MultiSelectListPreference> {
            val n = it.values.size
            if (n == 0) {
                getString(R.string.pref_language_not_set)
            } else {
                resources.getQuantityString(R.plurals.pref_language_summary, n, n)
            }
        }

         */
    }
}