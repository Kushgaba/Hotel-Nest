package com.example.hotelnest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hotelnest.R

@Composable
fun CityInfo(){
    val cityData = MyItem.item
    LazyColumn(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item {
            AsyncImage(model = cityData.image, contentDescription = cityData.name,
                       modifier = Modifier
                           .fillMaxWidth()
                           .height(200.dp)
                           .clip(RoundedCornerShape(bottomEnd = 30.dp)),
                       contentScale = ContentScale.Crop)
        }
        item {
            Text(text = cityData.name, textAlign = TextAlign.Left,
                color = colorResource(id = R.color.myBlue),
                fontSize = 28.sp, fontWeight = FontWeight.SemiBold,
                )
        }
        item {
            Text(text = cityData.desc, fontSize = 14.sp,
                color = Color.DarkGray,
                )
        }
    }
}