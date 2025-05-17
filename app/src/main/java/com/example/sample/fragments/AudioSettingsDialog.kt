package com.example.sample.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.sample.R
import com.example.sample.databinding.DialogAudioSettingsBinding

class AudioSettingsDialog(
    private val currentInputVolume: Int,
    private val currentOutputVolume: Int,
    private val onSettingsSaved: (inputVolume: Int, outputVolume: Int) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogAudioSettingsBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAudioSettingsBinding.inflate(layoutInflater)

        binding.inputVolume.progress = currentInputVolume
        binding.outputVolume.progress = currentOutputVolume

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle(R.string.audio_settings_title)
            .setPositiveButton(R.string.save_button) { _, _ ->
                onSettingsSaved(
                    binding.inputVolume.progress,
                    binding.outputVolume.progress
                )
            }
            .setNegativeButton(R.string.cancel_button, null)
            .create()
    }
}