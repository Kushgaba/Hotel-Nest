package com.exapmle.hotelnest.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hotelnest.R
import com.example.hotelnest.data.Checkout
import com.example.hotelnest.data.Rooms
import com.example.hotelnest.ui.BookNestViewModel
import java.lang.Math.abs
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun Checkout(viewModel: BookNestViewModel) {
    val database by viewModel.checkoutDownloaded.collectAsState()
    if (database.size > 0) {
        CheckoutAvailable(viewModel, database = database)
    } else {
        CheckoutUnavailable()
    }
}

@Composable
fun CheckoutAvailable(
    viewModel: BookNestViewModel,
    database: List<Checkout>
) {
    val serverUser by viewModel.serverUser.collectAsState()
    val userDetails by viewModel.userDetails.collectAsState()
    val days = userDetails?.let { calculateDaysBetweenDates(it.checkIn, it.checkOut) }?.toInt()
    val numberOfDays = if (days != 0) days else 1
    val chosenHotel by viewModel.chosenHotel.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(245, 245, 245, 255)),
    ) {

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .width(200.dp),
                        horizontalAlignment = Alignment.Start
                        ) {
                        Text(
                            fontSize = 28.sp,
                            text = chosenHotel.name,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Box(
                            Modifier.padding(vertical = 10.dp)
                        ) {
                            Row {
                                (1..5).forEach {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = Color.LightGray)
                                }
                                Text(text = "${chosenHotel.rating}", fontSize = 18.sp, modifier = Modifier.padding(horizontal = 10.dp), fontWeight = FontWeight.Bold)
                            }
                            Row {
                                (1..chosenHotel.rating.toInt()).forEach {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = "Star")
                                }
                            }
                        }
                    }
                    AsyncImage(
                        model = chosenHotel.photo,
                        contentDescription = chosenHotel.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(20.dp))
                        )
                }
                Divider(thickness = 1.dp, color = Color.LightGray)
                Text(
                    text = "Booking Details",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(66, 165, 245, 255)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "CHECK-IN")
                    Text(text = "CHECK-OUT")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    userDetails?.let { Text(text = it.checkIn, color = Color.Black, fontWeight = FontWeight.Bold) }
                    Card(modifier = Modifier.padding(bottom = 10.dp)) {
                        Text(text = days.toString() + if (days == 1) " Day" else " Days", Modifier.padding(5.dp))
                    }
                    userDetails?.let { Text(text = it.checkOut, color = Color.Black, fontWeight = FontWeight.Bold) }
                }
            }
        }

        items(database) {
            RoomCard(room = Rooms(
                name = it.name,
                photo = it.photo,
                price = it.price
            ), viewModel)
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(236, 236, 236, 255),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ){
                    Text(
                        text = "Price Breakup",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(66, 165, 245, 255)
                    )

                    PriceCard(
                        itemName = "Room Price",
                        itemValue = "Rs. " + NumberFormat.getNumberInstance(Locale.US).format(database.sumOf { it.price } * numberOfDays!!),
                        FontWeight.Light,
                    )
                    PriceCard(itemName = "GST", itemValue = "Rs. " + NumberFormat.getNumberInstance(Locale.US).format(database.sumOf { it.price } * 18 / 100 * numberOfDays), FontWeight.Light,
                    )
                    Divider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 5.dp))
                    PriceCard(
                        itemName = "To Pay",
                        itemValue = "Rs. " + NumberFormat.getNumberInstance(Locale.US).format((database.sumOf { it.price } + (database.sumOf { it.price } * 18 / 100)) * numberOfDays),
                        FontWeight.ExtraBold,
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(236, 236, 236, 255),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ){
                    Text(
                        text = "User Details",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(66, 165, 245, 255)
                    )

                    serverUser?.let { PriceCard(itemName = "Name", itemValue = it.fullName, fontWeight = FontWeight.Bold) }
                    serverUser?.let { PriceCard(itemName = "Email Id", itemValue = it.email, fontWeight = FontWeight.Normal) }
                    serverUser?.let { PriceCard(itemName = "Phone Number", itemValue = it.phoneNumber, fontWeight = FontWeight.Normal) }
                }
            }
        }
    }
}

@Composable
fun RoomCard(
    room: Rooms,
    viewModel: BookNestViewModel
) {

    Column {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
            AsyncImage(model = room.photo, contentDescription = room.name, modifier = Modifier
                .height(120.dp),
                contentScale = ContentScale.Crop
            )


//        Card (
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(80.dp),
//            shape = RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp),
//            colors = CardDefaults.cardColors(
//                Color(66, 165, 245, 255),
//                Color.White,
//
//                )
//        ) {
//        }
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = room.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Rs. ${room.price}")
        }

        Card(
            modifier = Modifier
                .fillMaxHeight()
                .width(100.dp)
                .align(Alignment.CenterVertically)

        ) {
            Text(
                text = "Remove",
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(11.dp)
                    .clickable {
                        viewModel.removeFromDatabase(room)
                    },
                textAlign = TextAlign.Center
            )
        }
    }
    }
}

@Composable
fun PriceCard(
    itemName: String,
    itemValue: String,
    fontWeight: FontWeight,
    fontColor: Color = Color.Black
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = itemName, fontWeight = fontWeight, color = fontColor)
        Text(text = itemValue, fontWeight = fontWeight, color = fontColor)
    }
}

@Composable
fun CheckoutUnavailable() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(painter = painterResource(id = R.drawable.nohotels), contentDescription = "Empty")
        Text(text = "Ohh! You did not select any rooms yet", fontWeight = FontWeight.ExtraBold,
            color =Color(38, 198, 218, 255),
            fontSize = 18.sp

        )

    }
}

fun calculateDaysBetweenDates(checkInDate: String, checkOutDate: String): Long {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val checkIn = dateFormat.parse(checkInDate)
    val checkOut = dateFormat.parse(checkOutDate)

    val diffInMillis = abs(checkOut.time - checkIn.time)
    val diffDays = diffInMillis / (1000 * 60 * 60 * 24)

    return diffDays
}