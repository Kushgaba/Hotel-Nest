package com.example.hotelnest.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hotelnest.R
import com.example.hotelnest.data.Hotels
import com.example.hotelnest.data.Rooms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList

@Composable
fun BrowseHotels(viewModel: BookNestViewModel,
                 searchRoom: (Hotels) -> Unit){
    val items = viewModel.hotelsDownloaded.collectAsState()
    val city by viewModel.selectedCity.collectAsState()
    val checkIn by viewModel.checkIn.collectAsState()
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),) {
        items(items.value){
            HotelCard(it, viewModel, searchRoom)
        }
    }
}

@Composable
fun HotelCard(item: Hotels, viewModel: BookNestViewModel,
              searchRoom: (Hotels) -> Unit){
    val rooms = item.rooms
    var price = ""
    val priceColor: Color
    if(rooms?.isEmpty() == true){
        price = "Sold Out"
        priceColor = Color(217, 95, 136, 255)
    }
    else{
        price = sortRange(item)
        priceColor = colorResource(id = R.color.myBlue)
    }
    Card(shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clickable {
                searchRoom(item)
            }) {
        Column {
            AsyncImage(modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
                model = item.photo,
                contentDescription = "Hotel",
                contentScale = ContentScale.Crop)
            Row(modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.name, fontSize = 18.sp,
                    modifier = Modifier.padding(start = 10.dp),
                    color = Color.DarkGray,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold)
                Box(modifier = Modifier
                    .fillMaxHeight().width(120.dp)
                    .background(color = priceColor)){
                    Text(text = price,
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        maxLines = 1,
                        fontSize = 12.sp, color = Color.White,
                        textAlign = TextAlign.Center)
                }
            }
        }
    }
}

fun sortRange(hotel: Hotels): String{
    val roomsHashMap:HashMap<String,Rooms>? = hotel.rooms
    val roomsList: List<Rooms> = roomsHashMap?.values?.toList() ?: emptyList()
    val maxPriceRoom: Rooms? = roomsList.maxByOrNull { it.price }
    val minPriceRoom: Rooms? = roomsList.minByOrNull { it.price }
    val maxPrice: Int = maxPriceRoom?.price ?: 0
    val minPrice: Int = minPriceRoom?.price ?: 0
    if(maxPrice == minPrice) return "Rs.$maxPrice"
    else return "Rs.$minPrice to Rs.$maxPrice"
}