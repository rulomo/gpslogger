package com.mendhak.gpslogger.common.network

import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager

/*
  Android API levels 16 to 21 have TLS *supported* but not *enabled*
  This asinine class exists purely to override the default socket creation
  and enable all existing supported protocols.
 */

//class PreLollipopTLSSocketFactory @Throws(KeyManagementException::class, NoSuchAlgorithmException::class)
class PreLollipopTLSSocketFactory
    constructor(trustManagers: Array<TrustManager>) : SSLSocketFactory() {

    private val delegate: SSLSocketFactory

    init {
        val context = SSLContext.getInstance("TLS")
        context.init(null, trustManagers, null)
        delegate = context.socketFactory
    }

    override fun getDefaultCipherSuites(): Array<String> {
        return delegate.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return delegate.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket {
        return enableTLSOnSocket(delegate.createSocket())
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int): Socket {
        return enableTLSOnSocket(delegate.createSocket(host, port))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort))
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket {
        return enableTLSOnSocket(delegate.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort))
    }

    private fun enableTLSOnSocket(socket: Socket): Socket {
        if (socket != null && socket is SSLSocket) {
            socket.enabledProtocols = socket.supportedProtocols
        }
        return socket
    }
}