package com.example.hotelnest.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelnest.R

@Composable
fun FAQScreen(){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.faq),
                contentDescription = "FAQ Logo",
                Modifier.size(200.dp)
            )
        }

        item {
            Text(
                text = stringResource(id = R.string.Question1),
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                
            )
            Text(
                text = stringResource(id = R.string.Answer1),
                
            )
        }

        item {
            Text(
                text = stringResource(id = R.string.Question2),
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                
            )
            Text(
                text = stringResource(id = R.string.Answer2),
                
            )
        }

        item {
            Text(
                text = stringResource(id = R.string.Question3),
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                
            )
            Text(
                text = stringResource(id = R.string.Answer3),
                
            )
        }
    }


}