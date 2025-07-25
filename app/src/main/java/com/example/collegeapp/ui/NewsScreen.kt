package com.example.collegeapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Data model matches Firestore
data class NotificationItem(
    val id: String = "",
    val date: String = "",
    val className: String = "",
    val subject: String = "",
    val body: String = ""
)

@Composable
fun ModernNewsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 4.dp,
        shape = RoundedCornerShape(18.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            content = content
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewsScreen() {
    val db = FirebaseFirestore.getInstance()
    var notifications by remember { mutableStateOf(listOf<NotificationItem>()) }

    // For expanded view
    var selectedNotificationIndex by remember { mutableStateOf<Int?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Real-time Firestore listener
    DisposableEffect(Unit) {
        val registration = db.collection("notifications")
            .orderBy("date")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    notifications = snapshot.documents.map { doc ->
                        NotificationItem(
                            id = doc.id,
                            date = doc.getString("date") ?: "",
                            className = doc.getString("className") ?: "",
                            subject = doc.getString("subject") ?: "",
                            body = doc.getString("body") ?: ""
                        )
                    }
                }
            }
        onDispose { registration.remove() }
    }

    fun refreshNotifications() {
        isRefreshing = true
        db.collection("notifications")
            .orderBy("date")
            .get()
            .addOnSuccessListener { snapshot ->
                notifications = snapshot.documents.map { doc ->
                    NotificationItem(
                        id = doc.id,
                        date = doc.getString("date") ?: "",
                        className = doc.getString("className") ?: "",
                        subject = doc.getString("subject") ?: "",
                        body = doc.getString("body") ?: ""
                    )
                }
                isRefreshing = false
            }
            .addOnFailureListener {
                // Optional: Handle error
                isRefreshing = false
            }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Filled.Notifications,
            contentDescription = "Notifications",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Live News & Notifications",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(18.dp))

        if (notifications.isEmpty()) {
            Text(
                "No notifications available.",
                modifier = Modifier.padding(top = 32.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
        } else if (selectedNotificationIndex == null) {
            val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    coroutineScope.launch { refreshNotifications() }
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(notifications.size) { index ->
                        val notification = notifications[index]
                        ModernNewsCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .clickable { selectedNotificationIndex = index } // Make card clickable
                        ) {
                            Text(
                                notification.subject,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colors.onSurface,
                                fontSize = 17.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                notification.body.take(60) + if (notification.body.length > 60) "..." else "",
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.86f),
                                fontSize = 15.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Class: ${notification.className}  •  Date: ${notification.date}",
                                color = MaterialTheme.colors.primary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        } else {
            val notification = notifications[selectedNotificationIndex!!]
            ModernNewsCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    notification.subject,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 20.sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    notification.body,
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Class: ${notification.className}",
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Text(
                    "Date: ${notification.date}",
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = { selectedNotificationIndex = null }) {
                    Text("Back")
                }
            }
        }
    }
}

//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun NewsScreen() {
//    val db = FirebaseFirestore.getInstance()
//    var notifications by remember { mutableStateOf(listOf<NotificationItem>()) }
//
//    // For expanded view
//    var selectedNotificationIndex by remember { mutableStateOf<Int?>(null) }
//    var isRefreshing by remember { mutableStateOf(false) }
//    val coroutineScope = rememberCoroutineScope()
//
//    // Real-time Firestore listener
//    DisposableEffect(Unit) {
//        val registration = db.collection("notifications")
//            .orderBy("date")
//            .addSnapshotListener { snapshot, _ ->
//                if (snapshot != null) {
//                    notifications = snapshot.documents.map { doc ->
//                        NotificationItem(
//                            id = doc.id,
//                            date = doc.getString("date") ?: "",
//                            className = doc.getString("className") ?: "",
//                            subject = doc.getString("subject") ?: "",
//                            body = doc.getString("body") ?: ""
//                        )
//                    }
//                }
//            }
//        onDispose { registration.remove() }
//    }
//    fun refreshNotifications() {
//        isRefreshing = true
//        db.collection("notifications")
//            .orderBy("date")
//            .get()
//            .addOnSuccessListener { snapshot ->
//                notifications = snapshot.documents.map { doc ->
//                    NotificationItem(
//                        id = doc.id,
//                        date = doc.getString("date") ?: "",
//                        className = doc.getString("className") ?: "",
//                        subject = doc.getString("subject") ?: "",
//                        body = doc.getString("body") ?: ""
//                    )
//                }
//                isRefreshing = false
//            }
//            .addOnFailureListener {
//                // Optional: Handle error
//                isRefreshing = false
//            }
//    }
//
//    // UI
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.fillMaxSize()
//    ) {
//
//        Icon(
//            Icons.Filled.Notifications,
//            contentDescription = "Notifications",
//            tint = MaterialTheme.colors.primary,
//            modifier = Modifier.size(50.dp)
//        )
//        Spacer(Modifier.height(12.dp))
//        Text(
//            "Live News & Notifications",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Medium
//        )
//        Spacer(Modifier.height(18.dp))
//
//
//        if (notifications.isEmpty()) {
//            Text(
//                "No notifications available.",
//                modifier = Modifier.padding(top = 32.dp),
//                fontSize = 16.sp,
//                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
//            )
//        } else if (selectedNotificationIndex == null) {
//            val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
//
//            SwipeRefresh(
//                state = swipeRefreshState,
//                onRefresh = {
//                    coroutineScope.launch { refreshNotifications() }
//                }
//            ) {
//
//                LazyColumn(
//                    modifier = Modifier.fillMaxWidth(),
//                    contentPadding = PaddingValues(8.dp)
//
//                ) {
//                    items(notifications.size) { index ->
//                        val notification = notifications[index]
//                        ModernNewsCard(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 14.dp, vertical = 8.dp)
//                                .clickable { selectedNotificationIndex = index } // <--- Add this
//
//                        ) {
//                            Text(
//                                notification.subject,
//                                fontWeight = FontWeight.SemiBold,
//                                color = MaterialTheme.colors.onSurface,
//                                fontSize = 17.sp
//                            )
//                            Spacer(Modifier.height(6.dp))
//                            Text(
//                                notification.body.take(60) + if (notification.body.length > 60) "..." else "",
//                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.86f),
//                                fontSize = 15.sp
//                            )
//                            Spacer(Modifier.height(8.dp))
//                            Text(
//                                "Class: ${notification.className}  •  Date: ${notification.date}",
//                                color = MaterialTheme.colors.primary,
//                                fontWeight = FontWeight.Medium,
//                                fontSize = 13.sp
//                            )
//                        }
//                    }
//                }
//
//            }
//
//        } else {
//            val notification = notifications[selectedNotificationIndex!!]
//            ModernNewsCard(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 14.dp, vertical = 8.dp)
//            ) {
//                Text(
//                    notification.subject,
//                    fontWeight = FontWeight.SemiBold,
//                    color = MaterialTheme.colors.onSurface,
//                    fontSize = 17.sp
//                )
//                Spacer(Modifier.height(6.dp))
//                Text(
//                    notification.body.take(60) + if (notification.body.length > 60) "..." else "",
//                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.86f),
//                    fontSize = 15.sp
//                )
//                Spacer(Modifier.height(8.dp))
//                Text(
//                    "Class: ${notification.className}  •  Date: ${notification.date}",
//                    color = MaterialTheme.colors.primary,
//                    fontWeight = FontWeight.Medium,
//                    fontSize = 13.sp
//                )
//            }
//        }
//    }
//}
