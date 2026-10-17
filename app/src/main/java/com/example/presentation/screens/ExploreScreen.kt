package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.Tertiary
import com.example.ui.theme.TertiaryContainer
import com.example.ui.theme.TertiaryFixed

data class OpportunityItem(
    val id: String,
    val title: String,
    val organization: String,
    val category: String, // "Internship", "Scholarship", "Hackathon"
    val stipendOrPrize: String,
    val location: String,
    val deadline: String,
    val matchScore: Int,
    val tags: List<String>,
    val description: String
)

@Composable
fun ExploreScreen(
    filter: String,
    onFilterChange: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    scanState: String,
    onScanResume: () -> Unit
) {
    val opportunities = remember {
        listOf(
            OpportunityItem(
                id = "1",
                title = "Backend Engineering Intern",
                organization = "Razorpay",
                category = "Internship",
                stipendOrPrize = "₹45,000 / month",
                location = "Bengaluru / Hybrid",
                deadline = "Apply by Nov 10",
                matchScore = 96,
                tags = listOf("Kotlin", "Spring Boot", "Distributed Systems"),
                description = "Build resilient payment gateway infrastructure processing 15M+ daily transactions."
            ),
            OpportunityItem(
                id = "2",
                title = "Reliance Foundation Scholar 2024",
                organization = "Reliance Foundation",
                category = "Scholarship",
                stipendOrPrize = "₹2,00,000 Grant",
                location = "All India • Merit-cum-Means",
                deadline = "Closes in 6 Days",
                matchScore = 91,
                tags = listOf("Engineering", "Full Tuition", "Mentorship"),
                description = "Full tuition grant + professional leadership training for high-performing tech undergraduates."
            ),
            OpportunityItem(
                id = "3",
                title = "Smart India Hackathon (SIH 2024)",
                organization = "Ministry of Education & AICTE",
                category = "Hackathon",
                stipendOrPrize = "₹1,00,000 / Problem Statement",
                location = "National Finals • New Delhi",
                deadline = "College Internal Round: Oct 30",
                matchScore = 94,
                tags = listOf("EdTech", "AI/ML", "Govt of India"),
                description = "Nationwide platform solving digital governance and smart education challenges with real deployment."
            ),
            OpportunityItem(
                id = "4",
                title = "AI Research & Systems Fellow",
                organization = "Microsoft Research India",
                category = "Internship",
                stipendOrPrize = "₹65,000 / month",
                location = "Bengaluru",
                deadline = "Nov 15",
                matchScore = 88,
                tags = listOf("LLMs", "PyTorch", "Graph Algorithms"),
                description = "Collaborate with senior scientists on on-device model quantization and low-resource NLP."
            ),
            OpportunityItem(
                id = "5",
                title = "Post-Matric National Scholarship (NSP)",
                organization = "Government of India",
                category = "Scholarship",
                stipendOrPrize = "₹50,000 / year",
                location = "All India Colleges",
                deadline = "Nov 30",
                matchScore = 85,
                tags = listOf("Direct Benefit Transfer", "Central Sector"),
                description = "Financial assistance covering hostel fees and semester tuition subsidies."
            ),
            OpportunityItem(
                id = "6",
                title = "HackIITB Annual Hackathon",
                organization = "IIT Bombay Student Council",
                category = "Hackathon",
                stipendOrPrize = "₹3,50,000 Pool",
                location = "Powai, Mumbai",
                deadline = "Nov 5",
                matchScore = 95,
                tags = listOf("Fintech", "Web3", "Hardware"),
                description = "36-hour sprint featuring mentors from top Silicon Valley and Indian VC-backed startups."
            )
        )
    }

    val filterChips = listOf("All", "Internship", "Scholarship", "Hackathon")

    val filteredList = opportunities.filter { item ->
        val matchesCategory = if (filter == "All") true else item.category.equals(filter, ignoreCase = true)
        val matchesSearch = if (searchQuery.isBlank()) true else {
            item.title.contains(searchQuery, ignoreCase = true) ||
            item.organization.contains(searchQuery, ignoreCase = true) ||
            item.tags.any { it.contains(searchQuery, ignoreCase = true) }
        }
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("explore_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Campus Hub & Explore",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Internships, Scholarships & Tech Events",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search by skill, company or scholarship...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Primary)
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                focusedBorderColor = Primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("explore_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // AI Match Score Card + Resume Scanner Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Primary, Secondary)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = TertiaryFixed,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "94% PROFILE MATCH",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TertiaryFixed
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "CSE Sem 5 Resume Synced",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Matching you with Tier-1 tech internships & active AICTE scholarships.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = onScanResume,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("scan_resume_button")
                    ) {
                        if (scanState == "SCANNING") {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Primary, strokeWidth = 2.dp)
                        } else if (scanState == "UPDATED") {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Synced!", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Re-scan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterChips) { chip ->
                val isSelected = filter.equals(chip, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isSelected) Primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier
                        .clickable { onFilterChange(chip) }
                        .testTag("filter_$chip")
                ) {
                    Text(
                        text = chip,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Opportunities List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList, key = { it.id }) { item ->
                OpportunityCard(item)
            }
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun OpportunityCard(item: OpportunityItem) {
    var isBookmarked by remember { mutableStateOf(false) }
    var isApplied by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (item.category) {
                                        "Internship" -> PrimaryFixed
                                        "Scholarship" -> TertiaryFixed
                                        else -> SecondaryFixed
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.category.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (item.category) {
                                    "Internship" -> Primary
                                    "Scholarship" -> Tertiary
                                    else -> Secondary
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = item.deadline,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = item.organization,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { isBookmarked = !isBookmarked },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) Primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tags row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item.tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Action & Stipend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CurrencyRupee,
                        contentDescription = null,
                        tint = Tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = item.stipendOrPrize,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = { isApplied = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isApplied) MaterialTheme.colorScheme.surfaceContainerHighest else Primary,
                        contentColor = if (isApplied) MaterialTheme.colorScheme.onSurface else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    if (isApplied) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Applied", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Easy Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp))
                    }
                }
            }
        }
    }
}
