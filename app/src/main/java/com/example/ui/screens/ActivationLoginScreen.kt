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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AppTextField
import com.example.ui.components.GhostButton
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.components.TicketCard
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.Dimens
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PillShape
import com.example.ui.theme.Shapes
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppViewModel

@Composable
fun ActivationLoginScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val activationInfo by viewModel.activationInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var activationCodeInput by remember { mutableStateOf("") }
    var activationError by remember { mutableStateOf<String?>(null) }

    // Xtream Fields
    var xtreamServer by remember { mutableStateOf("") }
    var xtreamUser by remember { mutableStateOf("") }
    var xtreamPass by remember { mutableStateOf("") }
    var xtreamError by remember { mutableStateOf<String?>(null) }

    // M3U Fields
    var m3uName by remember { mutableStateOf("قائمتي الشخصية") }
    var m3uUrl by remember { mutableStateOf("") }
    var m3uError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .padding(horizontal = Dimens.Space16, vertical = Dimens.Space24),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = stringResource(R.string.app_name),
                style = Typography.displayMedium.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(Dimens.Space4))
            Text(
                text = "تفعيل التطبيق وإضافة بيانات الاشتراك",
                style = Typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(Dimens.Space20))

            // Ticket Card for Device Activation
            TicketCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.device_id),
                                style = Typography.labelMedium.copy(color = TextSecondary)
                            )
                            Spacer(modifier = Modifier.height(Dimens.Space2))
                            Text(
                                text = activationInfo.deviceId,
                                style = Typography.titleLarge.copy(
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                            )
                        }

                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(activationInfo.deviceId))
                                Toast.makeText(context, context.getString(R.string.copied), Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(Dimens.MinTouchTarget)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkNavySurface)
                                .border(0.5.dp, GlassBorder, RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copy Device ID",
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.Space12))

                    // Trial / Status Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkNavySurface)
                            .padding(horizontal = Dimens.Space12, vertical = Dimens.Space8)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = StatusSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(Dimens.Space8))
                        Text(
                            text = if (activationInfo.trialDaysRemaining > 0)
                                "فترة تجريبية مجانية: متبقي ${activationInfo.trialDaysRemaining} أيام (تنتهي: ${activationInfo.expirationDateText})"
                            else
                                "تم التفعيل: ${activationInfo.planName}",
                            style = Typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.Medium)
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.Space16))

                    // Code input row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextField(
                            value = activationCodeInput,
                            onValueChange = {
                                activationCodeInput = it
                                activationError = null
                            },
                            placeholder = "أدخل كود التفعيل (VIP-XXXX)",
                            leadingIcon = Icons.Filled.VpnKey,
                            errorMessage = activationError,
                            modifier = Modifier.weight(1f),
                            testTag = "activation_code_input"
                        )

                        Spacer(modifier = Modifier.width(Dimens.Space8))

                        PrimaryButton(
                            text = stringResource(R.string.activate),
                            onClick = {
                                viewModel.activateCode(activationCodeInput) { success, err ->
                                    if (success) {
                                        Toast.makeText(context, "تم تفعيل الحساب بنجاح!", Toast.LENGTH_SHORT).show()
                                        activationCodeInput = ""
                                    } else {
                                        activationError = err
                                    }
                                }
                            },
                            isLoading = isLoading,
                            modifier = Modifier.height(Dimens.InputHeight),
                            testTag = "activate_button"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Space24))

            // Tabs for Xtream vs M3U
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkNavyCard,
                contentColor = GoldPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GoldPrimary,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(Shapes.medium)
                    .border(1.dp, GlassBorder, Shapes.medium)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = stringResource(R.string.xtream_login),
                            style = Typography.labelLarge.copy(
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) GoldPrimary else TextSecondary
                            )
                        )
                    },
                    modifier = Modifier.height(Dimens.ButtonHeight)
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = stringResource(R.string.m3u_login),
                            style = Typography.labelLarge.copy(
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) GoldPrimary else TextSecondary
                            )
                        )
                    },
                    modifier = Modifier.height(Dimens.ButtonHeight)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Space20))

            // Form Content
            if (selectedTab == 0) {
                // Xtream Codes Form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Shapes.large)
                        .background(DarkNavyCard)
                        .border(1.dp, GlassBorder, Shapes.large)
                        .padding(Dimens.Space20)
                ) {
                    AppTextField(
                        value = xtreamServer,
                        onValueChange = { xtreamServer = it; xtreamError = null },
                        placeholder = "http://example.com:8080",
                        label = stringResource(R.string.server_url),
                        leadingIcon = Icons.Filled.Storage,
                        testTag = "xtream_server_input"
                    )

                    Spacer(modifier = Modifier.height(Dimens.Space12))

                    AppTextField(
                        value = xtreamUser,
                        onValueChange = { xtreamUser = it; xtreamError = null },
                        placeholder = "اسم المستخدم",
                        label = stringResource(R.string.username),
                        leadingIcon = Icons.Filled.Person,
                        testTag = "xtream_user_input"
                    )

                    Spacer(modifier = Modifier.height(Dimens.Space12))

                    AppTextField(
                        value = xtreamPass,
                        onValueChange = { xtreamPass = it; xtreamError = null },
                        placeholder = "كلمة المرور",
                        label = stringResource(R.string.password),
                        leadingIcon = Icons.Filled.Lock,
                        isPassword = true,
                        errorMessage = xtreamError,
                        testTag = "xtream_pass_input"
                    )

                    Spacer(modifier = Modifier.height(Dimens.Space20))

                    PrimaryButton(
                        text = stringResource(R.string.login),
                        onClick = {
                            if (xtreamServer.isBlank() || xtreamUser.isBlank() || xtreamPass.isBlank()) {
                                xtreamError = "يرجى تعبئة جميع الحقول"
                                return@PrimaryButton
                            }
                            viewModel.loginXtream(xtreamServer, xtreamUser, xtreamPass) { success, err ->
                                if (!success) {
                                    xtreamError = err
                                }
                            }
                        },
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "xtream_submit_button"
                    )
                }
            } else {
                // M3U Playlist Form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Shapes.large)
                        .background(DarkNavyCard)
                        .border(1.dp, GlassBorder, Shapes.large)
                        .padding(Dimens.Space20)
                ) {
                    AppTextField(
                        value = m3uName,
                        onValueChange = { m3uName = it },
                        placeholder = "اسم القائمة (اختياري)",
                        label = stringResource(R.string.playlist_name),
                        leadingIcon = Icons.Filled.Storage,
                        testTag = "m3u_name_input"
                    )

                    Spacer(modifier = Modifier.height(Dimens.Space12))

                    AppTextField(
                        value = m3uUrl,
                        onValueChange = { m3uUrl = it; m3uError = null },
                        placeholder = "http://example.com/playlist.m3u",
                        label = stringResource(R.string.playlist_url),
                        leadingIcon = Icons.Filled.Link,
                        errorMessage = m3uError,
                        testTag = "m3u_url_input"
                    )

                    Spacer(modifier = Modifier.height(Dimens.Space20))

                    PrimaryButton(
                        text = "تحميل وبدء المشاهدة",
                        onClick = {
                            if (m3uUrl.isBlank()) {
                                m3uError = "يرجى إدخال رابط القائمة M3U"
                                return@PrimaryButton
                            }
                            viewModel.loginM3u(m3uUrl, m3uName) { success, err ->
                                if (!success) {
                                    m3uError = err
                                }
                            }
                        },
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "m3u_submit_button"
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Space24))

            // Quick Demo Button
            SecondaryButton(
                text = "الدخول السريع بحساب تجريبي (Demo)",
                icon = Icons.Filled.PlayArrow,
                onClick = {
                    // Navigate to Profiles or Home directly using the pre-seeded demo content
                    viewModel.navigateTo(com.example.ui.viewmodel.ScreenRoute.Profiles)
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "demo_button"
            )

            Spacer(modifier = Modifier.height(Dimens.Space16))
        }
    }
}
