package com.example.sample.fragments

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample.R
import com.example.sample.adapters.MessageAdapter
import com.example.sample.databinding.FragmentChatDetailBinding
import com.example.sample.models.Message
import com.example.sample.models.Message.MessageStatus
import com.example.sample.models.Message.MessageType
import com.example.sample.viewmodels.ChatViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

class ChatDetailFragment : Fragment() {
    private var _binding: FragmentChatDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var viewModel: ChatViewModel

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imageUri ->
            try {
                val bitmap = getBitmapFromUri(imageUri)
                bitmap?.let {
                    val tempFile = createTempImageFile(requireContext())
                    saveBitmapToFile(it, tempFile)

                    val imageMessage = Message(
                        id = System.currentTimeMillis().toString(),
                        text = "", // Пустой текст для изображения
                        timestamp = Date().time,
                        senderId = "me",
                        senderName = "Я",
                        type = MessageType.IMAGE,
                        status = MessageStatus.SENT,
                        isOutgoing = true,
                        imageUrl = tempFile.absolutePath
                    )

                    viewModel.sendMessage(imageMessage)
                } ?: showError("Не удалось загрузить изображение")
            } catch (e: Exception) {
                showError("Ошибка: ${e.message ?: "неизвестная ошибка"}")
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            requireContext().contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun createTempImageFile(context: Context): File {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "IMG_${System.currentTimeMillis()}",
            ".jpg",
            storageDir
        ).apply {
            createNewFile()
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                out.flush()
            }
        } catch (e: IOException) {
            throw IOException("Не удалось сохранить изображение", e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        initRecyclerView()
        setupClickListeners()

        arguments?.getString("chatId")?.let { chatId ->
            viewModel.loadMessages(chatId)
        }
    }

    private fun initRecyclerView() {
        messageAdapter = MessageAdapter(
            onMessageDelete = { viewModel.deleteMessage(it) },
            onImageDownload = { downloadImage(it) },
            onImageClick = { showFullScreenImage(it) }
        )

        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
            setHasFixedSize(true)
        }

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.submitList(messages) {
                binding.messagesRecyclerView.scrollToPosition(messages.size - 1)
            }
        }
    }

    private fun setupClickListeners() {
        binding.sendButton.setOnClickListener {
            val text = binding.messageInput.text.toString().trim()
            if (text.isNotBlank()) {
                viewModel.sendMessage(text)
                binding.messageInput.text?.clear()
            }
        }

        binding.attachButton.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private fun downloadImage(url: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(getString(R.string.downloading_image))
                .setDescription(getString(R.string.downloading_image_description))

            (requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
                .enqueue(request)

            showMessage("Загрузка начата")
        } catch (e: Exception) {
            showError("Ошибка загрузки: ${e.message}")
        }
    }

    private fun showFullScreenImage(imageUrl: String) {
        // Реализуйте полноэкранный просмотр изображения
        showMessage("Просмотр изображения: $imageUrl")
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}