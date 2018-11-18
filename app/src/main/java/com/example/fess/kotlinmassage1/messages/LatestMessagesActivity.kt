package com.example.fess.kotlinmassage1.messages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.fess.kotlinmassage1.R
import com.example.fess.kotlinmassage1.R.id.recyclerview_latest_messages
import com.example.fess.kotlinmassage1.models.ChatMessage
import com.example.fess.kotlinmassage1.models.User
import com.example.fess.kotlinmassage1.registerlogin.RegisterActivity
import com.example.fess.kotlinmassage1.views.LatestKartinkaMessageRow
import com.example.fess.kotlinmassage1.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
//import kotlinx.android.synthetic.main.activity_latest_messages.*
import java.util.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // set item click listener on your adapter
//        var imgMessages = refreshRecyclerViewMessages()
//        Log.d(TAG, "IF_onCreate_mess $imgMessages")
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "123")
            val intent = Intent(this, ChatLogActivity::class.java)

            val row = item as LatestKartinkaMessageRow
//            val row = item as LatestMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            // val row = item as LatestKartinkaMessageRow

            startActivity(intent)
        }

//    setupDummyRows()
        listenForLatestMessages()

        fetchCurrentUser()

        verifyUserIsLoggedIn()
    }


//    private fun checkROW() {
//      val fromId = FirebaseAuth.getInstance().uid
//        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
//        ref.addChildEventListener(object: ChildEventListener {
//            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
//                val substChatMessage = chatMessage.text.substringBefore('.')
//                val test1 = chatMessage.text
//                val time1 = chatMessage.timestamp*1000
//                val sfd = java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
//                val sfd1 = sfd.format( Date(time1)).toString()
//                    Log.d(TAG, "listenFor_mess $test1")
//                    Log.d(TAG, "latest_DATE!!!! $sfd1")
//    }

    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(imgMessages: String) {
        adapter.clear()
        latestMessagesMap.values.forEach {
            Log.d(TAG, "IF_listenFor_mess $imgMessages")
//            if (imgMessages == "picTures"){
//                adapter.add(LatestKartinkaMessageRow(it))
//
//            }else{
//                Log.d(TAG, "IF_listenFor_mess $imgMessages")
//                adapter.add(LatestMessageRow(it))
//
//            }
            adapter.add(LatestKartinkaMessageRow(it))
            //adapter.add(LatestMessageRow(it))
        }
    }

    fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                val substChatMessage = chatMessage.text.substringBefore('.')
                val test1 = chatMessage.text
                val time1 = chatMessage.timestamp * 1000
                val sfd = java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                val sfd1 = sfd.format(Date(time1)).toString()
                Log.d(TAG, "listenFor_mess $test1")
                Log.d(TAG, "latest_DATE!!!! $sfd1")
                if (substChatMessage == "https://firebasestorage") {
                    var imgMessages = "picTures"
                    Log.d(TAG, "pic_DATE!!!! $imgMessages")
                    latestMessagesMap[p0.key!!] = chatMessage
                    refreshRecyclerViewMessages(imgMessages)

                } else {
                    var imgMessages = "TEXT"
                    Log.d(TAG, "pic_DATE!!!! $imgMessages")
                    latestMessagesMap[p0.key!!] = chatMessage
                    refreshRecyclerViewMessages(imgMessages)

                }


            }


            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                val substChatMessage = chatMessage.text.substringBefore('.')
                val test1 = chatMessage.text
                val time1 = chatMessage.timestamp * 1000
//                formatedTime(time1)
                val sfd = java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                val sfd1 = sfd.format(Date(time1)).toString()
                Log.d(TAG, "latests_mess $test1")
                Log.d(TAG, "latest_DATE!!!! $sfd1")
                if (substChatMessage == "https://firebasestorage") {
                    var imgMessages = "picTures"
                    Log.d(TAG, "pic_DATE!!!! $imgMessages")
                    latestMessagesMap[p0.key!!] = chatMessage
                    refreshRecyclerViewMessages(imgMessages)
                } else {
                    var imgMessages = "TEXT"
                    Log.d(TAG, "pic_DATE!!!! $imgMessages")
                    latestMessagesMap[p0.key!!] = chatMessage
                    refreshRecyclerViewMessages(imgMessages)
                }

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    val adapter = GroupAdapter<ViewHolder>()


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.profileImageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}