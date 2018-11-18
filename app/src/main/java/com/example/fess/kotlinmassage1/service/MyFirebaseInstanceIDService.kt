package com.example.fess.kotlinmassage1.service

import android.content.Intent
import android.support.annotation.NonNull
import android.util.Log
import android.widget.Toast
import com.example.fess.kotlinmassage1.R.id.username_edittext_register
import com.example.fess.kotlinmassage1.messages.LatestMessagesActivity
import com.example.fess.kotlinmassage1.registerlogin.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.android.synthetic.main.activity_register.*


class MyFirebaseInstanceIDService : FirebaseMessagingService() {
    val TAG = "NEW_TOKEN"

    override fun onNewToken(s: String?) {
        Log.d("NEW_TOKEN", s)


        val newRegistrationToken = FirebaseInstanceId.getInstance().getInstanceId().toString()
        Log.d(TAG, "TOKEN_$newRegistrationToken")
        Toast.makeText(this, "Failed create Token: ${newRegistrationToken}", Toast.LENGTH_SHORT).show()

        if (FirebaseAuth.getInstance().currentUser != null)
            addTokenToFirestore(newRegistrationToken)
        saveTokenToFirebaseDatabase(newRegistrationToken)
    }

    fun saveTokenToFirebaseDatabase(newRegistrationToken: String) {
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
                    Log.d("Register", "Failed set Token value to firebasedatabase ${it.message}")
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
}
