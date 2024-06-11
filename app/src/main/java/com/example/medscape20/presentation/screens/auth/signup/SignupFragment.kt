package com.example.medscape20.presentation.screens.auth.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.medscape20.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import timber.log.Timber


class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private lateinit var auth: FirebaseAuth
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentSignupBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nxtBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Signed up success", Toast.LENGTH_SHORT).show()
                } else {
                    if(task.exception is FirebaseAuthUserCollisionException){
                        Toast.makeText(context, "Already registered", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}