package com.example.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sample.databinding.FragmentVoiceCallBinding

class VoiceCallFragment : Fragment() {

    private var _binding: FragmentVoiceCallBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val channelId = arguments?.getString("channelId")
        val channelName = arguments?.getString("channelName")
        binding.channelName.text = "$channelName"

        binding.callAction.setOnClickListener {
            // Заглушка для действия звонка
        }

        binding.endCallButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}