package com.example.coconut.oauth2

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.webkit.URLUtil
import androidx.browser.customtabs.CustomTabsIntent
import com.example.coconut.Constant
import com.example.coconut.MyApplication
import com.google.gson.GsonBuilder
import net.openid.appauth.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import com.example.coconut.R
import com.example.coconut.model.api.UserInfoAPI
import com.example.coconut.util.MyPreference


class AuthRepo(
    private var app: MyApplication,
    private var pref: MyPreference
) {

    private val TAG = AuthRepo::class.simpleName
    private var authService: AuthorizationService? =
        AuthorizationService(app, AppAuthConfiguration.Builder().build())
    private var loginListener: AuthLoginListener? = null
    private var authState: AuthState? = null
    private var userInfoUrl: String? = null
    private var loginLock: Semaphore? = Semaphore(1)
    var clientId: String? = null
    var redirectUri: String? = null
    var authScope: String? = null


    private fun isConfigured(): Boolean {
        return authState != null && authState!!.authorizationServiceConfiguration != null && clientId != null && redirectUri != null && authScope != null
    }

    private fun isAuthorized(): Boolean {
        return authState != null && authState!!.isAuthorized
    }

    private fun lockLogins() {
        try {
            loginLock!!.acquire()
        } catch (ex: InterruptedException) {
            throw RuntimeException("Unexpected interrupt", ex)
        }
    }

    private fun unlockLogins() {
        loginLock!!.release()
    }

    fun login(loginListener: AuthLoginListener) {
        Log.i(TAG, "login called")
        lockLogins()
        if (isAuthorized()) {
            unlockLogins()
            return
        }
        this.loginListener = loginListener
        loginListener.onStart(this@AuthRepo, AuthEvent.AUTH_LOGIN_START)
        if (!isConfigured()) {
            startServiceConfig()
        } else {
            startUserAuth()
        }
    }

    private fun startServiceConfig() {
        Log.i(TAG, "Starting service config")
        val discoveryEndpoint: String = app.getString(R.string.discovery_endpoint)
        if (discoveryEndpoint.trim { it <= ' ' }.isEmpty() || !URLUtil.isValidUrl(
                discoveryEndpoint
            )
        ) {
            Log.i(TAG, "Using static service config")
            val serviceConfig = AuthorizationServiceConfiguration(
                Uri.parse(app.getString(R.string.authorization_endpoint)),
                Uri.parse(app.getString(R.string.token_endpoint))
            )
            authState = AuthState(serviceConfig)
            userInfoUrl = app.getString(R.string.user_info_endpoint)
            finishServiceConfig()
        } else {
            Log.i(TAG, "Using discovery service config")
            val discoveryUri = Uri.parse(discoveryEndpoint)
            loginListener!!.onEvent(this@AuthRepo, AuthEvent.AUTH_SERVICE_DISCOVERY_START)
            AuthorizationServiceConfiguration.fetchFromUrl(
                discoveryUri
            ) { config: AuthorizationServiceConfiguration?, ex: AuthorizationException? ->
                finishServiceDiscovery(
                    config,
                    ex
                )
            }
        }
    }

    private fun finishServiceDiscovery(
        config: AuthorizationServiceConfiguration?,
        ex: AuthorizationException?
    ) {
        if (config == null) {
            failLogin(AuthException("Failed to retrieve authorization service discovery document"))
            return
        }
        authState = AuthState(config)
        val discovery = config.discoveryDoc
        userInfoUrl = discovery!!.userinfoEndpoint.toString()
        loginListener!!.onEvent(this@AuthRepo, AuthEvent.AUTH_SERVICE_DISCOVERY_FINISH)
        finishServiceConfig()
    }

    private fun finishServiceConfig() {
        val test: URL
        try {
            test = URL(userInfoUrl)
            if (!userInfoUrl!!.endsWith("/")) userInfoUrl += "/"
        } catch (urlEx: MalformedURLException) {
            userInfoUrl = null
        }
        Log.i(TAG, "Finishing service config")
        Log.i(
            TAG,
            "  authorization endpoint: " + authState!!.authorizationServiceConfiguration!!.authorizationEndpoint
        )
        Log.i(
            TAG,
            "  token endpoint: " + authState!!.authorizationServiceConfiguration!!.tokenEndpoint
        )
        Log.i(TAG, "  user info endpoint: $userInfoUrl")
        startClientConfig()
    }

    private fun startClientConfig() {
        Log.i(TAG, "Starting client config")
        clientId = app.getString(R.string.client_id)
        redirectUri = app.getString(R.string.redirect_uri)
        authScope = app.getString(R.string.authorization_scope)
        finishClientConfig()
    }

    private fun finishClientConfig() {
        Log.i(TAG, "Finishing client config")
        Log.i(TAG, "  client id: $clientId")
        Log.i(TAG, "  redirect uri: $redirectUri")
        Log.i(TAG, "  auth scope: $authScope")
        startUserAuth()
    }

    private fun startUserAuth() {
        Log.i(TAG, "Starting user auth")
        loginListener!!.onEvent(this@AuthRepo, AuthEvent.AUTH_USER_AUTH_START)

        // may need to do this off UI thread?
        val authRequestBuilder = AuthorizationRequest.Builder(
            authState!!.authorizationServiceConfiguration!!,
            clientId!!,
            ResponseTypeValues.CODE,
            Uri.parse(redirectUri)
        ).setScope(authScope!!)

        val authRequest = authRequestBuilder.build()
        val intentBuilder: CustomTabsIntent.Builder =
            authService!!.createCustomTabsIntentBuilder(authRequest.toUri())
        intentBuilder.setToolbarColor(app.getColorValue(R.color.colorAccent))
        val authIntent: CustomTabsIntent = intentBuilder.build()
        val intent = authService!!.getAuthorizationRequestIntent(authRequest, authIntent)
        loginListener!!.onUserAgentRequest(this@AuthRepo, intent)
    }

    fun notifyUserAgentResponse(data: Intent?, returnCode: Int) {
        Log.i(TAG, "notifyUserAgentResponse returnCode : ${returnCode == Constant.RC_AUTH}")
        if (returnCode != Constant.RC_AUTH) {
            failLogin(AuthException("User authorization was cancelled"))
            return
        }
        val resp = AuthorizationResponse.fromIntent(data!!)
        val ex = AuthorizationException.fromIntent(data)
        if (resp == null) {
            failLogin(AuthException("User authorization failed"))
            return
        } else {
            authState!!.update(resp, ex)
            finishUserAuth()
        }
    }

    private fun finishUserAuth() {
        Log.i(TAG, "Finishing user auth")
        loginListener!!.onEvent(this@AuthRepo, AuthEvent.AUTH_USER_AUTH_FINISH)
        startCodeExchange()
    }

    private fun startCodeExchange() {
        Log.i(TAG, "Starting code exchange")
        loginListener!!.onEvent(this@AuthRepo, AuthEvent.AUTH_CODE_EXCHANGE_START)

        val resp = authState!!.lastAuthorizationResponse
        authService!!.performTokenRequest(
            resp!!.createTokenExchangeRequest()
        ) { response: TokenResponse?, ex: AuthorizationException? ->
            onTokenRequestCompleted(
                response,
                ex
            )
        }
    }

    private fun onTokenRequestCompleted(resp: TokenResponse?, ex: AuthorizationException?) {
        if (resp == null) {
            failLogin(AuthException(ex!!.message!!))
            return
        }
        Log.i(TAG, "onTokenRequestCompleted > accessToken=${resp.accessToken}")
        Log.i(TAG, "onTokenRequestCompleted > refreshToken=${resp.refreshToken}")

        saveTokens(resp)

        authState!!.update(resp, ex)
        finishCodeExchange()
    }

    private fun finishCodeExchange() {
        Log.i(TAG, "Finishing code exchange")
        loginListener!!.onEvent(this@AuthRepo, AuthEvent.AUTH_CODE_EXCHANGE_FINISH)
        startUserInfo()
    }

    private fun startUserInfo() {
        Log.i(TAG, "Starting user info")
        loginListener!!.onEvent(this@AuthRepo, AuthEvent.AUTH_USER_INFO_START)
        fetchUserInfo(object : UserInfoCallback {
            override fun call(userInfo: UserInfo?, ex: AuthException?) {
                onUserInfoCompleted(
                    userInfo,
                    ex
                )
            }
        })
    }

    private fun onUserInfoCompleted(userInfo: UserInfo?, ex: AuthException?) {
        if (userInfo == null) Log.i(TAG, "Unable to obtain user info.")
        finishUserInfo()
    }


    private fun finishUserInfo() {
        Log.i(TAG, "Finishing user info")
        loginListener!!.onEvent(this@AuthRepo, AuthEvent.AUTH_USER_INFO_FINISH)
        finishLogin()
    }

    private fun failLogin(ex: AuthException) {
        Log.i(TAG, "Failing login")
        loginListener!!.onFailure(this@AuthRepo, AuthEvent.AUTH_LOGIN_FAILURE, ex)
        unlockLogins()
    }

    private fun finishLogin() {
        Log.i(TAG, "Finishing login")

        // onSuccess에서 화면 넘기기
        Log.i(TAG, "finishLogin> getUserInfo: ${getUserInfo()?.toString()}")
        loginListener!!.onSuccess(this@AuthRepo, AuthEvent.AUTH_LOGIN_SUCCESS, getUserInfo())

        // 유저 정보 서버에 전달하기

        unlockLogins()
    }

    private var userInfo: UserInfo? = null

    private fun createUserInfoAPI(): UserInfoAPI {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        val authClient = OkHttpClient().newBuilder()
            .addInterceptor(getAccessTokenInterceptor())
            .addInterceptor(getApiKeyInterceptor())
            .addInterceptor(logger)
            .build()
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com")
            .client(authClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(UserInfoAPI::class.java)
    }

    @SuppressLint("StaticFieldLeak")
    inner class UserInfoTask(private val callback: UserInfoCallback) :
        AsyncTask<Void?, Void?, UserInfo?>() {

        override fun doInBackground(vararg params: Void?): UserInfo? {
            val userInfoAPI: UserInfoAPI = createUserInfoAPI()
            val call: Call<UserInfoResult> = userInfoAPI.getUserInfo()
            try {
                val response = call.execute()
                Log.i(TAG, "UserInfoTask response.isSuccessful() : " + response.isSuccessful)
                if (response.isSuccessful) {
                    val result = response.body()
                    userInfo = UserInfo(
                        result?.mFamilyName, result?.mGivenName,
                        result?.mPicture, result?.mEmail
                    )
                    Log.i(TAG, "UserInfoTask doInBackground : " + userInfo.toString())
                } else {
                    userInfo = null
                }
            } catch (e: IOException) {
                Log.i(TAG, "UserInfoTask doInBackground exception : " + e.message)
                userInfo = null
            }
            return userInfo
        }

        override fun onPostExecute(userInfo: UserInfo?) {
            if (userInfo == null) {
                callback.call(null, AuthException("Unable to retrieve user info"))
                return
            }
            callback.call(userInfo, null)
        }

    }

    private fun fetchUserInfo(callback: UserInfoCallback?) {
        if (callback == null) throw RuntimeException("fetchUserInfo: null callback")
        if (!isAuthorized()) {
            callback.call(null, AuthException("Not authorized"))
            return
        }
        UserInfoTask(callback).execute()
    }

    private fun getUserInfo(): UserInfo? {
        if (!isAuthorized()) return null
        if (userInfo != null) return userInfo
        val fetchComplete = CountDownLatch(1)
        fetchUserInfo(object : UserInfoCallback {
            override fun call(userInfo: UserInfo?, ex: AuthException?) {
                this@AuthRepo.userInfo = userInfo
                fetchComplete.countDown()
            }
        })
        val complete: Boolean = try {
            fetchComplete.await(5000, TimeUnit.MILLISECONDS)
        } catch (ex: InterruptedException) {
            false
        }
        if (!complete) userInfo = null
        return userInfo
    }

    fun logout(logoutListener: AuthLogoutListener) {
        lockLogins()
        if (!isAuthorized()) {
            unlockLogins()
            return
        }
        logoutListener.onStart(this@AuthRepo, AuthEvent.AUTH_LOGOUT_START)
        if (isConfigured()) {
            authState = AuthState(authState!!.authorizationServiceConfiguration!!)
            userInfo = null
        } else {
            authState = null
            clientId = null
            redirectUri = null
            userInfoUrl = null
            userInfo = null
        }
        logoutListener.onSuccess(this@AuthRepo, AuthEvent.AUTH_LOGOUT_SUCCESS)
        unlockLogins()
    }

    fun getApiKeyInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            val url = request.url().newBuilder()
                .addQueryParameter("key", app.getString(R.string.api_key)).build()
            request = request.newBuilder()
                .url(url)
                .header("X-Android-Package", app.packageName)
                .header("X-Android-Cert", app.getSignature()!!)
                .build()
            chain.proceed(request)
        }
    }

    private var accessToken: String? = null

    // dangerous; do not call on UI thread.
    private fun getAccessToken(): String? {
        if (!isAuthorized()) return null
        val actionComplete = CountDownLatch(1)
        authState!!.performActionWithFreshTokens(
            authService!!
        ) { authToken: String?, idToken: String?, ex: AuthorizationException? ->
            accessToken = authToken
            actionComplete.countDown()
        }
        val complete: Boolean = try {
            actionComplete.await(5000, TimeUnit.MILLISECONDS)
        } catch (ex: InterruptedException) {
            false
        }
        if (!complete) accessToken = null
        val token = accessToken
        accessToken = null
        return token
    }

    private fun getAccessTokenInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            request = request.newBuilder()
                .header("X-Android-Package", app.packageName)
                .header("X-Android-Cert", app.getSignature()!!)
                .header("Authorization", "Bearer " + getAccessToken())
                .build()
            Log.i(TAG, "AccessToken: " + getAccessToken())
            chain.proceed(request)
        }
    }

    private fun saveTokens(resp: TokenResponse) {
        pref.accessToken = resp.accessToken
        pref.refreshToken = resp.refreshToken
    }

}