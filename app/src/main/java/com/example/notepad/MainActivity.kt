package com.example.notepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    lateinit var username : EditText
    lateinit var loginpassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        username = findViewById(R.id.loginUser)
        loginpassword = findViewById(R.id.loginPW)

        loginBtn.setOnClickListener {
            checklogin()
        }
    }


    private fun checklogin()
    {

        val username = loginUser.text.toString().trim()
        val loginpassword = loginPW.text.toString().trim()
        if(username.isEmpty())
        {
            //requstFocus is for automatic directing to the EditText
            loginUser.requestFocus()
            return
        }
        if( !Patterns.EMAIL_ADDRESS.matcher(username).matches())
        {
            loginUser.error = "Valid Email required"
            loginUser.requestFocus()
            return
        }
        if(loginpassword.isEmpty() || loginpassword.length < 6)
        {
            loginPW.error="6 Char Password required"
            loginPW.requestFocus()
            return
        }

        loginUser(username,loginpassword)


    }

    private fun loginUser(email:String,password:String)
    {
        progressbar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){ Task ->

                if(Task.isSuccessful)
                {

                    val intent = Intent(this, NotePadActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        //IF YOU don't do this, when user press back button user will see the register again
                    }
                    startActivity(intent)
                }
                else
                {
                    Task.exception?.message?.let {
                        Toast.makeText(this,it, Toast.LENGTH_SHORT).show()
                    }

                }

                progressbar.visibility = View.GONE
            }
    }

    override fun onStart()
    {
        super.onStart()
        //This method is overriding the onStart method
        // Therefore if the user is already logged in, it will skip the login page
        mAuth.currentUser?.let {
            val intent = Intent(this,NotePadActivity::class.java)
            startActivity(intent)
        }
    }
}
