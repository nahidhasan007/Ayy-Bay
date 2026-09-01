package com.ayybay.app.alarm

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayybay.app.data.repository.AlarmRepositoryImpl.Companion.EXTRA_ALARM_ID
import com.ayybay.app.domain.model.Alarm
import com.ayybay.app.domain.repository.AlarmRepository
import com.ayybay.app.ui.theme.IslamicGreenDark
import com.ayybay.app.ui.theme.MVIAPPTheme
import com.ayybay.app.ui.theme.NightBlue
import org.koin.android.ext.android.inject

/** Full-screen ringing UI, shown over the lock screen via a full-screen-intent notification. */
class AlarmRingActivity : ComponentActivity() {

    private val alarmRepository: AlarmRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOverLockScreen()

        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)

        setContent {
            var alarm by remember { mutableStateOf<Alarm?>(null) }
            LaunchedEffect(alarmId) {
                alarm = alarmRepository.getAlarmById(alarmId)
            }

            MVIAPPTheme {
                AlarmRingContent(
                    alarm = alarm,
                    onStop = {
                        AlarmRingActions.stop(this@AlarmRingActivity)
                        finish()
                    },
                    onSnooze = {
                        AlarmRingActions.snooze(this@AlarmRingActivity, alarmId)
                        finish()
                    }
                )
            }
        }
    }

    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )
        (getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager)?.requestDismissKeyguard(this, null)
    }
}

@Composable
private fun AlarmRingContent(
    alarm: Alarm?,
    onStop: () -> Unit,
    onSnooze: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NightBlue, IslamicGreenDark))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Alarm,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = alarm?.let { "%02d:%02d".format(it.hour, it.minute) } ?: "--:--",
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold
            )
            if (!alarm?.label.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = alarm!!.label, color = Color.White.copy(alpha = 0.85f), fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = onStop,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = IslamicGreenDark)
            ) {
                Text("Stop", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onSnooze,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Icon(Icons.Default.Snooze, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Snooze 10 min", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }
}
