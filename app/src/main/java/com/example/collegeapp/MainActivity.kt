package com.example.collegeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.draw.shadow
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("test_key", "test_value")
        analytics.logEvent("test_event", bundle)

        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val tabs = listOf(
        TabItem("Online Payment", Icons.Filled.Payment),
        TabItem("Courses", Icons.Filled.MenuBook),
        TabItem("Web Registration", Icons.Filled.Language),
        TabItem("Login", Icons.Filled.Person)
    )
    var selectedTabIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.10f),
                        Color.White
                    )
                )
            )
    ) {
        Column {
            // HEADER (Logo + College Name)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colors.primary,
                                MaterialTheme.colors.secondary
                            )
                        ),
                        shape = RoundedCornerShape(bottomEnd = 32.dp, bottomStart = 32.dp)
                    )
                    .padding(top = 32.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Icon Circle with Y letter (replace with logo as needed)
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.White.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Y",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "YOGIRAJ COLLEGE",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tabs (Modern Card look)
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(4.dp)
                            .padding(horizontal = 28.dp),
                        color = MaterialTheme.colors.primary
                    )
                },
                edgePadding = 8.dp,
                backgroundColor = Color.Transparent
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(tab.icon, contentDescription = tab.label)
                        },
                        text = {
                            Text(
                                tab.label,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) MaterialTheme.colors.primary else Color.Gray
                            )
                        },
                        modifier = Modifier
                            .height(60.dp)
                            .padding(vertical = 6.dp)
                    )
                }
            }

            // Main Content (card, padding, elevation)
            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp)
                    .shadow(2.dp, RoundedCornerShape(18.dp))
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (selectedTabIndex) {
                    0 -> OnlinePaymentScreen()
                    1 -> CoursesScreen()
                    2 -> WebRegistrationScreen()
                    3 -> LoginScreen()
                }
            }
        }
    }
}

data class TabItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable fun OnlinePaymentScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.Payment, contentDescription = "Payment", tint = MaterialTheme.colors.primary, modifier = Modifier.size(50.dp))
        Spacer(Modifier.height(12.dp))
        Text("Pay your fees online easily and securely.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}
@Composable fun CoursesScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.MenuBook, contentDescription = "Courses", tint = MaterialTheme.colors.primary, modifier = Modifier.size(50.dp))
        Spacer(Modifier.height(12.dp))
        Text("Browse all our offered courses.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}
@Composable fun WebRegistrationScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.Language, contentDescription = "Web Registration", tint = MaterialTheme.colors.primary, modifier = Modifier.size(50.dp))
        Spacer(Modifier.height(12.dp))
        Text("Register online for classes, exams, and more.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}
@Composable fun LoginScreen() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.Person, contentDescription = "Login", tint = MaterialTheme.colors.primary, modifier = Modifier.size(50.dp))
        Spacer(Modifier.height(12.dp))
        Text("Login or create your student/staff account.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}












//package com.example.collegeapp
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.collegeapp.ui.theme.CollegeAppTheme
//import com.google.firebase.analytics.FirebaseAnalytics
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.unit.dp
//
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.filled.*
//
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.unit.sp
//
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.material.icons.filled.Payment
//import androidx.compose.material.icons.filled.MenuBook
//import androidx.compose.material.icons.filled.Language
//import androidx.compose.material.icons.filled.Person
//
//
//class MainActivity : ComponentActivity() {
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//
//            // Firebase Analytics test
//            val analytics = FirebaseAnalytics.getInstance(this)
//            val bundle = Bundle()
//            bundle.putString("test_key", "test_value")
//            analytics.logEvent("test_event", bundle)
//
//            setContent { // ← this is Compose's way of setting content
//                CollegeAppTheme {
//                    // Your Compose UI
//                    MainScreen()
//                }
//            }
//        }
//
//
//}
//
//
//@Composable
//fun MainScreen() {
//    val tabs = listOf(
//        TabItem("Online Payment", Icons.Filled.Payment),
//        TabItem("Courses", Icons.Filled.MenuBook),
//        TabItem("Web Registration", Icons.Filled.Language),
//        TabItem("Login", Icons.Filled.Person)
//    )
//    var selectedTabIndex by remember { mutableStateOf(0) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    listOf(
//                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
//                        Color.White
//                    )
//                )
//            )
//    ) {
//        Column {
//            // HEADER (Logo + College Name)
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(
//                        brush = Brush.horizontalGradient(
//                            colors = listOf(
//                                MaterialTheme.colorScheme.primary,
//                                MaterialTheme.colorScheme.secondary
//                            )
//                        ),
//                        shape = RoundedCornerShape(bottomEnd = 32.dp, bottomStart = 32.dp)
//                    )
//                    .padding(top = 32.dp, bottom = 24.dp)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 24.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    // App Icon Circle with Y letter (replace with logo as needed)
//                    Box(
//                        modifier = Modifier
//                            .size(54.dp)
//                            .background(Color.White.copy(alpha = 0.25f), CircleShape),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "Y",
//                            fontSize = 32.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = MaterialTheme.colorScheme.onPrimary
//                        )
//                    }
//                    Spacer(Modifier.width(16.dp))
//                    Text(
//                        text = "YOGIRAJ COLLEGE OF SCIENCE",
//                        color = Color.White,
//                        fontSize = 22.sp,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//            }
//
//            // Tabs (Modern Card look)
//            ScrollableTabRow(
//                selectedTabIndex = selectedTabIndex,
//                indicator = { tabPositions ->
//                    TabRowDefaults.Indicator(
//                        Modifier
//                            .height(4.dp)
//                            .padding(horizontal = 28.dp),
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                },
//                edgePadding = 8.dp,
//                containerColor = Color.Transparent,
//            ) {
//                tabs.forEachIndexed { index, tab ->
//                    Tab(
//                        selected = selectedTabIndex == index,
//                        onClick = { selectedTabIndex = index },
//                        icon = {
//                            Icon(tab.icon, contentDescription = tab.label)
//                        },
//                        text = {
//                            Text(
//                                tab.label,
//                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
//                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else Color.Gray
//                            )
//                        },
//                        modifier = Modifier
//                            .height(60.dp)
//                            .padding(vertical = 6.dp)
//                    )
//                }
//            }
//
//            // Main Content (card, padding, elevation)
//            Box(
//                Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//                    .padding(24.dp)
//                    .shadow(2.dp, RoundedCornerShape(18.dp))
//                    .background(Color.White, RoundedCornerShape(18.dp))
//                    .padding(24.dp),
//                contentAlignment = Alignment.TopCenter
//            ) {
//                when (selectedTabIndex) {
//                    0 -> OnlinePaymentScreen()
//                    1 -> CoursesScreen()
//                    2 -> WebRegistrationScreen()
//                    3 -> LoginScreen()
//                }
//            }
//        }
//    }
//}
//
//data class TabItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
//
//@Composable fun OnlinePaymentScreen() {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Icon(Icons.Filled.Payment, contentDescription = "Payment", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(50.dp))
//        Spacer(Modifier.height(12.dp))
//        Text("Pay your fees online easily and securely.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
//    }
//}
//
//@Composable fun CoursesScreen() {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Icon(Icons.Filled.MenuBook, contentDescription = "Courses", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(50.dp))
//        Spacer(Modifier.height(12.dp))
//        Text("Browse all our offered courses.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
//    }
//}
//
//@Composable fun WebRegistrationScreen() {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Icon(Icons.Filled.Language, contentDescription = "Web Registration", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(50.dp))
//        Spacer(Modifier.height(12.dp))
//        Text("Register online for classes, exams, and more.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
//    }
//}
//
//@Composable fun LoginScreen() {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Icon(Icons.Filled.Person, contentDescription = "Login", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(50.dp))
//        Spacer(Modifier.height(12.dp))
//        Text("Login or create your student/staff account.", fontSize = 18.sp, fontWeight = FontWeight.Medium)
//    }
//}
//
//
//
//
//
//
//
//
//
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    CollegeAppTheme {
//        Greeting("Android")
//    }
//}