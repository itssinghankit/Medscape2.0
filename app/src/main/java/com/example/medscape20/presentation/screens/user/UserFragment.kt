package com.example.medscape20.presentation.screens.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.medscape20.R
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.medscape20.databinding.FragmentUserBinding

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        binding.bottomNav.menu.getItem(2).isEnabled = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting bottom navigation
        val navHostFragment=childFragmentManager.findFragmentById(R.id.user_fragment_holder) as NavHostFragment
        navController=navHostFragment.navController
        val bottomNavigationView=binding.bottomNav
        setupWithNavController(bottomNavigationView,navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}