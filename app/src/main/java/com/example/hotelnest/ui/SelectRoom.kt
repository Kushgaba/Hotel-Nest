package com.example.hotelnest.ui

import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hotelnest.R
import com.example.hotelnest.data.Checkout
import com.example.hotelnest.data.DataSource
import com.example.hotelnest.data.Hotels
import com.example.hotelnest.data.Rooms
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRooms(hotel: Hotels, viewModel: BookNestViewModel,
                finishCheckout: (Hotels) -> Unit){
    viewModel.setUserDetails()
    val dialerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    val userDetails by viewModel.userDetails.collectAsState()
    val checkout by viewModel.checkoutDownloaded.collectAsState()
    val days = userDetails?.let { calculateDaysBetweenDates(it.checkIn, it.checkOut).toInt() } ?: 1
    val rating = hotel.rating.toInt()
    val amenityNos = hotel.amenities.split(",")
    val rooms by viewModel.roomsDownload.collectAsState()
    val numberOfDays = if( days!= 0) days else 1
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White), verticalArrangement = Arrangement.spacedBy(25.dp)) {
        item {
            AsyncImage(model = hotel.photo, contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(bottomEnd = 60.dp)),
                contentScale = ContentScale.Crop)
        }
        item {
            Text(modifier = Modifier.padding(start = 10.dp, end = 10.dp),text = hotel.name, color = colorResource(id = R.color.myBlue),
                fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        }
        item {
            Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
               repeat(rating){
                   Icon(Icons.Filled.Star, contentDescription = "",
                       modifier = Modifier.height(20.dp))
               }
                repeat(5-rating){
                    Icon(imageVector = Icons.Filled.StarBorder, contentDescription = "",
                        modifier = Modifier.height(20.dp))
                }
               Spacer(modifier = Modifier.width(10.dp))
               Text(text = hotel.rating.toString(), fontWeight = FontWeight.SemiBold)
            }
        }
        item {
            Text(modifier = Modifier.padding(start = 10.dp, end = 10.dp),text = "About the hotel", color = colorResource(id = R.color.myBlue),
                fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(modifier = Modifier.padding(top = 6.dp, start = 10.dp, end = 10.dp),text = hotel.desc, color = Color.Black,
                fontSize = 12.sp)
        }
        item {
            Divider(thickness = 0.5.dp, color = Color.LightGray)
        }
        item {
            Text(modifier = Modifier.padding(start = 10.dp, end = 10.dp),text = "Amenities Available", color = colorResource(id = R.color.myBlue),
                fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Row(modifier = Modifier
                .padding(start = 10.dp, top = 8.dp, end = 10.dp)
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround){
                val amenities = DataSource.loadAmenities(amenityNos)
                for(item in amenities){
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(painter = painterResource(id = item.img), contentDescription = "", modifier = Modifier.size(30.dp))
                        Text(text = item.name, fontSize = 10.sp)
                    }
                }
            }
            Divider(thickness = 0.5.dp, color = Color.LightGray)
        }
        item {
            Text(modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                text = "Property Rules & Information", color = colorResource(id = R.color.myBlue),
                fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
        item { 
            Text(modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                text = stringResource(id = R.string.rules), color = Color.Black,
                fontSize = 12.sp)
        }
        item {
            Text(modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                text = "Select Room(s)", color = colorResource(id = R.color.myBlue),
                fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
        items(rooms){
            RoomsCard(room = it, checkOut = checkout,viewModel)
        }
        item {
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Bottom
    ){

        if(checkout.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    finishCheckout(hotel)
                },
                colors = CardDefaults.cardColors(
                    Color(66, 165, 245, 255)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val day = if (days == 1) "Day" else "Days"
                    Text(text = "${checkout.size} Rooms | $numberOfDays $day | Rs. ${
                        NumberFormat.getNumberInstance(
                            Locale.US
                        ).format(checkout.sumOf { it.price } * numberOfDays!!)
                    }", color = Color.White, fontSize = 16.sp)
                    Card(
                    ) {
                        Text(text = "Checkout", fontSize = 16.sp, modifier = Modifier.padding(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RoomsCard(room: Rooms, checkOut: List<Checkout>,
              viewModel: BookNestViewModel) {
    var selected = false
    for(item in checkOut){
        if(item.name == room.name){
            selected = true
        }
    }
    Card(colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent)) {
        Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
            AsyncImage(modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp)),
                model = room.photo,
                contentDescription = "",
                contentScale = ContentScale.Crop)
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(text = room.name, fontWeight = FontWeight.Bold,
                        color = Color.Black, fontSize = 12.sp)
                    Text(text = "Rs.${room.price}", color = Color.Black,
                        fontSize = 12.sp)
                }
                Button(onClick = {if (!selected) {
                    viewModel.addToDatabase(room)
                } else {
                    viewModel.removeFromDatabase(room)
                }},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!selected) Color.White else Color(66, 165, 245, 50)),
                    modifier = Modifier
                        .border(
                            width = 3.dp,
                            color = colorResource(id = R.color.myBlue),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .height(40.dp)){
                    Text(text = if(selected)"SELECTED" else "SELECT", color = colorResource(id = R.color.myBlue))
                }
            }
        }
    }
}

fun launchDialer( launcher: androidx.activity.result.ActivityResultLauncher<android.content.Intent>, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = android.net.Uri.parse("tel:$phoneNumber")
    }
    launcher.launch(intent)
}

fun calculateDaysBetweenDates(checkInDate: String, checkOutDate: String): Long {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val checkIn = dateFormat.parse(checkInDate)
    val checkOut = dateFormat.parse(checkOutDate)

    val diffInMillis = Math.abs(checkOut.time - checkIn.time)
    val diffDays = diffInMillis / (1000 * 60 * 60 * 24)

    return diffDays
}
