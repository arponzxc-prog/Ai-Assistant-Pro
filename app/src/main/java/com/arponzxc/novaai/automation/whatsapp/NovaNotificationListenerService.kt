package com.arponzxc.novaai.automation.whatsapp

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.arponzxc.novaai.JarvisApp

class NovaNotificationListenerService : NotificationListenerService() {

    companion object {
        private const val TAG = "NovaNotifListener"

        /**
         * Checks if the NotificationListenerService permission has been granted by user.
         */
        fun isPermissionGranted(context: Context): Boolean {
            val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(context)
            return enabledListeners.contains(context.packageName)
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i(TAG, "NovaNotificationListenerService connected.")
        try {
            JarvisApp.instance.whatsAppAutomationManager.updateServiceConnected(true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update service connected state: ${e.message}")
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w(TAG, "NovaNotificationListenerService disconnected.")
        try {
            JarvisApp.instance.whatsAppAutomationManager.updateServiceConnected(false)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update service disconnected state: ${e.message}")
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val pkg = sbn.packageName
        if (pkg == "com.whatsapp" || pkg == "com.whatsapp.w4b") {
            try {
                JarvisApp.instance.whatsAppAutomationManager.onNotificationReceived(sbn)
            } catch (e: Exception) {
                Log.e(TAG, "Error handling WhatsApp notification: ${e.message}", e)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        // No-op for removed notifications
    }
}
