package app.pwhs.universalinstaller.util

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Utility to monitor permission status changes while the user is in system settings.
 *
 * When a permission is granted, it automatically brings the app back to the foreground
 * using [Intent.FLAG_ACTIVITY_REORDER_TO_FRONT]. This provides a seamless "magic" return
 * experience without the user having to manually press the back button.
 */
object PermissionMonitor {
    private var job: Job? = null

    /**
     * Start polling for a permission change.
     *
     * @param context Current context
     * @param check Lambda that returns true when the desired permission is granted
     */
    fun start(context: Context, check: () -> Boolean) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            Timber.d("PermissionMonitor: Started polling")
            // Give the user 1.5 seconds to reach the settings screen before we start checking
            delay(1500)
            
            var attempts = 0
            // Poll every 500ms for up to 3 minutes (360 attempts)
            while (attempts < 360) {
                if (check()) {
                    Timber.d("PermissionMonitor: Permission granted! Bringing app to front.")
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                    launchIntent?.apply {
                        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }?.let {
                        context.startActivity(it)
                    }
                    break
                }
                delay(500)
                attempts++
            }
            job = null
        }
    }

    /**
     * Stop active monitoring. Should be called when the app resumes or a grant is detected
     * via standard activity result pathways.
     */
    fun stop() {
        job?.cancel()
        job = null
    }
}
