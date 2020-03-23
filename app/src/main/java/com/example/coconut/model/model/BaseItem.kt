package com.example.coconut.model.model

data class BaseItem(
    var id : String,
    val image : String,
    val name : String
) {
    override fun toString(): String {
        return "BaseItem(image='$image', name='$name')"
    }
}