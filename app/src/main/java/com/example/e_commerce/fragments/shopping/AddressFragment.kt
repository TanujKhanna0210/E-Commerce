package com.example.e_commerce.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.e_commerce.data.Address
import com.example.e_commerce.databinding.FragmentAddressBinding
import com.example.e_commerce.util.Resource
import com.example.e_commerce.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment: Fragment() {
    private lateinit var binding: FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()
    val args by navArgs<AddressFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }
                    is Resource.Failure -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = args.address
        if (address == null) {
            binding.btnDelete.visibility = View.GONE
        } else {
            binding.apply {
                etAddressTitle.setText(address.addressTitle)
                etFullName.setText(address.fullName)
                etStreet.setText(address.street)
                etCity.setText(address.city)
                etState.setText(address.state)
                etPhone.setText(address.phone)
            }
        }

        binding.apply {
            btnSave.setOnClickListener {
                val addressTitle = etAddressTitle.text.toString()
                val fullName = etFullName.text.toString()
                val street = etStreet.text.toString()
                val phone = etPhone.text.toString()
                val city = etCity.text.toString()
                val state = etState.text.toString()

                val address = Address(addressTitle, fullName, street, phone, city, state)

                viewModel.addAddress(address)
            }
        }
    }
}