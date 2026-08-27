package com.example.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Centralized, reactive network connectivity monitor for Android.
 * Continuously tracks physical network changes and performs actual internet reachability verification.
 */
class NetworkMonitor private constructor(context: Context) {

    private val applicationContext = context.applicationContext
    private val connectivityManager =
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        // 1. Immediate startup check
        checkInitialConnectivity()

        // 2. Register continuous network callback
        registerNetworkCallback()
    }

    private fun checkInitialConnectivity() {
        scope.launch {
            val hasNetwork = isNetworkConnectedAccordingToSystem()
            if (hasNetwork) {
                val canReachInternet = verifyActualInternetAccess()
                _isOnline.value = canReachInternet
            } else {
                _isOnline.value = false
            }
            Log.i(TAG, "Initial network connectivity status checked: isOnline=${_isOnline.value}")
        }
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network onAvailable triggered. Verifying internet reachability...")
                scope.launch {
                    _isChecking.value = true
                    val hasInternet = verifyActualInternetAccess()
                    _isChecking.value = false
                    _isOnline.value = hasInternet
                    Log.i(TAG, "Network state updated to: isOnline=$hasInternet")
                }
            }

            override fun onLost(network: Network) {
                Log.d(TAG, "Network onLost triggered. System disconnected from network.")
                scope.launch {
                    val remainingConnected = isNetworkConnectedAccordingToSystem()
                    if (remainingConnected) {
                        _isOnline.value = verifyActualInternetAccess()
                    } else {
                        _isOnline.value = false
                    }
                    Log.i(TAG, "Network state updated to: isOnline=${_isOnline.value}")
                }
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val isValidated =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                val hasInternet =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

                Log.d(TAG, "onCapabilitiesChanged: hasInternet=$hasInternet, isValidated=$isValidated")

                if (hasInternet && isValidated) {
                    _isOnline.value = true
                } else if (!hasInternet) {
                    _isOnline.value = false
                } else {
                    // recheck actively
                    scope.launch {
                        _isOnline.value = verifyActualInternetAccess()
                    }
                }
            }
        }

        try {
            connectivityManager.registerNetworkCallback(request, networkCallback!!)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register NetworkCallback: ${e.message}", e)
        }
    }

    private fun isNetworkConnectedAccordingToSystem(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Verifies actual socket connectivity to Google DNS (8.8.8.8:53) to confirm real internet access.
     */
    suspend fun verifyActualInternetAccess(): Boolean = withContext(Dispatchers.IO) {
        if (!isNetworkConnectedAccordingToSystem()) return@withContext false
        
        try {
            val socket = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)
            socket.connect(socketAddress, 1500) // 1.5 second timeout
            socket.close()
            true
        } catch (e: IOException) {
            Log.w(TAG, "Internet verification socket test failed: ${e.message}")
            false
        } catch (e: Exception) {
            false
        }
    }

    fun forceCheck() {
        scope.launch {
            _isChecking.value = true
            val online = verifyActualInternetAccess()
            _isChecking.value = false
            _isOnline.value = online
        }
    }

    fun unregister() {
        networkCallback?.let {
            try {
                connectivityManager.unregisterNetworkCallback(it)
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering network callback: ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "NetworkMonitor"

        @Volatile
        private var INSTANCE: NetworkMonitor? = null

        fun getInstance(context: Context): NetworkMonitor {
            return INSTANCE ?: synchronized(this) {
                val instance = NetworkMonitor(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
