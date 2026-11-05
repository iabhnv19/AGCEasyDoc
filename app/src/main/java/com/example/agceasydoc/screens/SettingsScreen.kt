package com.example.agceasydoc.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agceasydoc.R

private const val GITHUB_REPO_URL = "https://github.com/iabhnv19/AGCEasyDoc"
private const val GITHUB_PROFILE_URL = "https://github.com/iabhnv19"
private const val GITHUB_ISSUES_URL = "https://github.com/iabhnv19/AGCEasyDoc/issues"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color(0xFF1E1E1E)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color(0xFF1E1E1E)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {

                SettingsSectionTitle("General Info")
                SettingsCard {
                    SettingsValueRow("App Name", "AGC EasyDoc")
                    SettingsDivider()
                    SettingsValueRow("Institution", "Amritsar Group Of Colleges")
                    SettingsDivider()
                    SettingsValueRow("Type", "Academic Document Creator")
                }

                Spacer(Modifier.height(20.dp))

                SettingsSectionTitle("Open Source")
                SettingsCard {
                    SettingsValueRow(
                        title = "Source Code",
                        value = "GitHub",
                        onClick = { openUrl(context, GITHUB_REPO_URL) }
                    )
                    SettingsDivider()
                    SettingsValueRow(
                        title = "Developer Profile",
                        value = "@iabhnv19",
                        onClick = { openUrl(context, GITHUB_PROFILE_URL) }
                    )
                    SettingsDivider()
                    SettingsValueRow(
                        title = "License",
                        value = "GNU AGPLv3",
                        onClick = { openUrl(context, "$GITHUB_REPO_URL/blob/main/LICENSE") }
                    )
                }

                Spacer(Modifier.height(20.dp))

                SettingsSectionTitle("About")
                SettingsCard {
                    SettingsValueRow("App Version", "1.9.5")
                    SettingsDivider()
                    SettingsValueRow("Last Updated", "Mar 2026")
                    SettingsDivider()
                    SettingsValueRow("Developer", "LAZZY GUY")
                }

                Spacer(Modifier.height(20.dp))

                SettingsSectionTitle("Support & Feedback")
                SettingsCard {
                    SettingsValueRow(
                        title = "Send Feedback",
                        value = "Share ideas",
                        onClick = { openUrl(context, GITHUB_ISSUES_URL) }
                    )
                    SettingsDivider()
                    SettingsValueRow(
                        title = "Report a Bug",
                        value = "Report issue",
                        onClick = { openUrl(context, "$GITHUB_ISSUES_URL/new") }
                    )
                    SettingsDivider()
                    SettingsValueRow(
                        title = "Contact Developer",
                        value = "Get in touch",
                        onClick = { openUrl(context, GITHUB_PROFILE_URL) }
                    )
                }

                Spacer(Modifier.height(40.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Designed & developed by",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color(0xFF9CA3AF)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Image(
                        painter = painterResource(id = R.drawable.lg),
                        contentDescription = "Company Logo",
                        modifier = Modifier.height(35.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF6B7280))
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        color = Color(0xFF6B7280),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(vertical = 4.dp)
    ) {
        content()
    }
}

@Composable
private fun SettingsValueRow(
    title: String,
    value: String,
    titleColor: Color = Color(0xFF1E1E1E),
    valueColor: Color = Color(0xFF6B7280),
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = titleColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp
        )
        if (onClick != null) {
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .height(1.dp)
            .background(Color(0xFFF0F0F0))
    )
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}
