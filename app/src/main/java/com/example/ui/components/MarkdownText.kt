package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val lines = remember(markdown) { markdown.split("\n") }
    var inCodeBlock = false
    var codeLang = ""
    val codeContent = StringBuilder()

    Column(modifier = modifier) {
        var index = 0
        while (index < lines.size) {
            val line = lines[index]

            if (line.trim().startsWith("```")) {
                if (inCodeBlock) {
                    // Close code block
                    CodeBlock(code = codeContent.toString().trimEnd(), language = codeLang)
                    codeContent.clear()
                    inCodeBlock = false
                    codeLang = ""
                } else {
                    inCodeBlock = true
                    codeLang = line.trim().removePrefix("```").trim().ifEmpty { "CODE" }
                }
                index++
                continue
            }

            if (inCodeBlock) {
                codeContent.append(line).append("\n")
                index++
                continue
            }

            // Normal markdown line parsing
            when {
                line.startsWith("# ") -> {
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("# "), textColor),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                line.startsWith("## ") -> {
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("## "), textColor),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                    )
                }
                line.startsWith("### ") -> {
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("### "), textColor),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }
                line.startsWith("- ") || line.startsWith("* ") || line.startsWith("• ") -> {
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = parseInlineMarkdown(line.substring(2), textColor),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = textColor
                        )
                    }
                }
                line.matches(Regex("^\\d+\\..*")) -> {
                    val num = line.substringBefore(".")
                    val content = line.substringAfter(". ")
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "$num.",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = parseInlineMarkdown(content, textColor),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = textColor
                        )
                    }
                }
                line.isBlank() -> {
                    Spacer(modifier = Modifier.height(6.dp))
                }
                else -> {
                    Text(
                        text = parseInlineMarkdown(line, textColor),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = textColor,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
            index++
        }

        if (inCodeBlock && codeContent.isNotEmpty()) {
            CodeBlock(code = codeContent.toString().trimEnd(), language = codeLang)
        }
    }
}

@Composable
fun CodeBlock(code: String, language: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
    ) {
        // Code header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = language.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8),
                fontFamily = FontFamily.Monospace
            )
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Code", code)
                    clipboard.setPrimaryClip(clip)
                    isCopied = true
                    Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                    coroutineScope.launch {
                        delay(2000)
                        isCopied = false
                    }
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "Copy code",
                    tint = if (isCopied) Color(0xFF10B981) else Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Code content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            Text(
                text = code,
                color = Color(0xFFE2E8F0),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

fun parseInlineMarkdown(text: String, defaultColor: Color) = buildAnnotatedString {
    var i = 0
    while (i < text.size) {
        if (text.startsWith("**", i)) {
            val end = text.indexOf("**", i + 2)
            if (end != -1) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = defaultColor)) {
                    append(text.substring(i + 2, end))
                }
                i = end + 2
                continue
            }
        } else if (text.startsWith("`", i)) {
            val end = text.indexOf("`", i + 1)
            if (end != -1) {
                withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = Color(0xFF1E293B),
                        color = Color(0xFF38BDF8)
                    )
                ) {
                    append(" ${text.substring(i + 1, end)} ")
                }
                i = end + 1
                continue
            }
        } else if (text.startsWith("*", i) && !text.startsWith("**", i)) {
            val end = text.indexOf("*", i + 1)
            if (end != -1) {
                withStyle(SpanStyle(fontWeight = FontWeight.Normal, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)) {
                    append(text.substring(i + 1, end))
                }
                i = end + 1
                continue
            }
        }
        append(text[i])
        i++
    }
}

val String.size: Int
    get() = length
