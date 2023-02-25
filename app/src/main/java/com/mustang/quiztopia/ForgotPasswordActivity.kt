package com.mustang.quiztopia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mustang.quiztopia.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var forgotBinding : ActivityForgotPasswordBinding

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = forgotBinding.root
        setContentView(view)

        supportActionBar?.title = "Reset Password"

        forgotBinding.buttonReset.setOnClickListener {

            val userEmail = forgotBinding.editTextForgotEmail.text.toString()

            if (userEmail == "") {

                Toast.makeText(applicationContext, "Please enter your email id", Toast.LENGTH_SHORT).show()

            } else {

                auth.sendPasswordResetEmail(userEmail).addOnCompleteListener {task ->

                    if (task.isSuccessful) {

                        Toast.makeText(applicationContext,"We sent a password reset link to your email address",Toast.LENGTH_LONG).show()
                        finish()

                    } else {

                        Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()

                    }

                }

            }



        }

    }
}