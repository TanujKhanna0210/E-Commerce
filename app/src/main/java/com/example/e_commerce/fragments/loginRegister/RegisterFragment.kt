package com.example.e_commerce.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.e_commerce.data.User
import com.example.e_commerce.databinding.FragmentRegisterBinding
import com.example.e_commerce.util.RegisterValidation
import com.example.e_commerce.util.Resource
import com.example.e_commerce.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


private const val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment: Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                val user = User(
                    etFirstNameRegister.text.toString().trim(),
                    etLastNameRegister.text.toString().trim(),
                    etEmailRegister.text.toString().trim()
                )
                val password = etPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when(it) {
                    is Resource.Loading -> {
                        binding.buttonRegisterRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.d("test", it.data.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }
                    is Resource.Failure -> {
                        Log.e(TAG, it.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect {validation ->
                if(validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.etEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if(validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.etPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }

            }
        }
    }
}