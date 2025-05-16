package com.example.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample.R
import com.example.sample.databinding.FragmentChatBinding
import com.example.sample.adapters.ChatAdapter
import com.example.sample.models.Chat

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chats = listOf(
            Chat("1", "Общий чат", "Последнее сообщение", System.currentTimeMillis()),
            Chat("2", "Команда разработки", "Фиксим баги", System.currentTimeMillis()),
            Chat("3", "Дизайнеры", "Новый макет готов", System.currentTimeMillis())
        )

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.chatRecyclerView.adapter = ChatAdapter(chats) { chat ->
            findNavController().navigate(
                R.id.action_chat_to_detail,
//                bundleOf("chatId" to chat.id)
                Bundle().apply { putString("chatId", chat.id) }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}