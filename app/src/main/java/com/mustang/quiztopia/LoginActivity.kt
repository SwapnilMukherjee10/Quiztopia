package com.mustang.quiztopia

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mustang.quiztopia.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding

    val auth = FirebaseAuth.getInstance()

    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)

        val textOfGoogleButton = loginBinding.buttonGoogleSignIn.getChildAt(0) as TextView
        textOfGoogleButton.text = "Continue with Google"
        textOfGoogleButton.setTextColor(Color.BLACK)
        textOfGoogleButton.textSize = 18F

        // Register
        registerActivityForGoogleSignIn()

        loginBinding.buttonSignIn.setOnClickListener {

            val userEmail = loginBinding.editTextLoginEmail.text.toString()
            val userPassword = loginBinding.editTextLoginPassword.text.toString()

            if (userEmail == "" || userPassword == "") {

                Toast.makeText(applicationContext, "Enter email id and password to sign in", Toast.LENGTH_SHORT).show()

            } else {

                signInUser(userEmail, userPassword)

            }

        }

        loginBinding.buttonGoogleSignIn.setOnClickListener {

            signInGoogle()

        }

        loginBinding.textViewSignUp.setOnClickListener {

            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)

        }

        loginBinding.textViewForgotpassword.setOnClickListener {

            val intent =Intent(this@LoginActivity,ForgotPasswordActivity::class.java)
            startActivity(intent)

        }

    }

    fun signInUser(userEmail: String, userPassword: String) {

        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                Toast.makeText(applicationContext,"Welcome to Quiztopia",Toast.LENGTH_SHORT).show()

                val intent = Intent(this@LoginActivity,MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {

                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()

            }

        }



    }

    // When user has already logged in previously
    override fun onStart() {
        super.onStart()

        val user = auth.currentUser

        if (user != null) {

            Toast.makeText(applicationContext,"Welcome to Quiztopia",Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()

        }

    }

    private fun signInGoogle() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("205979530980-2tchr97pi3s2tbmmqfjfsak0g5qqhasq.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        signIn()

    }

    private fun signIn() {

        val signInIntent : Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)

    }

    private fun registerActivityForGoogleSignIn() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {result ->

                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == RESULT_OK && data != null) {

                    val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
                    firebaseSignInWithGoogle(task)

                }

            })

    }

    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {

        try {
            val account : GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext,"Welcome to Quiztopia",Toast.LENGTH_SHORT).show()

            val intent = Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
            firebaseGoogleAccount(account)
        } catch (e: ApiException) {
            Toast.makeText(applicationContext,e.localizedMessage,Toast.LENGTH_SHORT).show()
        }


    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount) {

        val authCredential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(authCredential).addOnCompleteListener {task ->

            if (task.isSuccessful) {

                //val user = auth.currentUser

            } else {

            }

        }

    }

}