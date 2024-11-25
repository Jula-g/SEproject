package com.example.physioconsult.login.SignUp


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp


/**
 * Composable function to display a name input field.
 *
 * @param value Current value of the name input field.
 * @param onChange Callback function to handle value changes in the name input field.
 * @param modifier Modifier for styling and layout customization.
 * @param label Label text for the name input field.
 * @param placeholder Placeholder text for the name input field when empty.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NameField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Name",
    placeholder: String = " "
) {
    val focusManager = LocalFocusManager.current

    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Person,
            contentDescription = null
        )
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier
            .width(140.dp)
            .border(
                width = 1.5.dp,
                color = Color.Black,
                shape = RoundedCornerShape(30.dp)
            )
            .height(50.dp),
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Left) }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.White,
            focusedIndicatorColor = Color.White
        ),

        )

}


/**
 * Composable function to display a surname input field.
 *
 * @param value Current value of the surname input field.
 * @param onChange Callback function to handle value changes in the surname input field.
 * @param modifier Modifier for styling and layout customization.
 * @param label Label text for the surname input field.
 * @param placeholder Placeholder text for the surname input field when empty.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SurnameField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Surname",
    placeholder: String = " "
) {
    val focusManager = LocalFocusManager.current

    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Person,
            contentDescription = null
        )
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier
            .border(
                width = 1.5.dp,
                color = Color.Black,
                shape = RoundedCornerShape(30.dp)
            )
            .height(50.dp),
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.White,
            focusedIndicatorColor = Color.White
        ),

        )

}