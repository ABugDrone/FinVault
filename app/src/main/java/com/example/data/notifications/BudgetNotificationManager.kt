package com.example.data.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.security.CryptoManager
import java.util.Locale

class BudgetNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "finvault_budget_alerts"
        const val CHANNEL_NAME = "FinVault Budget Alerts"
        const val CHANNEL_DESCRIPTION = "High-priority alerts dispatched when approaching threshold (e.g. 85%) or exceeding category caps"
    }

    init {
        createNotificationChannel()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
                enableLights(true)
                lightColor = Color.rgb(255, 68, 88)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Checks if the app has permission to post notifications.
     * On Android 13+ (API 33+), checks Manifest.permission.POST_NOTIFICATIONS.
     * Also verifies system-wide notification enablement.
     */
    fun hasNotificationPermission(): Boolean {
        val systemEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        if (!systemEnabled) return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Dispatches a budget notification alert
     * @param category The financial category
     * @param spent Total spent this month
     * @param limit Monthly budget target
     * @param isExceeded True if spent >= limit
     * @param currencySymbol Current legal jurisdiction tender symbol
     */
    fun sendBudgetAlert(
        category: String,
        spent: Double,
        limit: Double,
        isExceeded: Boolean,
        currencySymbol: String = CryptoManager.activeCurrencySymbol
    ) {
        if (!hasNotificationPermission()) return

        val percent = if (limit > 0) ((spent / limit) * 100).toInt() else 100
        val title = if (isExceeded) {
            "🚨 Budget Cap Exceeded: $category"
        } else {
            "⚠️ Budget Threshold Warning: $category ($percent%)"
        }

        val spentStr = String.format(Locale.US, "%,.2f", spent)
        val limitStr = String.format(Locale.US, "%,.2f", limit)

        val content = if (isExceeded) {
            val overAmount = String.format(Locale.US, "%,.2f", spent - limit)
            "Category spend reached $currencySymbol$spentStr of $currencySymbol$limitStr cap (Exceeded by $currencySymbol$overAmount)!"
        } else {
            val remaining = String.format(Locale.US, "%,.2f", (limit - spent).coerceAtLeast(0.0))
            "Spend is at $percent% ($currencySymbol$spentStr / $currencySymbol$limitStr). $currencySymbol$remaining remaining before limit."
        }

        val notificationId = ("budget_" + category.lowercase(Locale.ROOT)).hashCode()

        // PendingIntent to launch app directly into the budgets view
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_NAVIGATE_TO", "budgets")
            putExtra("EXTRA_CATEGORY", category)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alertColor = if (isExceeded) 0xFFF43F5E.toInt() else 0xFFF59E0B.toInt()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_budget_alert)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .bigText(content)
                    .setSummaryText(if (isExceeded) "Exceeded Category Cap" else "Approaching Cap ($percent%)")
            )
            .setColor(alertColor)
            .setColorized(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_stat_budget_alert,
                "Review Budgets",
                pendingIntent
            )

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Permission revoked or not granted at runtime
        }
    }

    /**
     * Test notification dispatch to verify alerts, channels, sound, and vibration.
     */
    fun sendTestNotification(
        category: String = "Dining & Leisure",
        isExceeded: Boolean = false,
        currencySymbol: String = CryptoManager.activeCurrencySymbol
    ) {
        val limit = 350.0
        val spent = if (isExceeded) 412.50 else 305.00
        sendBudgetAlert(
            category = category,
            spent = spent,
            limit = limit,
            isExceeded = isExceeded,
            currencySymbol = currencySymbol
        )
    }
}

