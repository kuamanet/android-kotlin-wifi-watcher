package net.kuama.wifiMonitor.implementation

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import net.kuama.wifiMonitor.WifiListener

/**
 * From Android Q on, most of the Wi-Fi-related classes and properties have been deprecated
 * https://developer.android.com/about/versions/10/behavior-changes-10
 *
 * From now on, to observe connectivity changes we should register a
 * [ConnectivityManager.NetworkCallback] implementation
 */
@TargetApi(Build.VERSION_CODES.N)
internal class AndroidQWifiListener(context: Context) : WifiListener {

    /**
     * Callback to propagate the "Wi-Fi connected" state change
     */
    var onChange: (() -> Unit)? = null

    /**
     * Registers the network callback
     */
    private val startImplementation = {
        (context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .registerDefaultNetworkCallback(networkCallback)
    }

    /**
     * Unregisters the network callback
     */
    private val stopImplementation = {
        (context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            .unregisterNetworkCallback(networkCallback)
    }

    /**
     * Simple [ConnectivityManager.NetworkCallback] implementation
     * will invoke the onChange on each onCapabilitiesChanged
     */
    private val networkCallback = object :
        ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            onChange?.invoke()
        }
    }

    override fun stop() {
        this.onChange = null
        stopImplementation()
    }

    override fun start(onChange: () -> Unit) {
        this.onChange = onChange
        startImplementation()
    }
}
