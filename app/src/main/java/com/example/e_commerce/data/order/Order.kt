package com.example.e_commerce.data.order

import com.example.e_commerce.data.Address
import com.example.e_commerce.data.CartProduct

data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProduct>,
    val address: Address
)
