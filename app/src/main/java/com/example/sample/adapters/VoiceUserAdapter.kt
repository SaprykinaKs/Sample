package com.example.sample.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.R
import com.example.sample.models.VoiceUser

class VoiceUserAdapter(
    private var users: List<VoiceUser> = emptyList()
) : RecyclerView.Adapter<VoiceUserAdapter.VoiceUserViewHolder>() {

    class VoiceUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val userStatus: ImageView = itemView.findViewById(R.id.user_status)
        val userVolume: ProgressBar = itemView.findViewById(R.id.user_volume)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceUserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_voice_user, parent, false)
        return VoiceUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoiceUserViewHolder, position: Int) {
        val user = users[position]
        with(holder) {
            userName.text = user.name
            userStatus.setImageResource(
                if (user.isSpeaking) R.drawable.ic_speaking else R.drawable.ic_muted
            )

            userVolume.progress = user.volumeLevel
            userVolume.visibility = if (user.isSpeaking) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun getItemCount(): Int = users.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateUsers(newUsers: List<VoiceUser>) {
        users = newUsers
        notifyDataSetChanged()
    }
}