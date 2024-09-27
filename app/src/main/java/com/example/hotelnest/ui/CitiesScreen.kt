package com.example.hotelnest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hotelnest.data.CityData

@Composable
fun BrowseCities(cityDetails:() -> Unit, viewModel: BookNestViewModel){
    val items by viewModel.placesDownloaded.collectAsState()
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(items){
            CityCard(it, cityDetails)
        }
    }
}

@Composable
fun CityCard(it: CityData, cityDetails: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(220.dp).clickable { MyItem.item = it
                                    cityDetails()}
        .background(color = Color.LightGray, RoundedCornerShape(10.dp))) {
        Column{
            AsyncImage(model = it.image, contentDescription = it.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop)
            Column(modifier = Modifier.padding(start = 20.dp)
                .fillMaxWidth()
                .height(40.dp), verticalArrangement = Arrangement.Center){
                Text(text = it.name, color = Color.DarkGray,
                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
