package com.example.fess.kotlinmassage1.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import com.example.fess.kotlinmassage1.R
import com.example.fess.kotlinmassage1.registerlogin.LoginActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "FCM_Service"

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage!!.from)
        Log.d(TAG, "Notification Message Body: " + remoteMessage.notification!!.body!!)
        sendNotification(remoteMessage)
//        val intent = Intent(this@MyFirebaseMessagingService, LoginActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        intent.putExtra("message", remoteMessage.notification!!.body!!)
//        startActivity(intent)
    }


    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        val deviceToken = s
        Log.d("NEW_TOKEN", deviceToken)


        val newRegistrationToken = FirebaseInstanceId.getInstance().instanceId.toString()
        Log.d(TAG, "TOKEN_$newRegistrationToken")
        Toast.makeText(this, "Failed create Token: ${newRegistrationToken}", Toast.LENGTH_SHORT).show()

//        if (FirebaseAuth.getInstance().currentUser != null)
//            addTokenToFirestore(newRegistrationToken)
//            saveTokenToFirebaseDatabase (newRegistrationToken)
    }

    fun saveTokenToFirebaseDatabase(newRegistrationToken: String?) {

        if (newRegistrationToken == null) {
            throw NullPointerException("FCM token is null.")
            //        val uid = FirebaseAuth.getInstance().uid ?: ""
        }

        val ref = FirebaseDatabase.getInstance().getReference("/Tokens/$newRegistrationToken")

//        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)

        ref.setValue(newRegistrationToken)
                .addOnSuccessListener {
                    Log.d("Register_TOKEN", "Finally save Token to firebasedatabase")

//                    val intent = Intent(this, LatestMessagesActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(intent)

                }
                .addOnFailureListener {
                    Log.d("Register_TOKEN", "Failed set Token value to firebasedatabase ${newRegistrationToken}")
                }
    }


    companion object {
        fun addTokenToFirestore(newRegistrationToken: String?) {
            if (newRegistrationToken == null) throw NullPointerException("FCM token is null.")

//            FirestoreUtil.getFCMRegistrationTokens { tokens ->
//                if (tokens.contains(newRegistrationToken))
//                    return@getFCMRegistrationTokens
//
//                tokens.add(newRegistrationToken)
//                FirestoreUtil.setFCMRegistrationTokens(tokens)
//            }
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setContentText(remoteMessage.notification!!.body)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_fire_emoji)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }


    override fun onMessageSent(msgId: String?) {
        Log.e(TAG, "onMessageSent: " + msgId!!)
    }

    override fun onSendError(msgId: String?, e: Exception?) {
        Log.e(TAG, "onSendError: " + msgId!!)
        Log.e(TAG, "Exception: " + e!!)
    }


    private val SENDER_ID = "44195834951"
    private val random = Random()
}