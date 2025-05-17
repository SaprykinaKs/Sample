package com.example.sample.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.example.sample.models.VoiceChannel

class VoiceChannelAdapter(
    private val channels: List<VoiceChannel>,
    private val onItemClick: (VoiceChannel) -> Unit
) : RecyclerView.Adapter<VoiceChannelAdapter.VoiceChannelViewHolder>() {

    class VoiceChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val channelName: TextView = itemView.findViewById(R.id.channel_name)
        val usersOnline: TextView = itemView.findViewById(R.id.users_online)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_voice_channel, parent, false)
        return VoiceChannelViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VoiceChannelViewHolder, position: Int) {
        val channel = channels[position]
        holder.channelName.text = channel.name
        holder.usersOnline.text = holder.itemView.context.getString(
            R.string.online_members_count,
            channel.usersOnline
        )
        holder.itemView.setOnClickListener { onItemClick(channel) }
    }

    override fun getItemCount(): Int = channels.size
}