package com.example.hotelnest.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelnest.data.Checkout
import com.example.hotelnest.data.CityData
import com.example.hotelnest.data.Hotels
import com.example.hotelnest.data.Rooms
import com.example.hotelnest.data.UserData
import com.example.hotelnest.data.UserDetails
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BookNestViewModel: ViewModel(){

    private lateinit var timerJob: Job
    private lateinit var progressJob: Job

    private val _serverUser = MutableStateFlow<UserData?>(null)
    val serverUser: MutableStateFlow<UserData?> get() = _serverUser

    private val _userDetails = MutableStateFlow<UserDetails?>(null)
    val userDetails: MutableStateFlow<UserDetails?> get() = _userDetails

    private val _placesDownloaded = MutableStateFlow<List<CityData>>(mutableListOf())
    val placesDownloaded: MutableStateFlow<List<CityData>> get() = _placesDownloaded

    private val _loading = MutableStateFlow(false)
    val loading: MutableStateFlow<Boolean> get() = _loading
    fun setLoading(loading: Boolean){
        _loading.value = loading
    }

    private val _selectedCity = MutableStateFlow("")
    val selectedCity: MutableStateFlow<String> get() = _selectedCity
    fun setCity(city: String) {
        _selectedCity.value = city
    }

    private val _chosenHotel = MutableStateFlow(Hotels())
    val chosenHotel: MutableStateFlow<Hotels> get() = _chosenHotel

    fun setHotel(hotel: Hotels) {
        _chosenHotel.value = hotel
    }

    private val _roomsDownloaded = MutableStateFlow<List<Rooms>>(mutableListOf())
    val roomsDownload: MutableStateFlow<List<Rooms>> get() = _roomsDownloaded

    fun addRooms(rooms: Rooms) {
        val currentRooms = _roomsDownloaded.value.toMutableList()
        currentRooms.add(rooms)
        _roomsDownloaded.value = currentRooms.toList()
    }

    private val _checkoutDownloaded = MutableStateFlow<List<Checkout>>(mutableListOf())
    val checkoutDownloaded: MutableStateFlow<List<Checkout>> get() = _checkoutDownloaded

    fun addCheckout(checkout: Checkout) {
        val currentCheckout = _checkoutDownloaded.value.toMutableList()
        currentCheckout.add(checkout)
        _checkoutDownloaded.value = currentCheckout.toList()
    }

    private val _hotelsDownloaded = MutableStateFlow<List<Hotels>>(mutableListOf())
    val hotelsDownloaded: MutableStateFlow<List<Hotels>> get() = _hotelsDownloaded

    fun addHotel(hotels: Hotels, numberOfRooms: Int) {
        val newHotels = hotels.copy(numberOfRooms = numberOfRooms)
        val currentHotels = _hotelsDownloaded.value.toMutableList()
        currentHotels.add(newHotels)
        _hotelsDownloaded.value = currentHotels.toList()
    }

    private val _progress = MutableStateFlow(0f)
    val progress: MutableStateFlow<Float> get() = _progress
    fun resetProgress(){_progress.value = 0f}

    private val _showDialog = MutableStateFlow(false)
    val showDialog: MutableStateFlow<Boolean> get() = _showDialog
    fun setDialogVisible(showDialog: Boolean){
        _showDialog.value = showDialog
    }

    private val _ticks = MutableStateFlow(60L)
    val ticks: MutableStateFlow<Long> get() = _ticks

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user : MutableStateFlow<FirebaseUser?> get() = _user
    fun setUser(user: FirebaseUser?){
        _user.value = user
    }

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: MutableStateFlow<String> get() = _phoneNumber
    fun setPhoneNumber(phoneNumber: String){
        _phoneNumber.value = phoneNumber
    }

    private val _fullName = MutableStateFlow("")
    val fullName: MutableStateFlow<String> get() = _fullName
    fun setFullName(fullName: String){
        _fullName.value = fullName
    }

    private val _email = MutableStateFlow("")
    val email: MutableStateFlow<String> get() = _email
    fun setEmail(email: String){
        _email.value = email
    }

    private val _otp = MutableStateFlow("")
    val otp: MutableStateFlow<String> get() = _otp
    fun setOtp(otp: String){
        _otp.value = otp
    }

    private val _verificationId = MutableStateFlow("")
    val verificationId: MutableStateFlow<String> get() = _verificationId
    fun setVerificationId(verificationId: String){
        _verificationId.value = verificationId
    }


    private lateinit var internetJob: Job
    private val _checkIn = MutableStateFlow("")
    val checkIn : MutableStateFlow<String> get() = _checkIn
    fun setCheckIn(checkIn: String){
        _checkIn.value = checkIn
    }

    private val _checkOut = MutableStateFlow("")
    val checkOut : MutableStateFlow<String> get() = _checkOut
    fun setCheckOut(checkOut: String){
        _checkOut.value = checkOut
    }

    private val _count = MutableStateFlow(0)
    val count: MutableStateFlow<Int> get() = _count
    fun setCount(count: Int){
        _count.value = count
    }

    private val _options = MutableStateFlow<List<String>>(mutableListOf())
    val options: MutableStateFlow<List<String>> get() = _options

    private val database = Firebase.database("https://hotel-booker-d04e1.firebaseio.com/")
    private val myRef = database.getReference("users/${auth.currentUser?.uid}")

    private val _bedFlag = MutableStateFlow(false)
    val bedFlag: MutableStateFlow<Boolean> get() = _bedFlag
    fun setBedFlag(bedFlag: Boolean) {
        _bedFlag.value = bedFlag
    }

    fun clearData(){
        _user.value = null
        _phoneNumber.value = ""
        _email.value = ""
        _fullName.value = ""
        _otp.value = ""
        _verificationId.value = ""
    }

    fun progressStart(){
        progressJob = viewModelScope.launch {
            while (_progress.value<1.0f){
                delay(20)
                _progress.value += 0.01f
            }
        }
    }
    fun runTimer() {
        timerJob = viewModelScope.launch {
            while (_ticks.value > 0) {
                delay(1000)
                _ticks.value -= 1
            }
        }
    }

    fun resetTimer() {
        try {
            timerJob.cancel()
        } catch (_:Exception) {}
        finally {
            _ticks.value = 60
        }
    }

    fun saveUserData(fullName: String, email: String, phoneNumber: String){
        val userData = UserData(fullName, email, phoneNumber)
        myRef.push().setValue(userData)
    }

    fun addPlace(place: CityData){
        val currentPlaces = _placesDownloaded.value.toMutableList()
        currentPlaces.add(place)
        _placesDownloaded.value = currentPlaces.toList()
    }

    fun addToDatabase(room: Rooms) {
        val myRef = database.getReference("Users/${user.value!!.uid}/cart")
        myRef.push().setValue(room)
    }

    fun removeFromDatabase(
        room: Rooms
    ) {
        val myRef = database.getReference("Users/${user.value!!.uid}/cart")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (childSnapshot in dataSnapshot.children) {

                    var itemRemoved = false
                    val item = childSnapshot.getValue(Checkout::class.java)
                    Log.d("vishnufirebase", item.toString())

                    item?.let {
                        if (room.name == it.name && room.price == it.price) {
                            childSnapshot.ref.removeValue()
                            itemRemoved = true
                        }
                    }
                    if (itemRemoved) break // Exit the loop if item has been removed
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    fun readDatabase() {
        val myRef = database.getReference("Users/${user.value!!.uid}/cart")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _checkoutDownloaded.value = emptyList()
                for (childSnapshot in dataSnapshot.children) {
                    val item = childSnapshot.getValue(Checkout::class.java)
                    if (item != null) {
                        addCheckout(item)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    private fun getDataFromFirebase(){
        val myRef = database.getReference("places")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _placesDownloaded.value = emptyList()
                for (placeSnapshot in dataSnapshot.children) {
                    val place = placeSnapshot.getValue(CityData::class.java)
                    place?.let { addPlace(it) }
                }
                options.value = placesDownloaded.value.map { it.name }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun saveUserData() {
        val users = database.getReference("Users/${user.value!!.uid}/userdata/")
        Log.e("rufus",users.toString())
        val userData = UserData(
            fullName = fullName.value,
            phoneNumber = phoneNumber.value,
            email = email.value
        )
        users.setValue(userData)
    }

    fun readUser() {
        val userRef = database.getReference("Users/${user.value!!.uid}/")

        val userDataRef = userRef.child("userdata")
        val userDetailsRef = userRef.child("userdetails")

        userDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.child("fullName").getValue(String::class.java) ?: ""
                val email = dataSnapshot.child("email").getValue(String::class.java) ?: ""
                val phone = dataSnapshot.child("phoneNumber").getValue(String::class.java) ?: ""

                _serverUser.value = UserData(
                    fullName = name,
                    email = email,
                    phoneNumber = phone
                )
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })

        userDetailsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userDetailsData = dataSnapshot.getValue(UserDetails::class.java)
                if (userDetailsData != null) {
                    _userDetails.value = userDetailsData
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    fun downloadHotels() {
        val TAG = "Kush_Firebase"
        val hotels = database.getReference("places/${selectedCity.value}/Hotels")
        hotels.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _hotelsDownloaded.value = emptyList()
                for (childSnapshot in dataSnapshot.children) {
                    val roomsRef = childSnapshot.child("Rooms")
                    val numberOfRooms = roomsRef.childrenCount.toInt()
                    Log.d(TAG, "Number of Rooms are: $numberOfRooms")
                    val itemFromFirebase = childSnapshot.getValue(Hotels::class.java)
                    itemFromFirebase?.let {
                        /*if (numberOfRooms >= count.value) {*/
                        addHotel(it, numberOfRooms)
                        /*}*/
                    }

                }
                options.value = hotelsDownloaded.value.map { it.name }
                Log.d(TAG, "Value is: $hotelsDownloaded")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    fun setUserDetails() {
        val data = UserDetails(
            checkIn = checkIn.value,
            checkOut = checkOut.value,
            hotelName = chosenHotel.value.name,
            numberOfRooms = checkoutDownloaded.value.size,
        )
        val userdetails = database.getReference("Users/${user.value!!.uid}/userdetails/")
        Log.d("rufus", userdetails.toString())
        userdetails.setValue(data)
    }

    fun downloadRooms() {
        val TAG = "Vishnu_Firebase"
        val rooms = database.getReference("places/${selectedCity.value}/Hotels/${chosenHotel.value.name.substringBefore(" ")}/rooms")
        rooms.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _roomsDownloaded.value = emptyList()
                for (childSnapshot in dataSnapshot.children) {
                    val itemFromFirebase = childSnapshot.getValue(Rooms::class.java)
                    itemFromFirebase?.let {
                        addRooms(it)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    init {
        getDataFromFirebase()
    }
}