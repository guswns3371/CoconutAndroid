package com.example.coconut.model.data

data class RegisterValidData(
    var idOk :Boolean,
    var nameOk : Boolean,
    var passwordOk : Boolean,
    var passwordConOk : Boolean
){
    override fun toString(): String {
        return "RegisterVaild(idOk=$idOk, nameOk=$nameOk, passwordOk=$passwordOk, passwordConOk=$passwordConOk)"
    }
}