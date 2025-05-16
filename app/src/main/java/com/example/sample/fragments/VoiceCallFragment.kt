package com.example.sample.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample.R
import com.example.sample.adapters.VoiceUserAdapter
import com.example.sample.databinding.FragmentVoiceCallBinding
import com.example.sample.models.VoiceUser
import kotlinx.coroutines.*

class VoiceCallFragment : Fragment() {
    private var _binding: FragmentVoiceCallBinding? = null
    private val binding get() = _binding!!
    private var isConnected = false
    private var volumeJob: Job? = null
    private lateinit var audioManager: AudioManager
    private lateinit var prefs: SharedPreferences
    private lateinit var adapter: VoiceUserAdapter

    private val users = mutableListOf(
        VoiceUser("1", "Алексей", true, 75),
        VoiceUser("2", "Мария", false, 0),
        VoiceUser("3", "Иван", true, 45)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализируем prefs в onCreate
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceCallBinding.inflate(inflater, container, false)
        audioManager = requireContext().getSystemService(AudioManager::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channelName = arguments?.getString("channelName") ?: "Голосовой канал"
        binding.channelName.text = channelName

        loadAudioSettings() // prefs инициализирован
        setupRecyclerView()
        setupCallControls()
        startVolumeMonitoring()
        simulateUserActivity()
    }

    private fun loadAudioSettings() {
        val inputVol = prefs.getInt("input_volume", 50)
        val outputVol = prefs.getInt("output_volume", 50)
        applyAudioSettings(inputVol, outputVol)
    }

    private fun setupRecyclerView() {
        adapter = VoiceUserAdapter(users)
        binding.usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@VoiceCallFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupCallControls() {
        updateConnectionState()

        binding.callAction.setOnClickListener {
            isConnected = !isConnected
            updateConnectionState()
            if (isConnected) {
                startVolumeMonitoring()
            } else {
                stopVolumeMonitoring()
            }
        }

        binding.endCallButton.setOnClickListener {
            stopVolumeMonitoring()
            findNavController().navigateUp()
        }

        binding.settingsButton.setOnClickListener {
            showAudioSettings()
        }
    }

    private fun updateConnectionState() {
        binding.callAction.setImageResource(
            if (isConnected) R.drawable.ic_mic_on else R.drawable.ic_mic_off
        )
        binding.connectionStatus.text = if (isConnected) "Подключено" else "Отключено"
        binding.volumeIndicator.visibility = if (isConnected) View.VISIBLE else View.GONE
    }

    private fun startVolumeMonitoring() {
        volumeJob?.cancel()
        volumeJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && isConnected) {
                val volume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
                val level = (volume.toFloat() / maxVolume * 100).toInt()

                binding.volumeIndicator.progress = level
                delay(100)
            }
        }
    }

    private fun stopVolumeMonitoring() {
        volumeJob?.cancel()
        volumeJob = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun simulateUserActivity() {
        CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(3000)
                users.forEachIndexed { index, user ->
                    if (user.isSpeaking) {
                        users[index] = user.copy(volumeLevel = (10..100).random())
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showAudioSettings() {
        AudioSettingsDialog(
            currentInputVolume = prefs.getInt("input_volume", 50),
            currentOutputVolume = prefs.getInt("output_volume", 50)
        ) { inputVol, outputVol ->
            saveAudioSettings(inputVol, outputVol)
            applyAudioSettings(inputVol, outputVol)
        }.show(childFragmentManager, "audio_settings")
    }

    private fun saveAudioSettings(inputVolume: Int, outputVolume: Int) {
        prefs.edit {
            putInt("input_volume", inputVolume)
            putInt("output_volume", outputVolume)
        }
    }

    private fun applyAudioSettings(inputVolume: Int, outputVolume: Int) {
        audioManager.setStreamVolume(
            AudioManager.STREAM_VOICE_CALL,
            (inputVolume / 100f * audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)).toInt(),
            AudioManager.FLAG_SHOW_UI
        )
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (outputVolume / 100f * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).toInt(),
            AudioManager.FLAG_SHOW_UI
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopVolumeMonitoring()
        _binding = null
    }
}