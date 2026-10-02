package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ProfileEntity
import com.example.ui.components.AppTextField
import com.example.ui.components.PrimaryButton
import com.example.ui.components.tvFocusable
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.Dimens
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.Shapes
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.Typography
import com.example.ui.viewmodel.AppViewModel

@Composable
fun ProfilesScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val profiles by viewModel.profiles.collectAsState()
    var showPinDialogForProfile by remember { mutableStateOf<ProfileEntity?>(null) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    var showAddProfileDialog by remember { mutableStateOf(false) }
    var newProfileName by remember { mutableStateOf("") }
    var isNewProfileKids by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBg)
            .testTag("profiles_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(Dimens.Space24)
        ) {
            Text(
                text = "من يشاهد الآن؟",
                style = Typography.displayMedium.copy(color = GoldPrimary, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(Dimens.Space8))

            Text(
                text = "اختر ملفك الشخصي لتخصيص المفضلة وسجل المشاهدة",
                style = Typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(modifier = Modifier.height(Dimens.Space48))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space24),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(profiles) { profile ->
                    ProfileCard(
                        profile = profile,
                        onClick = {
                            if (profile.pinCode != null) {
                                showPinDialogForProfile = profile
                            } else {
                                viewModel.selectProfile(profile)
                            }
                        }
                    )
                }

                // Add Profile Button
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .tvFocusable(shape = CircleShape, onEnterClick = { showAddProfileDialog = true })
                            .clip(CircleShape)
                            .clickable { showAddProfileDialog = true }
                            .padding(Dimens.Space8)
                            .testTag("add_profile_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(DarkNavyCard)
                                .border(1.5.dp, GlassBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Profile",
                                tint = GoldPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimens.Space12))

                        Text(
                            text = "إضافة ملف",
                            style = Typography.titleMedium.copy(color = TextSecondary)
                        )
                    }
                }
            }
        }

        // PIN Dialog
        if (showPinDialogForProfile != null) {
            AlertDialog(
                onDismissRequest = {
                    showPinDialogForProfile = null
                    pinInput = ""
                    pinError = null
                },
                containerColor = DarkNavyCard,
                title = {
                    Text(
                        text = "رمز الحماية (PIN)",
                        style = Typography.headlineMedium.copy(color = GoldPrimary)
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "يرجى إدخال رمز المرور للملف الشخصي: ${showPinDialogForProfile?.name}",
                            style = Typography.bodyMedium.copy(color = TextSecondary)
                        )
                        Spacer(modifier = Modifier.height(Dimens.Space12))
                        AppTextField(
                            value = pinInput,
                            onValueChange = { pinInput = it; pinError = null },
                            placeholder = "رمز PIN",
                            isPassword = true,
                            errorMessage = pinError,
                            leadingIcon = Icons.Filled.Lock,
                            testTag = "pin_input"
                        )
                    }
                },
                confirmButton = {
                    PrimaryButton(
                        text = "دخول",
                        onClick = {
                            if (pinInput == showPinDialogForProfile?.pinCode) {
                                val prof = showPinDialogForProfile!!
                                showPinDialogForProfile = null
                                pinInput = ""
                                viewModel.selectProfile(prof)
                            } else {
                                pinError = "رمز PIN غير صحيح"
                            }
                        },
                        modifier = Modifier.height(Dimens.InputHeight)
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showPinDialogForProfile = null }) {
                        Text("إلغاء", color = TextSecondary)
                    }
                }
            )
        }

        // Add Profile Dialog
        if (showAddProfileDialog) {
            AlertDialog(
                onDismissRequest = { showAddProfileDialog = false },
                containerColor = DarkNavyCard,
                title = {
                    Text(
                        text = "إضافة ملف شخصي جديد",
                        style = Typography.headlineMedium.copy(color = GoldPrimary)
                    )
                },
                text = {
                    Column {
                        AppTextField(
                            value = newProfileName,
                            onValueChange = { newProfileName = it },
                            placeholder = "اسم الملف (مثلاً: أحمد)",
                            leadingIcon = Icons.Filled.Person,
                            testTag = "new_profile_name_input"
                        )
                    }
                },
                confirmButton = {
                    PrimaryButton(
                        text = "حفظ",
                        onClick = {
                            if (newProfileName.isNotBlank()) {
                                viewModel.createProfile(newProfileName.trim(), isNewProfileKids, null)
                                newProfileName = ""
                                showAddProfileDialog = false
                            }
                        }
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showAddProfileDialog = false }) {
                        Text("إلغاء", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileCard(
    profile: ProfileEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .tvFocusable(shape = CircleShape, onEnterClick = onClick)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(Dimens.Space8)
            .testTag("profile_item_${profile.id}")
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (profile.isKids) {
                            listOf(Color(0xFF38BDF8), DarkNavyCard)
                        } else {
                            listOf(GoldPrimary, DarkNavyCard)
                        }
                    )
                )
                .border(2.dp, if (profile.isKids) Color(0xFF38BDF8) else GoldPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (profile.isKids) Icons.Filled.ChildCare else Icons.Filled.Person,
                contentDescription = profile.name,
                tint = Color.White,
                modifier = Modifier.size(52.dp)
            )

            if (profile.pinCode != null) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC0E1422))
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "PIN Protected",
                        tint = GoldPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.Space12))

        Text(
            text = profile.name,
            style = Typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        if (profile.isKids) {
            Text(
                text = "أطفال",
                style = Typography.labelSmall.copy(color = Color(0xFF38BDF8))
            )
        }
    }
}
