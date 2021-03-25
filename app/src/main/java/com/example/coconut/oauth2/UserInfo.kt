package com.example.coconut.oauth2

class UserInfo(
    var lastName : String?,
    var firstName : String?,
    var imageLink : String?,
    var email : String?
) {
    override fun toString(): String {
        return "UserInfo(lastName=$lastName, firstName=$firstName, imageLink=$imageLink, email=$email)"
    }
}