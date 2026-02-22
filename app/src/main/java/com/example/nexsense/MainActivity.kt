package com.example.nexsense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

// --- Color Palette ---
val DarkBg = Color(0xFF030712)
val SurfaceBg = Color(0xFF1F2937)
val Emerald500 = Color(0xFF10B981)
val Emerald400 = Color(0xFF34D399)
val Yellow500 = Color(0xFFEAB308)
val Red400 = Color(0xFFF87171)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NexSenseTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun NexSenseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = DarkBg,
            surface = SurfaceBg,
            primary = Emerald500
        ),
        content = content
    )
}

// --- Data Models ---
data class InventoryItem(
    val id: Int,
    val name: String,
    val expiry: String,
    val status: ItemStatus,
    val score: Int,
    val icon: String
)

enum class ItemStatus { FRESH, WARNING, CRITICAL }

val sampleItems = listOf(
    InventoryItem(1, "Apple", "3 days left", ItemStatus.FRESH, 92, "\uD83C\uDF4E"),
    InventoryItem(2, "Lemon", "12 hours left", ItemStatus.WARNING, 45, "\uD83C\uDF4B"),
    InventoryItem(3, "orange", "4 days left", ItemStatus.FRESH, 88, "\uD83C\uDF4A"),
    InventoryItem(4, "mango", "Expired", ItemStatus.CRITICAL, 12, "\uD83E\uDD6D")
)

@Composable
fun MainScreen() {
    var activeTab by remember { mutableStateOf("dashboard") }
    var showScanner by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        // Ambient Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Emerald500.copy(alpha = 0.05f), radius = size.width * 0.8f, center = Offset(0f, 0f))
        }

        Column(modifier = Modifier.fillMaxSize().padding(bottom = 100.dp)) {
            when (activeTab) {
                "dashboard" -> DashboardView()
                "inventory" -> InventoryView()
                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Settings", color = Color.Gray)
                }
            }
        }

        // POSITIONING FIXED: .align(Alignment.BottomCenter) is used here in the BoxScope
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavigationBar(
                activeTab = activeTab,
                onTabSelected = { if (it == "scan") showScanner = true else activeTab = it }
            )
        }

        if (showScanner) {
            ScannerOverlay(onClose = { showScanner = false })
        }
    }
}

@Composable
fun BottomNavigationBar(activeTab: String, onTabSelected: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBg.copy(alpha = 0.95f))
            .border(1.dp, Color.White.copy(alpha = 0.1f))
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Icons.Rounded.Home, "Home", activeTab == "dashboard") { onTabSelected("dashboard") }


            NavItem(Icons.Rounded.List, "Items", activeTab == "inventory") { onTabSelected("inventory") }
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Icon(icon, contentDescription = null, tint = if (isActive) Emerald400 else Color.Gray, modifier = Modifier.size(24.dp))
        Text(label, fontSize = 10.sp, color = if (isActive) Emerald400 else Color.Gray)
    }
}

@Composable
fun DashboardView() {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Header()
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(SurfaceBg, Color(0xFF111827))))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Fridge Health", color = Color.LightGray, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text("Environment Optimal", color = Color.Gray, fontSize = 14.sp)
                    }
                    FreshnessGauge(score = 84)
                }
                Spacer(modifier = Modifier.height(24.dp))

                // FIXED: Material 3 HorizontalDivider syntax
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.White.copy(alpha = 0.1f))

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // FIXED: Used Icons.Default set which is more reliable across IDE versions
                    SensorStat(painterResource(R.drawable.img),"Temp", "3.4°C")
                    SensorStat(painterResource(R.drawable.img_1), "Humid", "42%")
                    SensorStat(painterResource(R.drawable.img_2), "IoT", "Online", isActive = true)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Expiring Soon", color = Color.LightGray, modifier = Modifier.padding(horizontal = 24.dp))
        Column(modifier = Modifier.padding(24.dp)) {
            sampleItems.filter { it.score < 90 }.forEach { item ->
                InventoryItemCard(item)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun Header() {
    Row(modifier = Modifier.fillMaxWidth().padding(24.dp, 48.dp, 24.dp, 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text("NexSense", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Emerald400)
            Text("BIO-SENSING ACTIVE", fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun InventoryView() {
    Column(Modifier.padding(horizontal = 24.dp)) {
        Header()
        Text("Inventory", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(sampleItems) { item ->
                InventoryItemCard(item)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun InventoryItemCard(item: InventoryItem) {
    val color = when(item.status) {
        ItemStatus.FRESH -> Emerald400
        ItemStatus.WARNING -> Yellow500
        ItemStatus.CRITICAL -> Red400
    }
    Row(
        modifier = Modifier.fillMaxWidth().background(SurfaceBg.copy(0.5f), RoundedCornerShape(16.dp))
            .border(1.dp, color.copy(0.2f), RoundedCornerShape(16.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.icon, fontSize = 24.sp)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(item.name, color = Color.White)
            Text(item.expiry, color = color, fontSize = 12.sp)
        }
        Text("${item.score}%", color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FreshnessGauge(score: Int) {
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(80.dp)) {
            drawCircle(Color.Gray.copy(0.2f), style = Stroke(8.dp.toPx()))
            drawArc(Emerald500, -90f, (score/100f)*360f, false, style = Stroke(8.dp.toPx(), cap = StrokeCap.Round))
        }
        Text("$score", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SensorStat(icon: Painter, label: String, value: String, isActive: Boolean = false) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
            Text(label, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
        }
        Text(value, color = if(isActive) Emerald400 else Color.White, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ScannerOverlay(onClose: () -> Unit) {
    var isScanning by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(2000); isScanning = false }

    Box(Modifier.fillMaxSize().background(Color.Black).zIndex(10f)) {
        IconButton(onClick = onClose, Modifier.align(Alignment.TopEnd).padding(24.dp)) {
            Icon(Icons.Rounded.Close, null, tint = Color.White)
        }
        Box(Modifier.size(280.dp).align(Alignment.Center).border(2.dp, Emerald500, RoundedCornerShape(24.dp)))
        Text(if(isScanning) "SCANNING..." else "COMPLETE", color = Emerald400, modifier = Modifier.align(Alignment.Center).offset(y = 180.dp))
    }
}