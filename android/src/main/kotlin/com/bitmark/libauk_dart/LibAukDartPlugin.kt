package com.bitmark.libauk_dart

import android.content.Context
import androidx.annotation.NonNull
import com.bitmark.libauk.LibAuk

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.reactivex.disposables.CompositeDisposable
import org.web3j.crypto.RawTransaction
import java.math.BigInteger
import java.util.*

/** LibaukDartPlugin */
class LibAukDartPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var disposables: CompositeDisposable

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "libauk_dart")
        channel.setMethodCallHandler(this)
        disposables = CompositeDisposable()
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "createKey" -> {
                createKey(call, result)
            }
            "importKey" -> {
                importKey(call, result)
            }
            "isWalletCreated" -> {
                isWalletCreated(call, result)
            }
            "getName" -> {
                getName(call, result)
            }
            "updateName" -> {
                updateName(call, result)
            }
            "getETHAddress" -> {
                getETHAddress(call, result)
            }
            "signPersonalMessage" -> {
                signPersonalMessage(call, result)
            }
            "signTransaction" -> {
                signTransaction(call, result)
            }
            "exportMnemonicWords" -> {
                exportMnemonicWords(call, result)
            }
            "removeKeys" -> {
                removeKeys(call, result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        disposables.dispose()
    }

    private fun createKey(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val name: String = call.argument("name") ?: ""
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .createKey(name)
            .subscribe({
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["msg"] = "createKey success"
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("createKey error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun importKey(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val name: String = call.argument("name") ?: ""
        val words: String = call.argument("words") ?: ""
        val dateInMili: Long? = call.argument("date")
        val date: Date = dateInMili?.let { Date(it) } ?: Date()
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .importKey(words.split(" "), name, date)
            .subscribe({
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["msg"] = "importKey success"
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("importKey error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun isWalletCreated(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .isWalletCreated()
            .subscribe({
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["msg"] = "isWalletCreated success"
                rev["data"] = it
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("isWalletCreated error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun getName(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .getName()
            .subscribe({
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["msg"] = "getName success"
                rev["data"] = it
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("getName error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun updateName(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val name: String = call.argument("name") ?: ""
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .updateName(name)
            .subscribe({
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["msg"] = "updateName success"
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("updateName error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun getETHAddress(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .getETHAddress()
            .subscribe({ address ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["msg"] = "getETHAddress success"
                rev["data"] = address
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("getETHAddress error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun signPersonalMessage(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val message: ByteArray = call.argument("message") ?: error("missing message")
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .signPersonalMessage(message)
            .subscribe({ sigData ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["msg"] = "getETHAddress success"
                rev["data"] = "0x" + sigData.r.toHex() + sigData.s.toHex() + sigData.v.toHex()
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("signPersonalMessage error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun signTransaction(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val nonce: String = call.argument("nonce") ?: ""
        val gasPrice: String = call.argument("gasPrice") ?: ""
        val gasLimit: String = call.argument("gasLimit") ?: ""
        val to: String = call.argument("to") ?: error("missing recipient")
        val value: String = call.argument("value") ?: "0"
        val data: String = call.argument("data") ?: ""
        val chainId: Long = call.argument("chainId") ?: 0L
        val rawTransaction = RawTransaction.createTransaction(
            BigInteger(nonce),
            BigInteger(gasPrice),
            BigInteger(gasLimit),
            to,
            BigInteger(value),
            data
        )

        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .signTransaction(rawTransaction, chainId)
            .subscribe({ bytes ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["msg"] = "signTransaction success"
                rev["data"] = bytes
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("signTransaction error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun exportMnemonicWords(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .exportMnemonicWords()
            .subscribe({ words ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["msg"] = "exportMnemonicWords success"
                rev["data"] = words
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("exportMnemonicWords error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun removeKeys(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .removeKeys()
            .subscribe({
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["msg"] = "removeKeys success"
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("removeKeys error", it.message, it)
            })
            .let { disposables.add(it) }
    }
}

private val HEX_CHARS = "0123456789abcdef".toCharArray()

fun ByteArray.toHex(): String {
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}