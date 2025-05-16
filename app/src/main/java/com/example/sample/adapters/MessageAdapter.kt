package com.example.sample.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sample.R
import com.example.sample.models.Message
import com.example.sample.models.Message.MessageStatus
import com.example.sample.models.Message.MessageType
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val onMessageDelete: (Message) -> Unit,
    private val onImageDownload: (String) -> Unit,
    private val onImageClick: (String) -> Unit = {}
) : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    abstract class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(message: Message)

        protected fun formatTimestamp(timestamp: Long): String {
            return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        }

        protected fun showMessageMenu(
            view: View,
            message: Message,
            onCopy: () -> Unit,
            onDelete: () -> Unit,
            onDownload: (() -> Unit)? = null
        ) {
            PopupMenu(view.context, view).apply {
                menuInflater.inflate(R.menu.message_menu, menu)
                menu.findItem(R.id.menu_download)?.isVisible = onDownload != null
                menu.findItem(R.id.menu_copy)?.isVisible = message.text.isNotEmpty() == true
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_copy -> onCopy().let { true }
                        R.id.menu_delete -> onDelete().let { true }
                        R.id.menu_download -> onDownload?.invoke().let { true }
                        else -> false
                    }
                }
                show()
            }
        }

        protected fun copyToClipboard(context: Context, text: String) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboard?.let {
                it.setPrimaryClip(ClipData.newPlainText("message", text))
                Toast.makeText(context, "Скопировано", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class TextMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        private val senderName: TextView = itemView.findViewById(R.id.sender_name)
        private val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        private val statusIcon: ImageView = itemView.findViewById(R.id.status_icon)
        private val bubble: View = itemView.findViewById(R.id.message_bubble)

        override fun bind(message: Message) {
            messageText.text = message.text
            senderName.text = message.senderName
            timestamp.text = formatTimestamp(message.timestamp)

            bubble.setBackgroundColor(ContextCompat.getColor(
                itemView.context,
                if (message.isOutgoing) R.color.message_outgoing else R.color.message_incoming
            ))

            statusIcon.setImageResource(when (message.status) {
                MessageStatus.READ -> R.drawable.ic_read
                MessageStatus.DELIVERED -> R.drawable.ic_delivered
                MessageStatus.SENT -> R.drawable.ic_sent
            })

            statusIcon.visibility = if (message.isOutgoing) View.VISIBLE else View.GONE

            itemView.setOnLongClickListener { v ->
                showMessageMenu(
                    view = v,
                    message = message,
                    onCopy = { copyToClipboard(itemView.context, message.text) },
                    onDelete = { onMessageDelete(message) },
                    onDownload = null
                )
                true
            }
        }
    }

    inner class SystemMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.system_message_text)

        override fun bind(message: Message) {
            messageText.text = message.text
        }
    }

    inner class ImageMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        private val imageView: ImageView = itemView.findViewById(R.id.image_preview)
        private val downloadButton: ImageView = itemView.findViewById(R.id.download_button)
        private val senderName: TextView = itemView.findViewById(R.id.sender_name)
        private val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        private val statusIcon: ImageView = itemView.findViewById(R.id.status_icon)
        private val bubble: View = itemView.findViewById(R.id.message_bubble)

        override fun bind(message: Message) {
            messageText.text = message.text
            senderName.text = message.senderName
            timestamp.text = formatTimestamp(message.timestamp)

            bubble.setBackgroundColor(ContextCompat.getColor(
                itemView.context,
                if (message.isOutgoing) R.color.message_outgoing else R.color.message_incoming
            ))

            statusIcon.setImageResource(
                when (message.status) {
                    MessageStatus.READ -> R.drawable.ic_read
                    MessageStatus.DELIVERED -> R.drawable.ic_delivered
                    MessageStatus.SENT -> R.drawable.ic_sent
                }
            )

            statusIcon.visibility = if (message.isOutgoing) View.VISIBLE else View.GONE

            message.imageUrl?.let { url ->
                try {
                    Glide.with(itemView.context)
                        .load(File(url))
                        .placeholder(R.drawable.ic_placeholder_image)
                        .error(R.drawable.ic_error_image)
                        .into(imageView)
                } catch (e: Exception) {
                    Glide.with(itemView.context)
                        .load(R.drawable.ic_error_image)
                        .into(imageView)
                }

                imageView.setOnClickListener {
                    onImageClick(url)
                }
            }

            downloadButton.setOnClickListener {
                message.imageUrl?.let(onImageDownload)
            }

            itemView.setOnLongClickListener { v ->
                showMessageMenu(
                    view = v,
                    message = message,
                    onCopy = { copyToClipboard(itemView.context, message.text) },
                    onDelete = { onMessageDelete(message) },
                    onDownload = { message.imageUrl?.let(onImageDownload) }
                )
                true
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            MessageType.TEXT -> R.layout.item_message
            MessageType.IMAGE -> R.layout.item_image_message
            MessageType.SYSTEM -> R.layout.item_system_message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.item_message -> TextMessageViewHolder(inflater.inflate(R.layout.item_message, parent, false))
            R.layout.item_image_message -> ImageMessageViewHolder(inflater.inflate(R.layout.item_image_message, parent, false))
            R.layout.item_system_message -> SystemMessageViewHolder(inflater.inflate(R.layout.item_system_message, parent, false))
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}