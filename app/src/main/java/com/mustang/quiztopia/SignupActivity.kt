package com.mustang.quiztopia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mustang.quiztopia.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    lateinit var signupBinding: ActivitySignupBinding

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        val view = signupBinding.root
        setContentView(view)

        supportActionBar?.title = "Sign Up"

        signupBinding.buttonSignUp.setOnClickListener {

            val email = signupBinding.editTextSignupEmail.text.toString()
            val password = signupBinding.editTextSignupPassword.text.toString()

            if ( email == "" || password == "") {

                Toast.makeText(applicationContext, "Please enter your email id and password to sign up", Toast.LENGTH_SHORT).show()

            } else {

                signupWithFirebase(email, password)

            }

        }

    }

    fun signupWithFirebase(email: String, password: String) {

        signupBinding.progressBarSignup.visibility = View.VISIBLE
        signupBinding.buttonSignUp.isClickable = false

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task ->

            if (task.isSuccessful) {

                Toast.makeText(applicationContext,"Your account has been created",Toast.LENGTH_SHORT).show()
                finish()
                signupBinding.progressBarSignup.visibility = View.INVISIBLE
                signupBinding.buttonSignUp.isClickable = true

            } else {

                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()

            }

        }

    }

}