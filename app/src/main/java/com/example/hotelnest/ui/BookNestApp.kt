package com.example.hotelnest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hotelnest.data.CityData
import com.exapmle.hotelnest.ui.Checkout
import com.google.firebase.auth.FirebaseAuth


enum class BookNest{
    StartScreen, Where2Go, CityInformation, FAQScreen, CityInfo, BrowseHotels, BrowseRooms, Checkout
}
var canNavigateBack = false
val auth = FirebaseAuth.getInstance()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookNestApp(navController: NavHostController = rememberNavController(),
                viewModel: BookNestViewModel = viewModel()){
    viewModel.setUser(auth.currentUser)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val user by viewModel.user.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val cityDetails = {navController.navigate(BookNest.CityInfo.name)}
    val chosenHotel by viewModel.chosenHotel.collectAsState()
    val browseCities = { navController.navigate(BookNest.Where2Go.name) }
    val currentScreen = BookNest.valueOf(
        backStackEntry?.destination?.route ?: BookNest.StartScreen.name
    )
    canNavigateBack = navController.previousBackStackEntry != null

    if(user == null){
        LoginScreen(viewModel)
    }
    else{
        viewModel.readDatabase()
        viewModel.readUser()
        Scaffold(modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),topBar = {
            TopAppBar(title = {
                Row(modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .height(80.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentScreen.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Row(modifier = Modifier.clickable { viewModel.setDialogVisible(true)}) {
                        Icon(imageVector = Icons.Outlined.ExitToApp, contentDescription = "Logout")
                        Text(text = "Logout", fontSize = 17.sp)
                    }
                }
            }, navigationIcon = {
                if(canNavigateBack){
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            })
            if(showDialog){
                AlertCheck(
                    onYesButtonPressed = {
                        viewModel.setDialogVisible(false)
                        auth.signOut()
                        viewModel.clearData()

                    },
                    onNoButtonPressed = {
                        viewModel.setDialogVisible(false)
                    }
                )
            }
        }, bottomBar = { BottomBar(navController) }) {
            NavHost(navController = navController, startDestination = BookNest.StartScreen.name, modifier = Modifier.padding(it)) {
                composable(route = BookNest.StartScreen.name){
                    StartScreen(viewModel, browseCities,
                        cityDetails,search = { city ->
                            viewModel.setCity(city)
                            viewModel.downloadHotels()
                            navController.navigate(BookNest.BrowseHotels.name)
                        })
                }
                composable(route = BookNest.Where2Go.name){
                    BrowseCities(cityDetails,viewModel)
                }
                composable(route = BookNest.FAQScreen.name){
                    FAQScreen()
                }
                composable(route = BookNest.CityInfo.name){
                    CityInfo()
                }
                composable(route = BookNest.BrowseHotels.name){
                    BrowseHotels(viewModel, searchRoom = {it ->
                        viewModel.setHotel(it)
                        viewModel.downloadRooms()
                        navController.navigate(BookNest.BrowseRooms.name)
                    })
                }
                composable(route = BookNest.BrowseRooms.name){
                    SelectRooms(chosenHotel, viewModel,
                        finishCheckout = {
                            navController.navigate(BookNest.Checkout.name)
                        })
                }
                composable(route = BookNest.Checkout.name){
                    Checkout(viewModel)
                }
            }
        }
    }

}

@Composable
fun BottomBar(navController: NavHostController){
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(66, 165, 245, 255),
            Color(38, 198, 218, 255)
        ),
        startX = 0f,
        endX = 1000f
    )
    Row(modifier = Modifier
        .background(gradient)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .clickable {
                    navController.navigate(BookNest.StartScreen.name) {
                        popUpTo(0)
                    }
                }
                .padding(horizontal = 40.dp, vertical = 10.dp)
        ) {
            Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home", tint = Color.White)
            Text(text = "Home", fontSize = 10.sp, color = Color.White)
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .clickable {
                    navController.navigate(BookNest.Where2Go.name) {
                        popUpTo(BookNest.StartScreen.name)
                    }
                }
                .padding(horizontal = 40.dp, vertical = 10.dp)
        ) {
            Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = "Checkout", tint = Color.White)
            Text(text = "Where2Go", fontSize = 10.sp, color = Color.White)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .clickable {
                    navController.navigate(BookNest.FAQScreen.name) {
                        popUpTo(BookNest.StartScreen.name)
                    }
                }
                .padding(horizontal = 40.dp, vertical = 10.dp)
        ) {
            Icon(imageVector = Icons.Outlined.Info, contentDescription = "FAQ", tint = Color.White)
            Text(text = "FAQs", fontSize = 10.sp, color = Color.White)
        }
    }
}

object MyItem {
    lateinit var item: CityData
}

@Composable
fun AlertCheck(
    onYesButtonPressed: () -> Unit,
    onNoButtonPressed: () -> Unit

) {
    AlertDialog(
        onDismissRequest = { onNoButtonPressed() },
        confirmButton = {
            TextButton(
                onClick = {
                    onYesButtonPressed()
                }
            ) {
                Text(text = "Yes")
            }
        },
        title = {
            Text(text = "Logout?", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = "Are you sure you want to logout?")
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onNoButtonPressed()
                }
            ) {
                Text("No")
            }
        },
        containerColor = Color(225, 245, 254, 255)
    )
}

