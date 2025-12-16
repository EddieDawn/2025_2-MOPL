package com.example.termproject.api

data class RandomUserResponse(
    val results: List<UserResult>
)

data class UserResult(
    val name: Name,
    val dob: Dob,
    val location: Location,
    val picture: Picture
)

data class Name(
    val first: String,
    val last: String
)

data class Dob(
    val age: Int
)

data class Location(
    val country: String
)

data class Picture(
    val large: String
)
