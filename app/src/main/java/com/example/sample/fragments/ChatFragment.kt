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
import com.example.sample.models.Chat
import com.example.sample.models.Chat.ChatType

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private var allChats: List<Chat> = emptyList() // Полный список чатов
    private var filteredChats: List<Chat> = emptyList() // Отфильтрованный список

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
            Chat("1", "Общий чат", "Последнее сообщение", System.currentTimeMillis(),
                3, ChatType.GROUP, "https://example.com/group1.jpg"),
            Chat("2", "Команда разработки", "Фиксим баги", System.currentTimeMillis() - 10000,
                0, ChatType.GROUP, "https://example.com/dev.jpg"),
            Chat("3", "Алексей Климов", "Привет!", System.currentTimeMillis() - 3600000,
                1, ChatType.PERSONAL, "https://example.com/user1.jpg"),
            Chat("4", "Новости компании", "Новый релиз!", System.currentTimeMillis() - 86400000,
                5, ChatType.CHANNEL, "https://example.com/news.jpg")
        )
        allChats = chats // Инициализируем основной список
        filteredChats = allChats.toList() // Создаем копию

        chatAdapter = ChatAdapter(chats) { chat ->
            findNavController().navigate(
                R.id.action_chat_to_detail,
                Bundle().apply { putString("chatId", chat.id) }
            )
        }

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.chatRecyclerView.adapter = chatAdapter
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        // Настройка поиска
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterChats(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Настройка фильтров по типам чатов
        binding.filterPersonal.setOnClickListener { filterByType(ChatType.PERSONAL) }
        binding.filterGroup.setOnClickListener { filterByType(ChatType.GROUP) }
        binding.filterChannel.setOnClickListener { filterByType(ChatType.CHANNEL) }
        binding.filterAll.setOnClickListener { filterByType(null) }
    }

    private fun filterChats(query: String) {
        val filtered = if (query.isEmpty()) {
            allChats // Если поисковый запрос пуст, показываем все чаты
        } else {
            allChats.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.lastMessage.contains(query, ignoreCase = true)
            }
        }
        filteredChats = filtered
        chatAdapter.updateData(filteredChats.sortedByDescending { it.timestamp })
    }

    private fun filterByType(type: ChatType?) {
        val filtered = if (type == null) {
            allChats
        } else {
            allChats.filter { it.chatType == type }
        }
        filteredChats = filtered
        chatAdapter.updateData(filtered.sortedByDescending { it.timestamp })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}