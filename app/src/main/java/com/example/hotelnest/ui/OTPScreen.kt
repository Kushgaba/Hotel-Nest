package com.example.hotelnest.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit


@Composable
fun LoginScreen(viewModel: BookNestViewModel){
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()

    val callbacks = object : OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            viewModel.saveUserData(fullName, email, phoneNumber)
            viewModel.setVerificationId("")
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("OTP Error", e.message.toString())
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            viewModel.resetTimer()
            viewModel.runTimer()
            viewModel.setVerificationId(verificationId)
            viewModel.setLoading(false)
            viewModel.resetProgress()
        }
    }
    val loading by viewModel.loading.collectAsState()
    val otp by viewModel.otp.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val verificationId by viewModel.verificationId.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(66, 165, 245, 255),
                        Color(38, 198, 218, 255)
                    ),
                    startY = 0f,
                    endY = 2000f // Adjust the end position based on your requirement
                )
            )
    ) {
        Column {
            Text(
                text = "Sign Up",
                color = Color.White,
                modifier = Modifier.padding(start = 20.dp, top = 60.dp),
                fontSize = 26.sp,

                )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White, //Card background color
                    contentColor = Color.DarkGray  //Card content color,e.g.text
                ),
                modifier = Modifier.padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 20.dp,
                    top = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    if (verificationId.isEmpty())
                        NumberScreen(
                            viewModel = viewModel,
                            callbacks = callbacks,
                        )
                    else
                        OtpScreen(
                            otp = otp,
                            viewModel = viewModel,
                            verificationId = verificationId,
                            callbacks = callbacks
                        )

                }
            }
        }
    }
    if (loading)
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(255, 255, 255, 190))
        ) {
            CircularProgressIndicator(progress = progress)
            Text(text = "Loading")
        }
}

@Composable
fun OtpScreen(otp: String, viewModel: BookNestViewModel,
              verificationId: String,
              callbacks: OnVerificationStateChangedCallbacks) {
    val context = LocalContext.current
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val ticks by viewModel.ticks.collectAsState()
    //val serverUser by viewModel.serverUser.collectAsState()

    val resentOtpText = if(ticks == 0L) "Resend OTP" else if (ticks > 10) "Resend OTP(00:$ticks)" else "Resend OTP(00:0$ticks)"
    Spacer(modifier = Modifier.padding(10.dp))

    Text(
        text = "VERIFY MOBILE NUMBER",
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        modifier = Modifier.fillMaxWidth(),
        
    )

    Text(
        text = "OTP has been sent to you on your\n" +
                "mobile number, please enter it below",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.LightGray,
        modifier = Modifier.fillMaxWidth(),
        
    )

    OtpTextBox(otp, viewModel)

    FilledTonalButton(
        onClick = {
            if (otp.isEmpty()) {
                Toast.makeText(context, "Please enter otp", Toast.LENGTH_SHORT).show()
            } else {
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                signInWithPhoneAuthCredential(credential, context, viewModel)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(208, 228, 243, 255)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(6.dp)) {
        Text(text = "Verify OTP")
    }

    Button(
        onClick = {
            if(ticks == 0L){
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber("+91$phoneNumber") // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(context as Activity) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
            else Toast.makeText(context, "Please wait for $ticks seconds", Toast.LENGTH_SHORT).show()

        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(if (ticks == 0L) Color(0, 101, 138, 255) else Color(208, 228, 243, 255)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(6.dp)) {
        Text(text = resentOtpText)
    }

    Button(
        onClick = {
            viewModel.clearData()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = Color(0, 101, 138, 255)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(6.dp),
        
    ) {
        Text(text = "Edit Phone Number")
    }
}

@Composable
fun OtpTextBox(otp: String, viewModel: BookNestViewModel) {
    BasicTextField(
        value = otp,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = {
            viewModel.setOtp(it)
        },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true
    ) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(6) {index ->
                val number = when {
                    index >= otp.length -> ""
                    else -> otp[index].toString()
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)) {

                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(50.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = 2.dp, // Border width
                                    color = Color.Gray, // Border color
                                    shape = RoundedCornerShape(8.dp) // Adjust corner radius
                                )
                        ) {
                            Text(
                                text = number,
                                fontSize = 32.sp,
                                modifier = Modifier
                                    .fillMaxSize(),
                                textAlign = TextAlign.Center,
                                
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NumberScreen(
    viewModel: BookNestViewModel,
    callbacks: OnVerificationStateChangedCallbacks
) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.email.collectAsState()
    val ticks by viewModel.ticks.collectAsState()
    val verificationId by viewModel.verificationId.collectAsState()

    val context = LocalContext.current

    OutlinedTextField(
        value = fullName,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onValueChange = { viewModel.setFullName(it) },
        label = { Text(text = "Full Name") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        leadingIcon = { Icon(Icons.Outlined.AccountCircle, "") },
        
    )

    OutlinedTextField(
        value = email,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        onValueChange = { viewModel.setEmail(it) },
        label = { Text(text = "Email") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        leadingIcon = { Icon(Icons.Outlined.Email, "") },
        
    )


    OutlinedTextField(
        value = phoneNumber,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { viewModel.setPhoneNumber(it) },
        label = { Text(text = "Mobile Number") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        leadingIcon = { Icon(Icons.Outlined.Phone, "") },
        
    )

    Spacer(modifier = Modifier.padding(top = 0.4.dp))

    Button(
        enabled = (ticks == 0L || ticks == 60L),
        onClick = {
            viewModel.progressStart()
            viewModel.setLoading(true)
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+91$phoneNumber") // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(context as Activity) // Activity for callback binding
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = Color(0, 101, 138, 255)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(text = "Send OTP")
    }
}

private fun signInWithPhoneAuthCredential(
    credential: PhoneAuthCredential,
    context: Context,
    hotelViewModel: BookNestViewModel
) {
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Toast.makeText(context, "Verification Successful", Toast.LENGTH_SHORT).show()
                val user = task.result?.user
                hotelViewModel.setUser(user)
                hotelViewModel.saveUserData()
            } else {
                // Sign in failed, display a message and update the UI
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                    Toast.makeText(context, "The OTP you have entered is invalid. Please try again", Toast.LENGTH_SHORT).show()

                }
                // Update UI
            }
        }
}