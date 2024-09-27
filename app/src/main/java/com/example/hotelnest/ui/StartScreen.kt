package com.example.hotelnest.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.hotelnest.data.CityData
import com.example.hotelnest.R
import java.util.Calendar
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(viewModel: BookNestViewModel, browseCities: () -> Unit,
                cityDetails: () -> Unit,
                search:(String) -> Unit){
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Agra", "Bengaluru", "Chennai")
    val checkIn by viewModel.checkIn.collectAsState()
    val checkOut by viewModel.checkOut.collectAsState()
    val cityData by viewModel.placesDownloaded.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val count by viewModel.count.collectAsState()
    Column(modifier = Modifier.padding(20.dp)) {
        var hintText by remember { mutableStateOf("Where you want to go?") }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedCity,
                onValueChange = { viewModel.setCity(it) },
                label = { Text(hintText, color = colorResource(id = R.color.myBlue)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Where?",
                        tint = Color(66, 165, 245, 255)
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        tint = colorResource(id = R.color.myBlue)
                    )
                },
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = Color.White,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            viewModel.setCity(item)
                            hintText = "Where you want to go?"
                            expanded = false
                        }
                    )
                }
            }
        }
        myDatePicker("Check in Date", checkIn) { viewModel.setCheckIn(it) }
        myDatePicker("Check out Date", checkOut) { viewModel.setCheckOut(it) }

        TextField(
            value = if (count == 0) "" else count.toString(),
            onValueChange = { },
            label = { Text("Number of Rooms", color = colorResource(id = R.color.myBlue)) },
            enabled = false,
            modifier = Modifier
                .clickable { viewModel.setBedFlag(true) }
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                containerColor = Color.White,
                disabledIndicatorColor = MaterialTheme.colorScheme.secondaryContainer

            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "date",
                    tint = Color(
                        66,
                        165,
                        245,
                        255
                    )
                )
            }
        )

        val gradient = Brush.horizontalGradient(
            colors = listOf(
                Color(66, 165, 245, 255),
                Color(38, 198, 218, 255)
            ),
            startX = 0f,
            endX = 1000f
        )
        val context = LocalContext.current
        Button(
            onClick = {
                if (checkIn.isEmpty() || checkOut.isEmpty() || selectedCity.isEmpty()) {
                    Toast.makeText(context, "Please fill the details", Toast.LENGTH_SHORT).show()
                } else search(selectedCity)
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        ) {
            Text(text = "SEARCH")
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = "BEST PLACES", color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = "VIEW ALL", color = colorResource(id = R.color.myBlue),
                fontWeight = FontWeight.Bold, fontSize = 14.sp,
                modifier = Modifier.clickable { browseCities() })
        }

        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            items(cityData) {
                RowCard(item = it, cityDetails)
            }
        }

        val bedFlag by viewModel.bedFlag.collectAsState()

        if (bedFlag) {
            AlertDialogWithCounter(
                onDismissRequest = { viewModel.setBedFlag(false) },
                hotelViewModel = viewModel
            )
        }
    }
}

@Composable
fun RowCard(item: CityData, cityDetails:() -> Unit) {
    Card(
        modifier = Modifier
            .size(150.dp)
            .clickable {
                MyItem.item = item
                cityDetails()
            }
    ) {
        AsyncImage(
            model = item.image,
            contentDescription = item.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp),
            contentScale = ContentScale.Crop
        )
        Text(text = item.name, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun myDatePicker(
    label: String,
    check: String,
    dateValue: (String) -> Unit,
) {
    val mContext = LocalContext.current
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    val mCalendar = Calendar.getInstance()

    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()


    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            dateValue("$mDayOfMonth/${mMonth + 1}/$mYear")
        }, mYear, mMonth, mDay
    )


    TextField(
        value = check,
        onValueChange = { dateValue(it)
        },
        label = { Text(label, color = colorResource(id = R.color.myBlue)) },
        enabled = false,
        modifier = Modifier
            .clickable { mDatePickerDialog.show() }
            .fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            disabledLabelColor = MaterialTheme.colorScheme.onBackground,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
            containerColor = Color.White,
            disabledIndicatorColor = MaterialTheme.colorScheme.secondaryContainer

        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.DateRange, contentDescription = "date", tint = Color(
                    66,
                    165,
                    245,
                    255
                )
            )
        }
    )
}

@Composable
fun AlertDialogWithCounter(
    onDismissRequest: () -> Unit,
    hotelViewModel: BookNestViewModel
) {

    val nCount by hotelViewModel.count.collectAsState()


    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(225, 245, 254, 255)
            )
        ) {
            Text(
                text = "Number of Rooms",
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                textAlign = TextAlign.Center,
                
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (nCount > 0) {
                            hotelViewModel.setCount(nCount - 1)
                        }
                    },
                    
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Decrease"
                    )
                }

                Text(
                    text = nCount.toString(),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 20.sp,
                    
                )

                IconButton(
                    onClick = {
                        hotelViewModel.setCount(nCount + 1)
                    },
                    
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Increase"
                    )
                }
            }

        }}
}
