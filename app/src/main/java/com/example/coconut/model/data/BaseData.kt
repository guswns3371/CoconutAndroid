package com.example.coconut.model.data

data class BaseData(
    var id : String,
    val image : String,
    val name : String
) {
    override fun toString(): String {
        return "BaseItem(image='$image', name='$name')"
    }
}