package com.example.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample.R
import com.example.sample.databinding.FragmentVoiceChannelBinding
import com.example.sample.adapters.VoiceChannelAdapter
import com.example.sample.data.MockData

class VoiceChannelFragment : Fragment() {

    private var _binding: FragmentVoiceChannelBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.voiceChannelRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.voiceChannelRecyclerView.adapter = VoiceChannelAdapter(MockData.voiceChannels) { channel ->
            findNavController().navigate(
                R.id.action_voice_to_call,
                Bundle().apply {
                    putString("channelId", channel.id)
                    putString("channelName", channel.name)
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}