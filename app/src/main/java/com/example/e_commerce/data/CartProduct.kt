package com.example.e_commerce.data

data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int ?= null,
    val selectedSize: String ?= null
) {
    constructor(): this(Product(), 1, null, null)
}
