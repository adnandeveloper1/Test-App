package com.nexappra.testapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.Calendar


// ----------------------------------------------------
// App colors
// ----------------------------------------------------

private val BrandYellow = Color(0xFFFFBE32)
private val BrandYellowDark = Color(0xFFF4A900)
private val ScreenBackground = Color(0xFFFFF8E9)

private val FieldBackground = Color(0xFFFAFAFA)
private val FieldBorder = Color(0xFFE7E7E7)

private val MainTextColor = Color(0xFF151515)
private val SecondaryTextColor = Color(0xFF777777)
private val SuccessGreen = Color(0xFF79C942)


// ----------------------------------------------------
// Screen names
// ----------------------------------------------------

private const val SCREEN_LOGIN = "login"
private const val SCREEN_PERSONAL_INFO = "personal_info"
private const val SCREEN_SECURE_ACCOUNT = "secure_account"
private const val SCREEN_HOME = "home"


// ----------------------------------------------------
// User data models
// ----------------------------------------------------

data class PersonalInfo(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val username: String = ""
)

data class SecureAccountInfo(
    val day: String = "",
    val month: String = "",
    val year: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val securityQuestion: String = "",
    val securityAnswer: String = ""
)


// ----------------------------------------------------
// App theme
// ----------------------------------------------------

@Composable
fun AuthAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = BrandYellow,
            onPrimary = Color.Black,
            background = ScreenBackground,
            surface = Color.White,
            onSurface = MainTextColor
        ),
        content = content
    )
}


// ----------------------------------------------------
// App navigation
// ----------------------------------------------------

@Composable
fun AuthApp() {

    var currentScreen by rememberSaveable {
        mutableStateOf(SCREEN_LOGIN)
    }

    var personalInfo by remember {
        mutableStateOf(PersonalInfo())
    }

    var successMessage by rememberSaveable {
        mutableStateOf("")
    }

    when (currentScreen) {

        SCREEN_LOGIN -> {
            LoginScreen(
                externalSuccessMessage = successMessage,
                onLoginSuccess = {
                    successMessage = ""
                    currentScreen = SCREEN_HOME
                },
                onCreateAccount = {
                    successMessage = ""
                    currentScreen = SCREEN_PERSONAL_INFO
                }
            )
        }

        SCREEN_PERSONAL_INFO -> {
            PersonalInfoScreen(
                initialInformation = personalInfo,
                onContinue = { information ->
                    personalInfo = information
                    currentScreen = SCREEN_SECURE_ACCOUNT
                },
                onBackToLogin = {
                    currentScreen = SCREEN_LOGIN
                }
            )
        }

        SCREEN_SECURE_ACCOUNT -> {
            SecureAccountScreen(
                personalInfo = personalInfo,
                onCreateAccountSuccess = {
                    successMessage = "Account created successfully. Please log in."
                    currentScreen = SCREEN_LOGIN
                },
                onBackToPersonalInfo = {
                    currentScreen = SCREEN_PERSONAL_INFO
                },
                onBackToLogin = {
                    currentScreen = SCREEN_LOGIN
                }
            )
        }

        SCREEN_HOME -> {
            HomeScreen(
                userName = personalInfo.firstName.ifBlank {
                    "User"
                },
                onLogout = {
                    AuthManager.logout()
                    currentScreen = SCREEN_LOGIN
                }
            )
        }
    }
}


// ----------------------------------------------------
// Login screen
// ----------------------------------------------------

@Composable
private fun LoginScreen(
    externalSuccessMessage: String,
    onLoginSuccess: () -> Unit,
    onCreateAccount: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rememberPassword by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var infoMessage by rememberSaveable { mutableStateOf(externalSuccessMessage) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    AuthPageLayout(
        title = "Hello",
        subtitle = "Welcome Back!"
    ) {

        FormHeading(
            title = "Login Account",
            description = "Enter your email address and password to continue."
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppTextField(
            label = "Email Address",
            placeholder = "Your Email Address",
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
                infoMessage = ""
            },
            keyboardType = KeyboardType.Email,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            label = "Password",
            placeholder = "Enter Your Password",
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
                infoMessage = ""
            },
            keyboardType = KeyboardType.Password,
            isPassword = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier.clickable(enabled = !isLoading) {
                    rememberPassword = !rememberPassword
                },
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = rememberPassword,
                    onCheckedChange = {
                        rememberPassword = it
                    },
                    enabled = !isLoading,
                    colors = CheckboxDefaults.colors(
                        checkedColor = SuccessGreen,
                        uncheckedColor = FieldBorder,
                        checkmarkColor = Color.White
                    )
                )

                Text(
                    text = "Save Password",
                    fontSize = 12.sp,
                    color = MainTextColor
                )
            }

            TextButton(
                onClick = {
                    if (email.isBlank()) {
                        errorMessage = "Email enter karein to reset password."
                        return@TextButton
                    }
                    isLoading = true
                    scope.launch {
                        AuthManager.resetPassword(email.trim()).onSuccess {
                            infoMessage = "Password reset link sent to $email"
                            errorMessage = ""
                        }.onFailure {
                            errorMessage = it.message ?: "Error sending reset link."
                            infoMessage = ""
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                Text(
                    text = "Forgot Password?",
                    fontSize = 12.sp,
                    color = MainTextColor
                )
            }
        }

        if (infoMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = infoMessage,
                color = SuccessGreen,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        ErrorMessage(message = errorMessage)

        Spacer(modifier = Modifier.height(10.dp))

        PrimaryButton(
            text = if (isLoading) "Logging In..." else "Login Account",
            onClick = {
                errorMessage = when {
                    email.isBlank() -> "Email address enter karein."
                    !email.contains("@") -> "Enter Valid email address."
                    password.isBlank() -> "Enter Password."
                    else -> ""
                }

                if (errorMessage.isBlank()) {
                    isLoading = true
                    scope.launch {
                        AuthManager.login(email.trim(), password).onSuccess {
                            onLoginSuccess()
                        }.onFailure {
                            errorMessage = it.message ?: "Login failed."
                        }
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onCreateAccount,
            enabled = !isLoading
        ) {
            Text(
                text = "Create New Account",
                color = MainTextColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


// ----------------------------------------------------
// Personal information screen
// ----------------------------------------------------

@Composable
private fun PersonalInfoScreen(
    initialInformation: PersonalInfo,
    onContinue: (PersonalInfo) -> Unit,
    onBackToLogin: () -> Unit
) {

    var firstName by rememberSaveable(initialInformation.firstName) {
        mutableStateOf(initialInformation.firstName)
    }

    var lastName by rememberSaveable(initialInformation.lastName) {
        mutableStateOf(initialInformation.lastName)
    }

    var email by rememberSaveable(initialInformation.email) {
        mutableStateOf(initialInformation.email)
    }

    var username by rememberSaveable(initialInformation.username) {
        mutableStateOf(initialInformation.username)
    }

    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    AuthPageLayout(
        title = "Join Us",
        subtitle = "Create Free Account"
    ) {

        FormHeading(
            title = "Personal Info",
            description = "Provide your basic personal information to create your account."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your Name",
            modifier = Modifier.fillMaxWidth(),
            color = MainTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AppTextField(
                modifier = Modifier.weight(1f),
                label = "First Name",
                placeholder = "First Name",
                value = firstName,
                onValueChange = {
                    firstName = it
                    errorMessage = ""
                }
            )

            AppTextField(
                modifier = Modifier.weight(1f),
                label = "Last Name",
                placeholder = "Last Name",
                value = lastName,
                onValueChange = {
                    lastName = it
                    errorMessage = ""
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            label = "Email Address",
            placeholder = "Your Email Address",
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
            },
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            label = "Username",
            placeholder = "example1234",
            value = username,
            onValueChange = {
                username = it
                    .lowercase()
                    .replace(" ", "")

                errorMessage = ""
            }
        )

        ErrorMessage(message = errorMessage)

        Spacer(modifier = Modifier.height(22.dp))

        PrimaryButton(
            text = "Save & Continue",
            onClick = {

                errorMessage = when {

                    firstName.isBlank() -> "Enter first name."
                    lastName.isBlank() -> "Enter last name."
                    email.isBlank() -> "Enter email address."
                    !email.contains("@") -> "Enter Valid email address."
                    username.isBlank() -> "Enter Username."
                    username.length < 4 -> "Username must 4 characters."
                    else -> ""
                }

                if (errorMessage.isBlank()) {
                    onContinue(
                        PersonalInfo(
                            firstName = firstName.trim(),
                            lastName = lastName.trim(),
                            email = email.trim(),
                            username = username.trim()
                        )
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onBackToLogin
        ) {
            Text(
                text = "Back to Login",
                color = MainTextColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


// ----------------------------------------------------
// Secure account screen
// ----------------------------------------------------

@Composable
private fun SecureAccountScreen(
    personalInfo: PersonalInfo,
    onCreateAccountSuccess: () -> Unit,
    onBackToPersonalInfo: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val currentYear = remember {
        Calendar.getInstance().get(Calendar.YEAR)
    }

    val days = remember { (1..31).map { it.toString() } }

    val months = remember {
        listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
    }

    val years = remember {
        (currentYear downTo currentYear - 80).map { it.toString() }
    }

    val securityQuestions = remember {
        listOf(
            "What is your first school name?",
            "What is your childhood nickname?",
            "Who was your favourite teacher?",
            "What is your birth city?"
        )
    }

    var selectedDay by rememberSaveable { mutableStateOf("") }
    var selectedMonth by rememberSaveable { mutableStateOf("") }
    var selectedYear by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var selectedQuestion by rememberSaveable { mutableStateOf("") }
    var securityAnswer by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    AuthPageLayout(
        title = "Join Us",
        subtitle = "Create Free Account"
    ) {

        FormHeading(
            title = "Secure Account",
            description = "Secure your account with a password and recovery information."
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Birthday",
            modifier = Modifier.fillMaxWidth(),
            color = MainTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AppDropdownField(
                modifier = Modifier.weight(0.8f),
                placeholder = "Day",
                selectedValue = selectedDay,
                options = days,
                enabled = !isLoading,
                onSelected = {
                    selectedDay = it
                    errorMessage = ""
                }
            )

            AppDropdownField(
                modifier = Modifier.weight(1.3f),
                placeholder = "Month",
                selectedValue = selectedMonth,
                options = months,
                enabled = !isLoading,
                onSelected = {
                    selectedMonth = it
                    errorMessage = ""
                }
            )

            AppDropdownField(
                modifier = Modifier.weight(1f),
                placeholder = "Year",
                selectedValue = selectedYear,
                options = years,
                enabled = !isLoading,
                onSelected = {
                    selectedYear = it
                    errorMessage = ""
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            label = "Password",
            placeholder = "Create a Password",
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            keyboardType = KeyboardType.Password,
            isPassword = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            label = "Phone Number",
            placeholder = "+92 300 1234567",
            value = phoneNumber,
            onValueChange = { newValue ->
                phoneNumber = newValue.filter { character ->
                    character.isDigit() || character == '+' || character == ' '
                }
                errorMessage = ""
            },
            keyboardType = KeyboardType.Phone,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Security Question",
            modifier = Modifier.fillMaxWidth(),
            color = MainTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppDropdownField(
            placeholder = "Select Security Question",
            selectedValue = selectedQuestion,
            options = securityQuestions,
            enabled = !isLoading,
            onSelected = {
                selectedQuestion = it
                errorMessage = ""
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            label = "Security Answer",
            placeholder = "Your Answer...",
            value = securityAnswer,
            onValueChange = {
                securityAnswer = it
                errorMessage = ""
            },
            enabled = !isLoading
        )

        ErrorMessage(message = errorMessage)

        Spacer(modifier = Modifier.height(22.dp))

        PrimaryButton(
            text = if (isLoading) "Creating Account..." else "Create Account",
            onClick = {
                errorMessage = when {
                    selectedDay.isBlank() || selectedMonth.isBlank() || selectedYear.isBlank() -> "Birthday select karein."
                    password.length < 6 -> "Password kam az kam 6 characters ka hona chahiye."
                    phoneNumber.filter { it.isDigit() }.length < 10 -> "Valid phone number enter karein."
                    selectedQuestion.isBlank() -> "Security question select karein."
                    securityAnswer.isBlank() -> "Security answer enter karein."
                    else -> ""
                }

                if (errorMessage.isBlank()) {
                    isLoading = true
                    scope.launch {
                        val secureInfo = SecureAccountInfo(
                            day = selectedDay,
                            month = selectedMonth,
                            year = selectedYear,
                            password = password,
                            phoneNumber = phoneNumber.trim(),
                            securityQuestion = selectedQuestion,
                            securityAnswer = securityAnswer.trim()
                        )
                        AuthManager.createAccount(personalInfo, secureInfo).onSuccess {
                            onCreateAccountSuccess()
                        }.onFailure {
                            errorMessage = it.message ?: "Account creation failed."
                        }
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(6.dp))

        TextButton(
            onClick = onBackToPersonalInfo,
            enabled = !isLoading
        ) {
            Text(
                text = "Back to Personal Info",
                color = MainTextColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }

        TextButton(
            onClick = onBackToLogin,
            enabled = !isLoading
        ) {
            Text(
                text = "Back to Login",
                color = SecondaryTextColor,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}


// ----------------------------------------------------
// Home screen
// ----------------------------------------------------

@Composable
private fun HomeScreen(
    userName: String,
    onLogout: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        contentAlignment = Alignment.Center
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            color = Color.White,
            shape = RoundedCornerShape(30.dp),
            shadowElevation = 10.dp
        ) {

            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Welcome, $userName!",
                    color = MainTextColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Your account screen opened successfully.",
                    color = SecondaryTextColor,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(26.dp))

                PrimaryButton(
                    text = "Logout",
                    onClick = onLogout
                )
            }
        }
    }
}


// ----------------------------------------------------
// Common yellow header + white card
// ----------------------------------------------------

@Composable
private fun AuthPageLayout(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
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
                shape = RoundedCornerShape(
                    topStart = 34.dp,
                    topEnd = 34.dp
                ),
                shadowElevation = 14.dp
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = 24.dp,
                            vertical = 24.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content()
                }
            }
        }
    }
}


// ----------------------------------------------------
// Form heading
// ----------------------------------------------------

@Composable
private fun FormHeading(
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
private fun AppTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    enabled: Boolean = true
) {

    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = {
            Text(
                text = label,
                fontSize = 12.sp
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = SecondaryTextColor,
                fontSize = 13.sp
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        visualTransformation = when {

            isPassword && !passwordVisible -> {
                PasswordVisualTransformation()
            }

            else -> {
                VisualTransformation.None
            }
        },
        trailingIcon = if (isPassword) {

            {
                TextButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    },
                    contentPadding = PaddingValues(
                        horizontal = 6.dp
                    )
                ) {
                    Text(
                        text = if (passwordVisible) {
                            "Hide"
                        } else {
                            "Show"
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
            focusedContainerColor = FieldBackground,
            unfocusedContainerColor = FieldBackground,
            cursorColor = BrandYellowDark,
            focusedLabelColor = MainTextColor,
            unfocusedLabelColor = SecondaryTextColor
        )
    )
}


@Composable
private fun AppDropdownField(
    placeholder: String,
    selectedValue: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
    ) {

        OutlinedButton(
            onClick = {
                expanded = true
            },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(
                width = 1.dp,
                color = FieldBorder
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = FieldBackground,
                contentColor = MainTextColor
            ),
            contentPadding = PaddingValues(
                horizontal = 14.dp
            )
        ) {

            Text(
                text = selectedValue.ifBlank {
                    placeholder
                },
                modifier = Modifier.weight(1f),
                color = if (selectedValue.isBlank()) {
                    SecondaryTextColor
                } else {
                    MainTextColor
                },
                fontSize = 12.sp,
                maxLines = 1
            )

            Text(
                text = "⌄",
                color = SecondaryTextColor,
                fontSize = 19.sp
            )
        }

        DropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.heightIn(
                max = 280.dp
            )
        ) {

            options.forEach { option ->

                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = 13.sp
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


// ----------------------------------------------------
// Main yellow button
// ----------------------------------------------------

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
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
            disabledContainerColor = BrandYellow.copy(
                alpha = 0.5f
            )
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 5.dp,
            pressedElevation = 1.dp
        )
    ) {

        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


// ----------------------------------------------------
// Error text
// ----------------------------------------------------

@Composable
private fun ErrorMessage(
    message: String
) {

    if (message.isNotBlank()) {

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = message,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            textAlign = TextAlign.Start
        )
    }
}
