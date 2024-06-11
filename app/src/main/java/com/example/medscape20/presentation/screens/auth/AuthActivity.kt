package com.example.medscape20.presentation.screens.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.medscape20.R
import com.example.medscape20.presentation.screens.auth.login.LoginFragment

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        val fragmentManager = supportFragmentManager

        // Create a Fragment transaction
        val transaction = fragmentManager.beginTransaction()

        // Create a new instance of your fragment
        val loginFragment = LoginFragment()

        // Add the fragment to the container (replace with your container view ID)
        transaction.add(R.id.fragment_cont, loginFragment)

        // Commit the transaction
        transaction.commit()

    }
}