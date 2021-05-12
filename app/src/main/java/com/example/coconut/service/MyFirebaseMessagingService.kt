package com.example.coconut.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.model.MyRepository
import com.example.coconut.model.request.chat.FcmTokenRequest
import com.example.coconut.ui.main.chat.inner.InnerChatActivity
import com.example.coconut.util.MyPreference
import com.example.coconut.util.toHTTPString
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val pref: MyPreference by inject()
    private val myRepository: MyRepository by inject()
    private val TAG = MyFirebaseMessagingService::class.java.simpleName

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.e(TAG, "Message data payload: " + remoteMessage.data)
            sendNotification(remoteMessage.data)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.e(TAG, "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.e(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]


    /**
     * Schedule async work using WorkManager.
     */
    private fun scheduleJob() {
        // [START dispatch_job]
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
//        WorkManager.getInstance().beginWith(work).enqueue()
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.e(TAG, "sendRegistrationTokenToServer($token)")

        // preference에 저장후
        pref.fcmToken = token

        // id와 함께 서버에 보낸다
        // 맨처음 설치된 단말기는 id값이 설정되어있지 않는다
        // 이 함수는 이미 설치된 단말기에서 token값이 변경되었을때 사용하기 위해 존재

        /**서버에 fcm과 id를 보내어 저장한다
        앱이 처음 설치되었고, token이 compromised, 가입한 적이 없는 유저일 경우
        => emailVerify()를 통해 서버에 전달 (LoginViewModel)

        앱이 처음 설치되었고, token이 compromised, 가입한 적이 있는 유저일 경우
        => loginCheck()를 통해 서버에 전달 (LoginViewModel)

        앱이 다시 실행되고, token이 compromised, pref.userID 값이 저장되어있을경우
        => sendFcmTokenToServer()를 통해 서버에 전달 (MyFirebaseMessagingService)

        앱이 다시 실행되고, pref.token 값이 null 일경우
        => fcmToken()를 통해 서버에 전달 (LogoActivity)
         */

        pref.userIdx?.let {
            sendFcmTokenToServer(it, pref.fcmToken)
        }

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param message FCM message body received.
     */
    private fun sendNotification(data: Map<String, String>) {
        Log.e(TAG, "sendNotification")
        val intent = Intent(this, InnerChatActivity::class.java).apply {
            putExtra(IntentID.CHAT_MODE, IntentID.CHAT_FROM_NOTIFICATION)
            putExtra(IntentID.CHAT_ROOM_ID, data["roomId"])
            putExtra(IntentID.CHAT_ROOM_PEOPLE_LIST, data["roomPeople"])

            /**
             * manifests 에서
             * InnerChatActivity 속에  android:noHistory="true" 속성 추가하면
             * 액티비티 스택에 쌓이지 않는다.
             * */
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        // 각 노티를 클릭했을때 requestCode 를 고유한 값으로 구분하지 않으면
        // 모두 동일한 값을 엑티비티로 전달하게 된다
        val pendingIntent = PendingIntent.getActivity(
            this, data["roomId"]!!.toInt() /* Request code */, intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val bitmapImage = Glide.with(this).asBitmap()
            .load(data["userImage"]?.toHTTPString())
            .transform(CenterCrop(), RoundedCorners(25))
            .submit().get()

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_coconut_round)
            .setContentTitle(data["title"])
            .setContentText("${data["who"]} : ${data["body"]}")
            .setLargeIcon(bitmapImage)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            /* data["roomId"] 값으로 notification id를 설정하면 그룹화 된다 */
            .setGroup(data["roomId"])
            .setGroupSummary(true)
            .setOnlyAlertOnce(false)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        data["roomId"]?.toInt()?.let {
            notificationManager.notify(
                it/* ID of notification */,
                notificationBuilder.build()
            )
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
    }

    private fun sendFcmTokenToServer(id: String, token: String?) {
        CompositeDisposable().add(
            (myRepository.sendFcmTokenToServer(
                FcmTokenRequest(id, token)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.run {
                        Log.e(TAG, "response : ${toString()}")
                    }
                }, {
                    Log.e(TAG, "response error, message : ${it.message}")
                }))
        )

    }
}