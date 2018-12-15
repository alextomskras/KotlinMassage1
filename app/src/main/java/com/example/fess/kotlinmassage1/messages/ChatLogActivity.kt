package com.example.fess.kotlinmassage1.messages

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.fess.kotlinmassage1.R
import com.example.fess.kotlinmassage1.models.ChatMessage
import com.example.fess.kotlinmassage1.models.User
import com.example.fess.kotlinmassage1.views.ChatFromItem
import com.example.fess.kotlinmassage1.views.ChatToItem
import com.example.fess.kotlinmassage1.views.KartinkaFromItem
import com.example.fess.kotlinmassage1.views.KartinkaToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.util.*

//import kotlinx.android.synthetic.main.notification_template_lines_media.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    private val SENDER_ID = "44195834951"
    private val random = Random()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username


//    setupDummyData()
        listenForMessages()





        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            perfotmFCMSendMessages()
            performSendMessage()
        }

        image_send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to select images....")
            Toast.makeText(this, "Please select images", Toast.LENGTH_SHORT).show()

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedImageUri: Uri? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "Photo was selected")

            selectedImageUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)

            select_photoview_image_send.setImageBitmap(bitmap)

            image_send_button_chat_log.alpha = 0f

            uploadImageToFirebaseStorage()

            //  val bitmapDrawable = BitmapDrawable(bitmap)
            // select_photo_button_register.setBackgroundDrawable(bitmapDrawable)
            //Toast.makeText(this, "Please enter email/pw", Toast.LENGTH_SHORT).show()

        }
    }


    fun uploadImageToFirebaseStorage() {
        if (selectedImageUri == null) return


        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully upload image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "File location: $it")


                        // performSendMessage(it.toString())

                        //  saveUserToFirebaseDatabase(it.toString())

                        val kartinkaUrl = (it.toString())
                        performSendImage(kartinkaUrl)


                        Log.d(TAG, "TEST location: $kartinkaUrl")


                    }
                }
                .addOnFailureListener {
                    // do on fail
                    Log.d(TAG, "File location: ${it.message}")
                }

    }


    fun listenForMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        val substChatMessage = chatMessage.text.substringBefore('.')
                        val test1 = chatMessage.text
                        val time1 = chatMessage.timestamp * 1000
                        val sfd = java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                        val sfd1 = sfd.format(Date(time1)).toString()
                        if (substChatMessage == "https://firebasestorage") {
                            Log.d(TAG, "HTTPS!!!! $test1")
                            Log.d(TAG, "TIMERSSS!!!! $sfd1")

                            adapter.add(KartinkaFromItem(chatMessage.text, currentUser, sfd1))
                        } else {
                            adapter.add(ChatFromItem(chatMessage.text, currentUser, sfd1))
                        }

                    } else {
                        val substChatMessage = chatMessage.text.substringBefore('.')
                        val test1 = chatMessage.text
                        val time1 = chatMessage.timestamp * 1000
                        val sfd = java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                        val sfd1 = sfd.format(Date(time1)).toString()
                        if (substChatMessage == "https://firebasestorage") {
                            Log.d(TAG, "HTTPS!!!!_to_ $test1")
                            adapter.add(KartinkaToItem(chatMessage.text, toUser!!, sfd1))
                        } else {
                            adapter.add(ChatToItem(chatMessage.text, toUser!!, sfd1))
                        }

                    }
                }

                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }


    fun performSendMessage() {

        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()


        // val kartinka = (kartinkaUrl)
        // Log.d(TAG, "TEST1 location: $kartinka")

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

        //  val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${reference.key}")
                    edittext_chat_log.text.clear()
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }
        toReference.setValue(chatMessage)
        Log.d(TAG, "Saved to our chat message: ${toReference.key}")

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)


    }


    fun performSendImage(kartinkaUrl: String) {

        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()


        val kartinka = (kartinkaUrl)
        Log.d(TAG, "TEST1 location: $kartinka")

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

        //  val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, kartinka, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${reference.key}")
                    edittext_chat_log.text.clear()
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }
        toReference.setValue(chatMessage)
        Log.d(TAG, "Saved to our chat message: ${toReference.key}")

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)


    }


    fun perfotmFCMSendMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid
//        btn_upmessage.setOnClickListener {
        val fm = FirebaseMessaging.getInstance()

        val message = RemoteMessage.Builder(SENDER_ID + "@gcm.googleapis.com")
                .setMessageId(Integer.toString(random.nextInt(9999)))
                .addData("TEST1-- $fromId", "TEST1--  $toId")
//                    .addData(edt_key1.text.toString(), edt_value1.text.toString())
//                    .addData(edt_key2.text.toString(), edt_value2.text.toString())
                .build()

        if (!message.data.isEmpty()) {
            Log.e(TAG, "UpstreamData: " + message.data)
        }

        if (!message.messageId!!.isEmpty()) {
            Log.e(TAG, "UpstreamMessageId: " + message.messageId)
        }

        fm.send(message)
//        }
    }


}
