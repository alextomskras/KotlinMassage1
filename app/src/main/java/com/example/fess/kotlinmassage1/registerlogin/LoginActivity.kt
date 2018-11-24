package com.example.fess.kotlinmassage1.registerlogin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.fess.kotlinmassage1.R
import com.example.fess.kotlinmassage1.messages.LatestMessagesActivity
import com.example.fess.kotlinmassage1.service.MyFirebaseMessagingService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_login)
        login_button_login.setOnClickListener {
            performLogin()
//
        }

        back_to_register_login.setOnClickListener {
            finish()
        }


    }

    private fun refreshTokens(): String? {
        FirebaseInstanceId.getInstance().instanceId
                .addOnSuccessListener(this@LoginActivity) { instanceIdResult ->
                    val mToken = instanceIdResult.token
                    Log.d("printing  fcm token:", mToken)
                }


        val newToken = FirebaseInstanceId.getInstance().token
//        val newToken22 = FirebaseInstanceId.getInstance().instanceId.result.toString()
        Log.d("newTokenLogin", newToken)
//        Log.d("Token222", newToken22)
        Toast.makeText(this, "Please fill out $newToken", Toast.LENGTH_LONG).show()


        if (newToken != null) {
            MyFirebaseMessagingService().saveTokenToFirebaseDatabase(newToken)
        }
        return newToken
    }


    private fun performLogin() {
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

//        MyFirebaseMessagingService().onNewToken

        refreshTokens()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()
            return
        }


//        FirebaseInstanceId.getInstance().token


        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    Log.d("Login", "Successfully logged in: ${it.result!!.user.uid}")

                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

}