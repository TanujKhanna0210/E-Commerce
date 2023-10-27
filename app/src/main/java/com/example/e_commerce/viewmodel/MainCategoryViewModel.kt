package com.example.e_commerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce.data.Product
import com.example.e_commerce.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }

    fun fetchSpecialProducts() {
        if(!pagingInfo.isSpecialProductsPagingEnd) {
            viewModelScope.launch {
                _specialProducts.emit(Resource.Loading())
            }
            firestore.collection("Products")
                .whereEqualTo("category", "Special Products")
                .limit(pagingInfo.specialProductsPage * 5)
                .get()
                .addOnSuccessListener { result ->
                    val specialProductList = result.toObjects(Product::class.java)
                    pagingInfo.isSpecialProductsPagingEnd = specialProductList == pagingInfo.oldSpecialProducts
                    pagingInfo.oldSpecialProducts = specialProductList
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(specialProductList))
                    }
                    pagingInfo.specialProductsPage++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Failure(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestDeals() {
        if (!pagingInfo.isBestDealsPagingEnd) {
            viewModelScope.launch {
                _bestDealsProducts.emit(Resource.Loading())
            }
            firestore.collection("Products")
                .whereEqualTo("category", "Best Deals")
                .limit(pagingInfo.bestDealsPage * 5)
                .get()
                .addOnSuccessListener { result ->
                    val bestDealsProducts = result.toObjects(Product::class.java)
                    pagingInfo.isBestDealsPagingEnd = bestDealsProducts == pagingInfo.oldBestDeals
                    pagingInfo.oldBestDeals = bestDealsProducts
                    viewModelScope.launch {
                        _bestDealsProducts.emit(Resource.Success(bestDealsProducts))
                    }
                    pagingInfo.bestDealsPage++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _bestDealsProducts.emit(Resource.Failure(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isBestProductsPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").limit(pagingInfo.bestProductsPage * 10).get()
                .addOnSuccessListener { result ->
                    val bestProducts = result.toObjects(Product::class.java)
                    pagingInfo.isBestProductsPagingEnd = bestProducts == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = bestProducts
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProducts))
                    }
                    pagingInfo.bestProductsPage++
                }
                .addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Failure(it.message.toString()))
                    }
                }
        }
    }
}

internal data class PagingInfo(
    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isBestProductsPagingEnd: Boolean = false,

    var bestDealsPage: Long = 1,
    var oldBestDeals: List<Product> = emptyList(),
    var isBestDealsPagingEnd: Boolean = false,

    var specialProductsPage: Long = 1,
    var oldSpecialProducts: List<Product> = emptyList(),
    var isSpecialProductsPagingEnd: Boolean = false
)
