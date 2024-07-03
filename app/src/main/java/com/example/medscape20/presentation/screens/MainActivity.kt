package com.example.medscape20.presentation.screens

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.medscape20.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        //as we passed nav graph in nav host so there is no need for this

//        val fragmentManager = supportFragmentManager
//
//        // Create a Fragment transaction
//        val transaction = fragmentManager.beginTransaction()
//
//        // Create a new instance of your fragment
//        val loginFragment = SignupFragment()
//
//        // Add the fragment to the container (replace with your container view ID)
//        transaction.add(R.id.fragment_cont, loginFragment)
//
//        // Commit the transaction
//        transaction.commit()

    }
}