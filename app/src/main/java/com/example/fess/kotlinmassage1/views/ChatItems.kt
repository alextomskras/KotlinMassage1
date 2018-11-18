package com.example.fess.kotlinmassage1.views

import android.util.Log
import com.example.fess.kotlinmassage1.R
import com.example.fess.kotlinmassage1.messages.ChatLogActivity.Companion.TAG
import com.example.fess.kotlinmassage1.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.kartinka_from_row.view.*
import kotlinx.android.synthetic.main.kartinka_to_row.view.*
import java.util.*


class ChatFromItem(val text: String, val user: User, val sfd1: String) : Item<ViewHolder>() {
    companion object {
        val TAG = "ChatItems"
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text
        viewHolder.itemView.textView_chat_from_message_time2.text = sfd1

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User, val sfd1: String) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        viewHolder.itemView.textView_chat_to_message_time2.text = sfd1

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}

class KartinkaFromItem(val text: String, val user: User, val sfd1: String) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "_Message $text")
//        setTimeMessage (viewHolder)
        setTimeMessage()
        //  viewHolder.itemView.textView_message_time.text = setTimeMessage()
        viewHolder.itemView.textView_message_time.text = sfd1
        var uri1 = text

        var targetImageView1 = viewHolder.itemView.kartinka_chat_from_row2
        Picasso.get().load(uri1).into(targetImageView1)

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row2
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.kartinka_from_row
    }
}

class KartinkaToItem(val text: String, val user: User, val sfd1: String) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "_Message_to_ $text")
//        setTimeMessage_to (viewHolder)
        setTimeMessage()
        viewHolder.itemView.textView_to_message_time.text = sfd1
        val uri1 = text
        val targetImageView1 = viewHolder.itemView.kartinka_chat_to_row2
        Picasso.get().load(uri1).into(targetImageView1)
        //       viewHolder.itemView.kartinka_chat_to_row2.imageview_chat_to_row = kartinka

        // load our user image into the star
        val uri = user.profileImageUrl
        Log.d(TAG, "_User_to_ $uri")
        val targetImageView = viewHolder.itemView.imageview_chat_to_row2
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.kartinka_to_row
    }
}


fun setTimeMessage(): String {
//val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
    //val dateFormat = LocalDateTime.now()
    val stamp = java.sql.Timestamp(System.currentTimeMillis())
    val date1 = Date(stamp.getTime())
    Log.d(TAG, "The time_ ${date1}")
    return date1.toString()
    // viewHolder.itemView.textView_message_time.text = date1.toString()
}


