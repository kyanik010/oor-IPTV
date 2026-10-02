package com.example.data.activation

import android.content.Context
import android.provider.Settings
import com.example.data.model.DeviceActivationInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DeviceActivationManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("noor_iptv_activation", Context.MODE_PRIVATE)

    private val _activationState = MutableStateFlow(getActivationInfo())
    val activationState: StateFlow<DeviceActivationInfo> = _activationState.asStateFlow()

    fun getDeviceId(): String {
        var deviceId = prefs.getString("device_id", null)
        if (deviceId == null) {
            val androidId = try {
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            } catch (e: Exception) {
                null
            }
            deviceId = if (!androidId.isNullOrBlank() && androidId != "9774d56d682e549c") {
                androidId.uppercase()
            } else {
                UUID.randomUUID().toString().replace("-", "").take(16).uppercase()
            }
            prefs.edit().putString("device_id", deviceId).apply()
        }
        return deviceId
    }

    fun getActivationInfo(): DeviceActivationInfo {
        val deviceId = getDeviceId()
        val isActivated = prefs.getBoolean("is_activated", false)
        val activationCode = prefs.getString("activation_code", "") ?: ""

        // Calculate 7-day trial
        val firstLaunchTime = prefs.getLong("first_launch_time", 0L)
        val now = System.currentTimeMillis()
        val trialDurationMs = 7L * 24 * 60 * 60 * 1000L

        val effectiveFirstLaunch = if (firstLaunchTime == 0L) {
            prefs.edit().putLong("first_launch_time", now).apply()
            now
        } else {
            firstLaunchTime
        }

        val trialExpires = effectiveFirstLaunch + trialDurationMs
        val remainingMs = (trialExpires - now).coerceAtLeast(0L)
        val remainingDays = (remainingMs / (24 * 60 * 60 * 1000L)).toInt()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val expirationDateText = if (isActivated) {
            val activatedExpiry = prefs.getLong("activated_expiry", now + (365L * 24 * 60 * 60 * 1000L))
            dateFormat.format(Date(activatedExpiry))
        } else {
            dateFormat.format(Date(trialExpires))
        }

        return DeviceActivationInfo(
            deviceId = deviceId,
            activationCode = activationCode,
            isActivated = isActivated || remainingDays > 0,
            trialDaysRemaining = if (isActivated) 365 else remainingDays,
            expirationDateText = expirationDateText,
            planName = if (isActivated) "Premium VIP (1 Year)" else "Free Trial (7 Days)"
        )
    }

    suspend fun activateWithCode(code: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val trimmed = code.trim().uppercase()
        if (trimmed.length < 4) {
            return@withContext Result.failure(Exception("Activation code must be at least 4 characters"))
        }

        // Verification logic (with Supabase or code structure)
        val oneYearMs = 365L * 24 * 60 * 60 * 1000L
        prefs.edit()
            .putBoolean("is_activated", true)
            .putString("activation_code", trimmed)
            .putLong("activated_expiry", System.currentTimeMillis() + oneYearMs)
            .apply()

        _activationState.value = getActivationInfo()
        Result.success(true)
    }
}
