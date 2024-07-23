package com.example.medscape20.presentation.screens.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.medscape20.R
import com.example.medscape20.databinding.FragmentSplashBinding
import com.example.medscape20.presentation.screens.auth.login.LoginFragment.Companion.IS_COLLECTOR
import com.example.medscape20.util.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {

            // Check if user is signed in (non-null) and update UI accordingly.
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            var navigateToCollectorHome = false
            if (currentUser != null) {

                val isCollector: Flow<Boolean> = UserPreferences.getDataStore(requireContext()).data
                    .map { preferences ->
                        preferences[IS_COLLECTOR] ?: false
                    }
                if (isCollector.first()) navigateToCollectorHome = true

            }

            Handler(Looper.getMainLooper()).postDelayed({
                if (currentUser == null) {
                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                } else {
                    if (navigateToCollectorHome) {
                        findNavController().navigate(R.id.action_splashFragment_to_collectorHomeFragment)
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_userFragment)
                    }
                }

            }, 2000)


        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}