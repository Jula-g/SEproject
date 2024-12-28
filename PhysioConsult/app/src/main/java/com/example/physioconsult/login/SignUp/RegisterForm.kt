package com.example.physioconsult.login.SignUp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.physioconsult.Main.MainActivity

import com.example.physioconsult.R
import com.example.physioconsult.login.LogIn.LoginActivity
import com.example.physioconsult.ui.theme.PhysioConsultTheme
import com.google.firebase.auth.FirebaseAuth


/**
 * Composable function that displays a registration form.
 * Users can input their name, surname, email, password, and repeat password.
 * Upon successful registration, the user is redirected to the main activity.
 * @see RegisterCredentials
 * @see checkCredentials
 */
@Composable
fun RegisterForm() {
    var credentials by remember { mutableStateOf(RegisterCredentials()) }
    val context = LocalContext.current

    val annotatedText = buildAnnotatedString {
        append("Already have an account? ")
        withStyle(style = SpanStyle(color = Color.Blue)) { append("Sign In") }
    }

    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            val image: Painter =
                painterResource(id = R.drawable.anatomy_901797)
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 20.dp)
            )
            Text(text = "Create an account", style = TextStyle(fontSize = 30.sp), modifier = Modifier.padding(bottom = 5.dp))
            Text(text = "Register and start your tests", style = TextStyle(fontSize = 15.sp, color = Color.Gray), modifier = Modifier.padding(bottom = 40.dp))
            Row {
                NameField(value = credentials.name, onChange = { data -> credentials = credentials.copy(name = data) })
                Spacer(modifier = Modifier.width(20.dp))
                SurnameField(value = credentials.surname, onChange = { data -> credentials = credentials.copy(surname = data) })
            }
            Spacer(modifier = Modifier.height(20.dp))
            EmailField(value = credentials.email, onChange = { data -> credentials = credentials.copy(email = data) })
            Spacer(modifier = Modifier.height(20.dp))
            PasswordField(value = credentials.password, onChange = { data -> credentials = credentials.copy(password = data) }, submit = { checkCredentials(credentials, context) })
            Spacer(modifier = Modifier.height(20.dp))
            RepeatPasswordField(value = credentials.repeatPassword, onChange = { data -> credentials = credentials.copy(repeatPassword = data) }, submit = { checkCredentials(credentials, context) })
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { checkCredentials(credentials, context) },
                enabled = credentials.isNotEmpty(),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "SIGN UP", style = TextStyle(Color.White))
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = annotatedText,
                fontSize = 16.sp,
                modifier = Modifier.clickable {
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}



/**
 * Function to validate user credentials and initiate registration process.
 * If credentials are valid and passwords match, attempts to create user account using Firebase authentication.
 * Displays a toast message on success or failure.
 * @param credentials User registration data including name, surname, email, password, and repeat password.
 * @param context The context used to start activities and display toast messages.
 * @see RegisterCredentials
 * @see FirebaseAuth
 */

fun checkCredentials(credentials: RegisterCredentials, context: Context) {
    if (credentials.isNotEmpty() && credentials.password == credentials.repeatPassword) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(credentials.email, credentials.password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    context.startActivity(Intent(context, MainActivity::class.java))
                    Toast.makeText(context, "Register Successfully", Toast.LENGTH_SHORT).show()
                    context.finish()
                } else {
                    Toast.makeText(context, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    } else {
        Toast.makeText(context, "Wrong credentials", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Preview function to display a preview of the RegisterForm composable.
 * @see RegisterForm
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterFormPreview() {
    PhysioConsultTheme {
        RegisterForm()
    }
}