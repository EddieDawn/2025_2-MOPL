package com.example.gostopmobileappprogramminglab.api

data class RandomUserResponse(
    val results: List<UserResult>
)

data class UserResult(
    val name: UserName,
    val location: UserLocation,
    val dob: UserDOB,
    val picture: UserPicture
)

data class UserName(
    val title: String,
    val first: String,
    val last: String
)

data class UserLocation(
    val country: String
)

data class UserDOB(
    val age: Int
)

data class UserPicture(
    val large: String
)
