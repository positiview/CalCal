package com.example.calcal.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.calcal.MainActivity
import com.example.calcal.R

private const val NOTIFICATION_CHANNEL_ID = "ChronometerChannel"
class ChronometerService : Service() {
    private val startTime = SystemClock.elapsedRealtime()
    private val binder = ChronometerBinder()
    private var notificationManager: NotificationManager? = null
    private var notificationBuilder: NotificationCompat.Builder? = null
    private val notificationId = 1

    private val locationManager: LocationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // 위치가 변경될 때마다 호출됩니다.
            // 여기서 location 객체를 사용하여 필요한 작업을 수행하면 됩니다.
        }
    }

    inner class ChronometerBinder : Binder() {
        fun getElapsedTime(): Long {
            return SystemClock.elapsedRealtime() - startTime
        }
    }
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(notificationId, getNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
        stopForeground(false)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    /*override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 위치 접근 권한이 없는 경우, 요청을 하거나 작업을 중단해야 합니다.
            return START_NOT_STICKY
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000L,
            10f,
            locationListener
        )

        return START_STICKY
    }*/


    private fun getNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("openMapFragment", true)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Chronometer is running")
            .setContentText("Click to return to the app")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // 이 부분을 추가하여 알림이 종료되지 않도록 설정

        return notificationBuilder!!.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Chronometer Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}