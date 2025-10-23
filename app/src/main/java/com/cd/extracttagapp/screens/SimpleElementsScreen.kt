package com.cd.extracttagapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cd.uielementmanager.presentation.composables.UIElementViewModel
import com.cd.uielementmanager.presentation.composables.trackElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleElementsScreen(
    elementTracker: UIElementViewModel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Simple Elements (Max 8)",
                        modifier = Modifier.trackElement("simple_elements_screen", "title")
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.trackElement("simple_elements_screen", "back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Element 1: Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .trackElement("simple_elements_screen", "header_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Element 2: Title Text
                    Text(
                        "Welcome Dashboard",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.trackElement("simple_elements_screen", "welcome_text")
                    )
                    
                    // Element 3: Info Icon
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Info",
                        modifier = Modifier
                            .size(24.dp)
                            .trackElement("simple_elements_screen", "info_icon")
                    )
                }
            }
            
            // Element 4 & 5: Two buttons in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Element 4: Primary Button
                Button(
                    onClick = { /* Action */ },
                    modifier = Modifier
                        .weight(1f)
                        .trackElement("simple_elements_screen", "primary_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Item")
                }
                
                // Element 5: Secondary Button
                OutlinedButton(
                    onClick = { /* Action */ },
                    modifier = Modifier
                        .weight(1f)
                        .trackElement("simple_elements_screen", "secondary_button")
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Search")
                }
            }
            
            // Element 6: Switch with label
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .trackElement("simple_elements_screen", "switch_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Enable Notifications",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    // Element 7: Switch
                    Switch(
                        checked = true,
                        onCheckedChange = { /* Toggle */ },
                        modifier = Modifier.trackElement("simple_elements_screen", "notification_switch")
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Element 8: Bottom Action Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .trackElement("simple_elements_screen", "bottom_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "All Systems Operational",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Last checked: 2 minutes ago",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Summary text (not tracked as it would exceed 8 elements)
            Text(
                "Exactly 10 UI elements are being tracked on this screen",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}