package com.ait.aitforum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


    }

    fun loginClick(v: View) {
        // if user is validated
        // then open another activity
        // else give error message
        if (!isFormValid()) {
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            etEmail.text.toString(), etPassword.text.toString()
        ). addOnSuccessListener {
            Toast.makeText(this@LogInActivity, "Log In OK", Toast.LENGTH_LONG).show()
            // open forum activity
            startActivity(Intent(this@LogInActivity, ForumActivity::class.java))
        }.addOnFailureListener {
            Toast.makeText(
                this@LogInActivity, "Error: ${it.message}",
                Toast.LENGTH_LONG).show()
        }
    }

    fun registerClick(v: View) {
        // check if field is empty
        if (!isFormValid()) {
            return
        }
        // if not, we allow registration and extract name from email
        // update user profile
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            etEmail.text.toString(), etPassword.text.toString()
        ).addOnSuccessListener {
            val user = it.user

            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(userNameFromEmail(etEmail.text.toString()))
                    .build()
            )
            // if sccess, update profile and show message
            Toast.makeText(this@LogInActivity, "Success", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            // handle rror
            Toast.makeText(this@LogInActivity, "ErrorL ${it.message}", Toast.LENGTH_LONG).show()

        }
    }

    fun userNameFromEmail(email: String) = email.substringBefore("@")

    private fun isFormValid() : Boolean {
        return when {
            etEmail.text.isEmpty() -> {
                etEmail.error = "This field can not be empty"
                false
            }
            etPassword.text.isEmpty() -> {
                etPassword.error = "This field can not be empty"
                false
            }
            else -> true
        }
    }


}
