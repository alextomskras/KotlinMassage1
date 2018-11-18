package com.example.fess.kotlinmassage1.views

import android.util.Log

import com.example.fess.kotlinmassage1.R
import com.example.fess.kotlinmassage1.messages.LatestMessagesActivity
import com.example.fess.kotlinmassage1.models.ChatMessage
import com.example.fess.kotlinmassage1.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_image_message_row.view.*
import java.util.*

class LatestKartinkaMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>() {
    companion object {
        val TAG = "KartinkaMessageRow("
    }

    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val time1 = chatMessage.timestamp * 1000
//                formatedTime(time1)
        val sfd = java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val sfd1 = sfd.format(Date(time1)).toString()
        Log.d(TAG, "KARTINKA_latest_DATE!!!! $sfd1")
        val substChatMessage = chatMessage.text.substringBefore('.')
        if (substChatMessage == "https://firebasestorage") {
            viewHolder.itemView.textDate_message_time2.text = sfd1
            val targetImageView = viewHolder.itemView.kartinka_imageview_latest_message
            Picasso.get().load(chatMessage.text).into(targetImageView)
            Log.d(TAG, "Kartinka_Message_ $chatMessage")
        } else {
            viewHolder.itemView.textDate_message_time2.text = sfd1
            viewHolder.itemView.text_kartinka_textview_latest_message3.text = chatMessage.text
            Log.d(TAG, "Kartinka_TEXT_Message_ $chatMessage")
        }
        //      viewHolder.itemView.kartinka_imageview_latest_message = chatMessage.text


        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.username_kartinka_textview_latest_message.text = chatPartnerUser?.username

                val targetImageView = viewHolder.itemView.imageview_latest_message1
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_image_message_row
    }
}