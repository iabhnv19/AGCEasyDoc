package com.example.agceasydoc.data

import com.example.agceasydoc.screens.SignatureAlignment


data class SignatureBlock(
    val text: String = "",
    val alignment: SignatureAlignment = SignatureAlignment.RIGHT
)
