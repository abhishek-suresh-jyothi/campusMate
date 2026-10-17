package com.example.presentation.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyNoteEntity
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.Tertiary
import com.example.ui.theme.TertiaryFixed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StudyNotesScreen(
    notes: List<StudyNoteEntity>,
    onBack: () -> Unit,
    onOpenAiStudy: () -> Unit,
    isAiSubscribed: Boolean = false,
    onOpenPaywall: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var isGeneratingNotes by remember { mutableStateOf(false) }
    var generationStatusText by remember { mutableStateOf("") }

    val localNotes = remember {
        mutableStateListOf(
            StudyNoteEntity(
                id = 101,
                title = "Divide & Conquer: Master Theorem Cheatsheet",
                subject = "Algorithms (CS302)",
                readTimeMinutes = 4,
                formulasCount = 3,
                confidencePercent = 99,
                summaryText = "Case 1: If d < log_b(a) then O(n^{log_b(a)}). Case 2: If d = log_b(a) then O(n^d log n). Recurrence relation handles split-and-merge algorithms like MergeSort."
            ),
            StudyNoteEntity(
                id = 102,
                title = "Virtual Memory & Inverted Page Tables",
                subject = "Operating Systems (CS304)",
                readTimeMinutes = 6,
                formulasCount = 2,
                confidencePercent = 96,
                summaryText = "Inverted Page Table contains one entry per physical frame instead of virtual pages. Saves physical memory drastically on 64-bit systems with hashing lookup."
            ),
            StudyNoteEntity(
                id = 103,
                title = "TCP Sliding Window & Flow Control",
                subject = "Computer Networks (CS306)",
                readTimeMinutes = 5,
                formulasCount = 2,
                confidencePercent = 97,
                summaryText = "Sender window size = min(rwnd, cwnd). Prevents receiver buffer overflow and network bottleneck congestion during high-throughput streaming."
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("study_notes_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "AI Notes & Snap-Study",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Slide Summarizer, Formula Solver & Audio Transcriber",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Snap & Solve / Slide Upload Interactive Card
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
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
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
                                    text = if (isAiSubscribed) "AI MULTIMODAL • PRO UNLOCKED" else "AI MULTIMODAL • PRO ₹49/MO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TertiaryFixed
                                )
                            }
                        }
                    }

                    Text(
                        text = "Snap Blackboard or Upload Slide PDF",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = if (isAiSubscribed)
                            "CampusMate AI synthesizes long lecture slides into concise exam summaries with bulleted mnemonics."
                        else
                            "Campus AI Pro (₹49 INR/month) required to transcribe handwritten formulas & summarize 50+ page slides.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!isAiSubscribed) {
                                    onOpenPaywall()
                                } else {
                                    coroutineScope.launch {
                                        isGeneratingNotes = true
                                        generationStatusText = "Analyzing chalkboard photo..."
                                        delay(1000)
                                        generationStatusText = "Extracting mathematical formulae & graph..."
                                        delay(1000)
                                        localNotes.add(
                                            0,
                                            StudyNoteEntity(
                                                id = System.currentTimeMillis(),
                                                title = "Eigenvalues & Spectral Theorem (Snapped)",
                                                subject = "Linear Algebra (MATH301)",
                                                readTimeMinutes = 3,
                                                formulasCount = 4,
                                                confidencePercent = 99,
                                                summaryText = "A matrix A is symmetric iff it has an orthogonal basis of eigenvectors. Det(A - λI) = 0 computes characteristic roots."
                                            )
                                        )
                                        isGeneratingNotes = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Primary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("snap_board_button")
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isAiSubscribed) "Snap Photo" else "Snap (Pro ₹49)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (!isAiSubscribed) {
                                    onOpenPaywall()
                                } else {
                                    coroutineScope.launch {
                                        isGeneratingNotes = true
                                        generationStatusText = "Parsing 48 slide pages..."
                                        delay(1200)
                                        localNotes.add(
                                            0,
                                            StudyNoteEntity(
                                                id = System.currentTimeMillis(),
                                                title = "Lecture 14: Banker's Deadlock Algorithm",
                                                subject = "Operating Systems (CS304)",
                                                readTimeMinutes = 7,
                                                formulasCount = 2,
                                                confidencePercent = 95,
                                                summaryText = "Safe state analysis via Need matrix <= Available. Prevents circular wait condition during multithreaded resource allocation."
                                            )
                                        )
                                        isGeneratingNotes = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isAiSubscribed) "Upload Slides" else "Upload (Pro ₹49)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (isGeneratingNotes) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = generationStatusText,
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Generated Study Notes Library
        Text(
            text = "Synthesized High-Yield Notes",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(localNotes, key = { it.id }) { note ->
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PrimaryFixed)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = note.subject,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }

                            Text(
                                text = "${note.readTimeMinutes} min read • ${note.confidencePercent}% confidence",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = note.summaryText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = onOpenAiStudy,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryFixed,
                                    contentColor = Primary
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Ask Tutor About This", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}
