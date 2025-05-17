package com.example.sample.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sample.R
import com.example.sample.models.Chat
import com.example.sample.models.Chat.ChatType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
    private var chats: List<Chat> = emptyList(),
    private val onItemClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatName: TextView = itemView.findViewById(R.id.chat_name)
        val lastMessage: TextView = itemView.findViewById(R.id.last_message)
        val chatIcon: ImageView = itemView.findViewById(R.id.chat_icon)
        val unreadCount: TextView = itemView.findViewById(R.id.unread_count)
        val chatTypeIndicator: View = itemView.findViewById(R.id.chat_type_indicator)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]

        holder.chatName.text = chat.name
        holder.lastMessage.text = chat.lastMessage
        holder.chatIcon.context.getString(R.string.chat_avatar_description, chat.name)

        Glide.with(holder.itemView.context)
            .load(chat.avatarUrl ?: getDefaultAvatar(chat))
            .circleCrop()
            .into(holder.chatIcon)

        if (chat.unreadCount > 0) {
            holder.unreadCount.visibility = View.VISIBLE
            holder.unreadCount.text = chat.unreadCount.toString()
        } else {
            holder.unreadCount.visibility = View.GONE
        }

        holder.chatTypeIndicator.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, getColorForChatType(chat.chatType))
        )

        holder.timestamp.text = formatTimestamp(chat.timestamp)

        holder.itemView.setOnClickListener { onItemClick(chat) }
    }

    override fun getItemCount(): Int = chats.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newChats: List<Chat>) {
        if (chats == newChats) return
        chats = newChats.sortedByDescending { it.timestamp }
        notifyDataSetChanged()
    }

    private fun getDefaultAvatar(chat: Chat): Int {
        return when(chat.chatType) {
            ChatType.PERSONAL -> R.drawable.ic_person
            ChatType.GROUP -> R.drawable.ic_group
            ChatType.CHANNEL -> R.drawable.ic_channel
        }
    }

    private fun getColorForChatType(type: ChatType): Int {
        return when(type) {
            ChatType.PERSONAL -> R.color.personal_chat_color
            ChatType.GROUP -> R.color.group_chat_color
            ChatType.CHANNEL -> R.color.channel_color
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }
}