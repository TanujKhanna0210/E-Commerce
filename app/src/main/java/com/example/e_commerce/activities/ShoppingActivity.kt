package com.example.e_commerce.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.e_commerce.R
import com.example.e_commerce.databinding.ActivityShoppingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)
    }
}