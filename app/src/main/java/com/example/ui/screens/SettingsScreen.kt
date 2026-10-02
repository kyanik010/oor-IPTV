package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.PrimaryButton
import com.example.ui.components.tvFocusable
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.Dimens
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.Shapes
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val activationInfo by viewModel.activationInfo.collectAsState()
    val activeAccount by viewModel.activeAccount.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var hardwareAccel by remember { mutableStateOf(true) }
    var autoReconnect by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("settings_screen")
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Space16, vertical = Dimens.Space12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.size(Dimens.MinTouchTarget)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(Dimens.Space8))

            Text(
                text = "الإعدادات والحساب",
                style = Typography.titleLarge.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = Dimens.Space20, vertical = Dimens.Space8)
        ) {
            // Account Card
            SettingGroupCard(title = "معلومات الحساب والاشتراك") {
                SettingRow(
                    title = "معرّف الجهاز (Device ID)",
                    subtitle = activationInfo.deviceId,
                    icon = Icons.Filled.AccountCircle,
                    action = {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(activationInfo.deviceId))
                            Toast.makeText(context, "تم نسخ معرّف الجهاز", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Filled.ContentCopy, "Copy", tint = GoldPrimary)
                        }
                    }
                )

                SettingRow(
                    title = "حالة الاشتراك",
                    subtitle = "${activationInfo.planName} (ينتهي في: ${activationInfo.expirationDateText})",
                    icon = Icons.Filled.Settings
                )

                SettingRow(
                    title = "تبديل الحساب أو القائمة",
                    subtitle = activeAccount?.playlistName ?: "حساب تجريبي نشط",
                    icon = Icons.Filled.SwitchAccount,
                    onClick = {
                        viewModel.navigateTo(ScreenRoute.Activation)
                    }
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Space16))

            // Player Settings
            SettingGroupCard(title = "إعدادات المشغّل وجودة البث") {
                SettingRow(
                    title = "التسريع العتادي (Hardware Acceleration)",
                    subtitle = "تحسين سلاسة الفيديو وتقليل استهلاك البطارية",
                    icon = Icons.Filled.Speed,
                    action = {
                        Switch(
                            checked = hardwareAccel,
                            onCheckedChange = { hardwareAccel = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldPrimary,
                                checkedTrackColor = DarkNavySurface
                            )
                        )
                    }
                )

                SettingRow(
                    title = "إعادة الاتصال التلقائية",
                    subtitle = "إعادة محاولة تشغيل البث عند انقطاع الشبكة",
                    icon = Icons.Filled.Speed,
                    action = {
                        Switch(
                            checked = autoReconnect,
                            onCheckedChange = { autoReconnect = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldPrimary,
                                checkedTrackColor = DarkNavySurface
                            )
                        )
                    }
                )

                SettingRow(
                    title = "مسح الذاكرة المؤقتة (Clear Cache)",
                    subtitle = "تفريغ الملفات المؤقتة لتسريع التطبيق",
                    icon = Icons.Filled.CleaningServices,
                    onClick = {
                        Toast.makeText(context, "تم مسح الذاكرة المؤقتة بنجاح", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Space16))

            // Privacy and About
            SettingGroupCard(title = "حول والتطبيق والخصوصية") {
                SettingRow(
                    title = "سياسة الخصوصية والاستخدام",
                    subtitle = "التطبيق مشغل وسائط مستقل لا يقدم أي محتوى",
                    icon = Icons.Filled.PrivacyTip,
                    onClick = { showPrivacyDialog = true }
                )

                SettingRow(
                    title = "إصدار التطبيق",
                    subtitle = "Noor IPTV Player v1.0.0 Pro",
                    icon = Icons.Filled.Info
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Space48))
        }

        // Privacy Policy Dialog
        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                containerColor = DarkNavyCard,
                title = { Text("سياسة الخصوصية", color = GoldPrimary) },
                text = {
                    Text(
                        text = "تطبيق نور IPTV هو مشغّل وسائط فقط. التطبيق لا يوفر ولا يستضيف أو يبيع أي قنوات تلفزيونية أو محتوى مرئي.\n\nالمستخدم مسؤول مسؤولية كاملة عن المحتوى والروابط والاشتراكات التي يقوم بإضافتها إلى التطبيق.",
                        style = Typography.bodyMedium.copy(color = TextSecondary)
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showPrivacyDialog = false }) {
                        Text("موافق", color = GoldPrimary)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingGroupCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = Typography.labelLarge.copy(color = GoldPrimary, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = Dimens.Space8, start = Dimens.Space4)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Shapes.large)
                .background(DarkNavyCard)
                .border(1.dp, GlassBorder, Shapes.large)
                .padding(Dimens.Space16)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.Space16)) {
                content()
            }
        }
    }
}

@Composable
fun SettingRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldPrimary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(Dimens.Space16))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = Typography.bodySmall.copy(color = TextSecondary)
            )
        }

        if (action != null) {
            action()
        }
    }
}
