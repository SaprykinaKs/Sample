package com.example.sample.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample.R
import com.example.sample.adapters.ChatAdapter
import com.example.sample.databinding.FragmentChatBinding
import com.example.sample.data.MockData
import com.example.sample.models.Chat

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private var allChats = MockData.demoChats
    private var filteredChats = MockData.demoChats

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

        chatAdapter = ChatAdapter(allChats) { chat ->
            findNavController().navigate(
                R.id.action_chat_to_detail,
                Bundle().apply { putString("chatId", chat.id) }
            )
        }

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.chatRecyclerView.adapter = chatAdapter

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterChats(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.filterPersonal.setOnClickListener { filterByType(Chat.ChatType.PERSONAL) }
        binding.filterGroup.setOnClickListener { filterByType(Chat.ChatType.GROUP) }
        binding.filterChannel.setOnClickListener { filterByType(Chat.ChatType.CHANNEL) }
        binding.filterAll.setOnClickListener { filterByType(null) }
    }

    private fun filterChats(query: String) {
        filteredChats = if (query.isEmpty()) {
            allChats
        } else {
            allChats.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.lastMessage.contains(query, ignoreCase = true)
            }
        }
        chatAdapter.updateData(filteredChats.sortedByDescending { it.timestamp })
    }

    private fun filterByType(type: Chat.ChatType?) {
        filteredChats = if (type == null) {
            allChats
        } else {
            allChats.filter { it.chatType == type }
        }
        chatAdapter.updateData(filteredChats.sortedByDescending { it.timestamp })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}