package com.example.coconut.oauth2

class UserInfo(
    private var lastName : String?,
    private var firstName : String?,
    private var imageLink : String?
) {
    override fun toString(): String {
        return "UserInfo(lastName='$lastName', firstName='$firstName', imageLink='$imageLink')"
    }
}