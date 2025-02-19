package com.lowe.wanandroid.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lowe.wanandroid.BuildConfig
import com.lowe.wanandroid.R
import com.lowe.wanandroid.account.AccountManager
import com.lowe.wanandroid.constant.SettingConstants
import com.lowe.wanandroid.ui.about.AboutActivity
import com.lowe.wanandroid.ui.web.WebActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : PreferenceFragmentCompat() {

    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, null)
        findPreference<SwitchPreference>(SettingConstants.PREFERENCE_KEY_NORMAL_CATEGORY_DYNAMIC_COLORS)?.apply {
            if (DynamicColors.isDynamicColorAvailable().not()) {
                setDefaultValue(false)
                isEnabled = false
            }
        }
        findPreference<ListPreference>(SettingConstants.PREFERENCE_KEY_NORMAL_CATEGORY_DARK_MODE)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                AppCompatDelegate.setDefaultNightMode(SettingConstants.getNightMode(newValue.toString()))
                true
            }
        }
        findPreference<Preference>(SettingConstants.PREFERENCE_KEY_OTHER_CATEGORY_ABOUT)?.apply {
            summary = getString(
                R.string.app_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE.toString()
            )
            setOnPreferenceClickListener {
                startActivity(
                    Intent(
                        this@SettingFragment.requireContext(),
                        AboutActivity::class.java
                    )
                )
                true
            }
        }

        findPreference<Preference>(SettingConstants.PREFERENCE_KEY_OTHER_CATEGORY_GITHUB)?.apply {
            setOnPreferenceClickListener {
                WebActivity.loadUrl(requireContext(), summary.toString())
                true
            }
        }

        findPreference<Preference>(SettingConstants.PREFERENCE_KEY_OTHER_CATEGORY_LOGOUT)?.apply {
            isEnabled = AccountManager.isLogin()
        }
    }

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen): RecyclerView.Adapter<*> {
        return object : PreferenceGroupAdapter(preferenceScreen) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                super.onCreateViewHolder(parent, viewType)
                    .also(this@SettingFragment::initCustomViewPreference)

        }
    }

    private fun initCustomViewPreference(holder: PreferenceViewHolder) {
        when (holder.itemView.id) {
            R.id.preferenceLogoutLayout -> {
                holder.itemView.findViewById<View>(R.id.logout).setOnClickListener {
                    MaterialAlertDialogBuilder(it.context)
                        .setCancelable(true)
                        .setTitle("确认退出登录？")
                        .setPositiveButton("确认") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            this.activity?.finish()
                            settingViewModel.logout()
                        }
                        .setNegativeButton("取消") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                        }
                        .show()
                }
            }
        }
    }
}