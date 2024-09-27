package com.example.hotelnest.data

data class UserData(val fullName: String,
                    val phoneNumber: String,
                    val email: String)


data class UserDetails(
    val checkIn: String = "",
    val checkOut: String = "",
    val hotelName: String = "",
    val numberOfRooms: Int = 0,
)

data class Amenities(
    val img: Int,
    val name: String,
    val id: String
)

data class Hotels(
    val name: String = "",
    val photo: String = "",
    val desc: String = "",
    val rooms: HashMap<String, Rooms>? = null,
    val job: String = "",
    val amenities: String = "",
    val rating: Double = 0.0,
    val numberOfRooms: Int = 0
)

data class Checkout(
    val name: String = "",
    val photo: String = "",
    val price: Int = 0
)

data class Rooms(
    val name: String = "",
    val photo: String = "",
    val price: Int = 0
)
