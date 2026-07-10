package com.nexappra.testapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexappra.testapp.R
import com.nexappra.testapp.ui.theme.BrandYellow
import com.nexappra.testapp.ui.theme.BrandYellowDark
import com.nexappra.testapp.ui.theme.FieldBackground
import com.nexappra.testapp.ui.theme.FieldBorder
import com.nexappra.testapp.ui.theme.MainTextColor
import com.nexappra.testapp.ui.theme.ScreenBackground
import com.nexappra.testapp.ui.theme.SecondaryTextColor

@Composable
fun AuthPageLayout(
    title: String,
    subtitle: String,
    isLoading: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandYellow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                color = MainTextColor,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                modifier = Modifier.fillMaxWidth(),
                color = MainTextColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .imePadding(),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
                shadowElevation = 14.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = content
                    )

                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.55f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = BrandYellowDark)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormHeading(
    title: String,
    description: String
) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
        color = MainTextColor,
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(7.dp))

    Text(
        text = description,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        color = SecondaryTextColor,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun AuthTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    errorMessage: String? = null
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = { Text(text = label, fontSize = 12.sp) },
        placeholder = {
            Text(
                text = placeholder,
                color = SecondaryTextColor,
                fontSize = 13.sp
            )
        },
        singleLine = true,
        isError = errorMessage != null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = when {
            isPassword && !passwordVisible -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        },
        trailingIcon = if (isPassword) {
            {
                TextButton(
                    onClick = { passwordVisible = !passwordVisible },
                    enabled = enabled,
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text(
                        text = if (passwordVisible) {
                            stringResource(R.string.hide_password)
                        } else {
                            stringResource(R.string.show_password)
                        },
                        color = SecondaryTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            null
        },
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandYellowDark,
            unfocusedBorderColor = FieldBorder,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedContainerColor = FieldBackground,
            unfocusedContainerColor = FieldBackground,
            errorContainerColor = FieldBackground,
            cursorColor = BrandYellowDark,
            focusedLabelColor = MainTextColor,
            unfocusedLabelColor = SecondaryTextColor
        )
    )

    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(6.dp))
        StatusMessage(
            message = errorMessage,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandYellow,
            contentColor = MainTextColor,
            disabledContainerColor = BrandYellow.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 5.dp,
            pressedElevation = 1.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MainTextColor,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.size(10.dp))
        }

        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusMessage(
    message: String,
    color: Color
) {
    Text(
        text = message,
        modifier = Modifier.fillMaxWidth(),
        color = color,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        textAlign = TextAlign.Start
    )
}

@Composable
fun LoadingGate() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = BrandYellowDark)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.checking_session),
                color = MainTextColor,
                fontSize = 14.sp
            )
        }
    }
}
