package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.MarkdownText
import com.example.ui.components.NavDestination

enum class StudyMode(val label: String) {
    SIMPLE("Simple Explanation"),
    DETAILED("Detailed Explanation"),
    EXAM("Exam Answer"),
    REVISION("Revision Notes"),
    MCQ("MCQ Quiz"),
    FLASHCARDS("Flashcards")
}

data class McqQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@Composable
fun StudyScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var topic by remember { mutableStateOf("The Indian Constitution for Class 10") }
    var selectedMode by remember { mutableStateOf(StudyMode.EXAM) }

    // MCQ State
    var selectedAnswers by remember { mutableStateOf(mutableMapOf<Int, Int>()) }
    var score by remember { mutableStateOf<Int?>(null) }

    // Flashcard State
    var flashcardFlipped by remember { mutableStateOf(false) }
    var currentFlashcardIndex by remember { mutableStateOf(0) }

    val mcqs = remember {
        listOf(
            McqQuestion(
                question = "Who is known as the Chief Architect of the Indian Constitution?",
                options = listOf("Mahatma Gandhi", "Dr. B. R. Ambedkar", "Jawaharlal Nehru", "Sardar Vallabhbhai Patel"),
                correctIndex = 1,
                explanation = "Dr. B.R. Ambedkar was the Chairman of the Drafting Committee."
            ),
            McqQuestion(
                question = "When was the Constitution of India formally adopted by the Constituent Assembly?",
                options = listOf("26 November 1949", "26 January 1950", "15 August 1947", "30 January 1948"),
                correctIndex = 0,
                explanation = "Adopted on 26 November 1949 and came into effect on 26 January 1950."
            ),
            McqQuestion(
                question = "Which fundamental right was referred to as the 'Heart and Soul' of the Constitution by Dr. Ambedkar?",
                options = listOf("Right to Equality", "Right to Freedom of Religion", "Right to Constitutional Remedies", "Right to Education"),
                correctIndex = 2,
                explanation = "Article 32: Right to Constitutional Remedies empowers citizens to approach the Supreme Court."
            )
        )
    }

    val flashcards = remember {
        listOf(
            "Preamble" to "The introductory statement outlining ideals: Sovereign, Socialist, Secular, Democratic Republic, ensuring Justice, Liberty, Equality, and Fraternity.",
            "Fundamental Rights" to "Part III (Articles 12-35) protecting civil liberties of all citizens against state arbitrariness.",
            "Directive Principles (DPSP)" to "Part IV guidelines for the state to establish social and economic democracy, non-justiciable."
        )
    }

    val explanationContent = remember(selectedMode, topic) {
        when (selectedMode) {
            StudyMode.SIMPLE -> {
                "### The Indian Constitution (Simple Analogy)\n\n" +
                        "Think of the Indian Constitution like the **master rulebook of a school**:\n\n" +
                        "- **Who made it?** A committee led by Dr. B. R. Ambedkar.\n" +
                        "- **What does it do?** It ensures every student (citizen) has equal rights and that the principal and teachers (government) follow fair rules.\n" +
                        "- **Preamble**: The school's mission statement: fairness, equality, freedom, and kindness.\n\n" +
                        "*“Your AI, Your Way” - Adapted for beginner comprehension.*"
            }
            StudyMode.DETAILED -> {
                "### Detailed Study: The Constitution of India\n\n" +
                        "The Indian Constitution is the longest written supreme law of any sovereign nation in the world.\n\n" +
                        "#### Core Structural Pillars:\n" +
                        "1. **Preamble**: Sovereign, Socialist, Secular, Democratic Republic.\n" +
                        "2. **Federal System with Unitary Bias**: Power division between Center and States with residual emergency powers with the Union.\n" +
                        "3. **Independent Judiciary**: Supreme Court serves as the guardian of the Constitution.\n" +
                        "4. **Parliamentary Democracy**: Executive is directly responsible to the legislature."
            }
            StudyMode.EXAM -> {
                "### Exam-Oriented Model Answer (5 Marks)\n\n" +
                        "**Q: What are the salient features of the Indian Constitution? Explain with reference to democratic values.**\n\n" +
                        "**Answer Structure:**\n\n" +
                        "1. **Introduction**: Adopted on 26 November 1949, the Indian Constitution establishes a Sovereign, Socialist, Secular, Democratic Republic.\n" +
                        "2. **Key Salient Features**:\n" +
                        "   - *Lengthiest Written Constitution*: Blends provisions from UK, USA, Ireland, and Canada.\n" +
                        "   - *Fundamental Rights (Part III)*: Guarantees 6 core freedoms enforceable via Article 32.\n" +
                        "   - *Universal Adult Franchise*: Every citizen aged 18+ has equal voting rights without discrimination.\n" +
                        "   - *Secularism*: The state has no official religion and treats all faiths with equal respect.\n" +
                        "3. **Conclusion / Exam Tip**: Highlight Dr. Ambedkar's role as Drafting Committee Chairman for full marks."
            }
            StudyMode.REVISION -> {
                "### ⚡ 5-Minute Quick Revision Notes\n\n" +
                        "- ⏱️ **Drafting Duration**: 2 years, 11 months, 18 days.\n" +
                        "- 📜 **Original Schedule Count**: 8 schedules (now 12).\n" +
                        "- ⚖️ **Core Articles**: Art 14 (Equality), Art 19 (Speech), Art 21 (Life & Liberty), Art 32 (Constitutional Remedies).\n" +
                        "- 🎯 **Key Motto**: Satyameva Jayate (Truth Alone Triumphs)."
            }
            else -> ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Study Assistant",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Multi-level explanations, exam answers, quizzes & flashcards",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Topic Input
        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Study Topic or Chapter") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("study_topic_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Study Mode Chips
        Text("Select Learning Format", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StudyMode.values().forEach { mode ->
                FilterChip(
                    selected = selectedMode == mode,
                    onClick = { selectedMode = mode },
                    label = { Text(mode.label, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Content Display
        when (selectedMode) {
            StudyMode.MCQ -> {
                // Interactive MCQ Quiz
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Interactive Practice Quiz", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF00E5FF))
                            if (score != null) {
                                Text("Score: $score / ${mcqs.size}", fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        mcqs.forEachIndexed { qIndex, mcq ->
                            Text("${qIndex + 1}. ${mcq.question}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            mcq.options.forEachIndexed { optIndex, optText ->
                                val isSelected = selectedAnswers[qIndex] == optIndex
                                val isCorrect = mcq.correctIndex == optIndex
                                val isGraded = score != null

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when {
                                                isGraded && isCorrect -> Color(0xFF10B981).copy(alpha = 0.2f)
                                                isGraded && isSelected && !isCorrect -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                                isSelected -> Color(0xFF0C4A6E)
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            when {
                                                isGraded && isCorrect -> Color(0xFF10B981)
                                                isGraded && isSelected && !isCorrect -> Color(0xFFEF4444)
                                                isSelected -> Color(0xFF00E5FF)
                                                else -> Color.Transparent
                                            },
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            if (score == null) {
                                                val updated = selectedAnswers.toMutableMap()
                                                updated[qIndex] = optIndex
                                                selectedAnswers = updated
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(optText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            if (score != null) {
                                Text(
                                    text = "💡 Explanation: ${mcq.explanation}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Button(
                            onClick = {
                                var currentScore = 0
                                mcqs.forEachIndexed { idx, q ->
                                    if (selectedAnswers[idx] == q.correctIndex) currentScore++
                                }
                                score = currentScore
                                Toast.makeText(context, "Quiz scored: $currentScore / ${mcqs.size}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color(0xFF042F2E)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Submit Quiz", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            StudyMode.FLASHCARDS -> {
                // Interactive Flashcard
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Flashcard ${currentFlashcardIndex + 1} of ${flashcards.size}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF0C4A6E).copy(alpha = 0.5f))
                                .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(14.dp))
                                .clickable { flashcardFlipped = !flashcardFlipped }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val card = flashcards[currentFlashcardIndex]
                            if (flashcardFlipped) {
                                Text(card.second, fontSize = 13.sp, color = Color.White, lineHeight = 18.sp)
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(card.first, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("(Tap to flip definition)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    flashcardFlipped = false
                                    if (currentFlashcardIndex > 0) currentFlashcardIndex--
                                },
                                enabled = currentFlashcardIndex > 0
                            ) {
                                Text("Previous")
                            }
                            Button(
                                onClick = {
                                    flashcardFlipped = false
                                    if (currentFlashcardIndex < flashcards.size - 1) currentFlashcardIndex++
                                },
                                enabled = currentFlashcardIndex < flashcards.size - 1
                            ) {
                                Text("Next Card")
                            }
                        }
                    }
                }
            }
            else -> {
                // Markdown explanation
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        MarkdownText(markdown = explanationContent)
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.createNewChat()
                                    viewModel.sendMessage("I'm studying '$topic' in ${selectedMode.label} format. Please provide 3 practice quiz questions.")
                                    viewModel.navigateTo(NavDestination.AI_ASSISTANT)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Ask Follow-up in Chat", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
