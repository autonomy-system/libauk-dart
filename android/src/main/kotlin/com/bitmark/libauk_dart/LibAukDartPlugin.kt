package com.bitmark.libauk_dart

import android.content.Context
import androidx.annotation.NonNull
import com.bitmark.libauk.LibAuk

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.Sign
import java.io.File
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
            "getAccountDID" -> {
                getAccountDID(call, result)
            }
            "getAccountDIDSignature" -> {
                getAccountDIDSignature(call, result)
            }
            "getETHAddress" -> {
                getETHAddress(call, result)
            }
            "getETHAddressWithIndex" -> {
                getETHAddressWithIndex(call, result)
            }
            "ethSignPersonalMessage" -> {
                ethSignPersonalMessage(call, result)
            }
            "ethSignPersonalMessageWithIndex" -> {
                ethSignPersonalMessageWithIndex(call, result)
            }
            "ethSignMessage" -> {
                ethSignMessage(call, result)
            }
            "ethSignMessageWithIndex" -> {
                ethSignMessageWithIndex(call, result)
            }
            "ethSignTransaction" -> {
                ethSignTransaction(call, result)
            }
            "ethSignTransactionWithIndex" -> {
                ethSignTransactionWithIndex(call, result)
            }
            "ethSignTransaction1559" -> {
                ethSignTransaction1559(call, result)
            }
            "ethSignTransaction1559WithIndex" -> {
                ethSignTransaction1559WithIndex(call, result)
            }
            "encryptFile" -> {
                encryptFile(call, result)
            }
            "decryptFile" -> {
                decryptFile(call, result)
            }
            "exportMnemonicWords" -> {
                exportMnemonicWords(call, result)
            }
            "getTezosPublicKey" -> {
                getTezosPublicKey(call, result)
            }
            "getTezosPublicKeyWithIndex" -> {
                getTezosPublicKeyWithIndex(call, result)
            }
            "tezosSignMessage" -> {
                tezosSignMessage(call, result)
            }
            "tezosSignMessageWithIndex" -> {
                tezosSignMessageWithIndex(call, result)
            }
            "tezosSignTransaction" -> {
                tezosSignTransaction(call, result)
            }
            "tezosSignTransactionWithIndex" -> {
                tezosSignTransactionWithIndex(call, result)
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

    private fun getAccountDID(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")

        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .getAccountDID()
            .subscribe({ accountDID ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = accountDID
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("updateName error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun getAccountDIDSignature(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val message: String = call.argument("message") ?: ""
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .getAccountDIDSignature(message)
            .subscribe({ signature ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = signature
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
                rev["data"] = address
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("getETHAddress error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun getETHAddressWithIndex(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val index: Int = call.argument("index") ?: 0
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .getETHAddressWithIndex(index)
            .subscribe({ address ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = address
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("getETHAddress error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun ethSignMessage(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val message: ByteArray = call.argument("message") ?: error("missing message")
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .ethSignMessage(message, true)
            .subscribe({ sigData ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = "0x" + sigData.r.toHex() + sigData.s.toHex() + sigData.v.toHex()
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("signPersonalMessage error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun ethSignMessageWithIndex(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val message: ByteArray = call.argument("message") ?: error("missing message")
        val index: Int = call.argument("index") ?: 0
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .ethSignMessageWithIndex(message, true, index)
            .subscribe({ sigData ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = "0x" + sigData.r.toHex() + sigData.s.toHex() + sigData.v.toHex()
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("signMessage error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun ethSignPersonalMessage(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val message: ByteArray = call.argument("message") ?: error("missing message")
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .ethSignMessage(message.ethPersonalMessage(), false)
            .subscribe({ sigData ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = "0x" + sigData.r.toHex() + sigData.s.toHex() + sigData.v.toHex()
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("signPersonalMessage error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun ethSignPersonalMessageWithIndex(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val message: ByteArray = call.argument("message") ?: error("missing message")
        val index: Int = call.argument("index") ?: 0
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .ethSignMessageWithIndex(message.ethPersonalMessage(), false, index)
            .subscribe({ sigData ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = "0x" + sigData.r.toHex() + sigData.s.toHex() + sigData.v.toHex()
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("signPersonalMessage error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun ethSignTransaction(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val nonce: String = call.argument("nonce") ?: ""
        val gasPrice: String = call.argument("gasPrice") ?: ""
        val gasLimit: String = call.argument("gasLimit") ?: ""
        val to: String = call.argument("to") ?: error("missing recipient")
        val value: String = call.argument("value") ?: "0"
        val data: String = call.argument("data") ?: ""
        val chainId: Int = call.argument("chainId") ?: 0
        val rawTransaction = RawTransaction.createTransaction(
            BigInteger(nonce),
            BigInteger(gasPrice),
            BigInteger(gasLimit),
            to,
            BigInteger(value),
            data
        )

        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .ethSignTransaction(rawTransaction, chainId.toLong())
            .subscribe({ bytes ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = bytes
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("signTransaction error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun ethSignTransaction1559(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val nonce: String = call.argument("nonce") ?: ""
        val maxPriorityFeePerGas: String = call.argument("maxPriorityFeePerGas") ?: ""
        val maxFeePerGas: String = call.argument("maxFeePerGas") ?: ""
        val gasLimit: String = call.argument("gasLimit") ?: ""
        val to: String = call.argument("to") ?: error("missing recipient")
        val value: String = call.argument("value") ?: "0"
        val data: String = call.argument("data") ?: ""
        val chainId: Int = call.argument("chainId") ?: 0
        val rawTransaction = RawTransaction.createTransaction(
            chainId.toLong(),
            BigInteger(nonce),
            BigInteger(gasLimit),
            to,
            BigInteger(value),
            data,
            BigInteger(maxPriorityFeePerGas),
            BigInteger(maxFeePerGas),
        )

        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .ethSignTransaction(rawTransaction, chainId.toLong())
            .subscribe({ bytes ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = bytes
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("signTransaction error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun ethSignTransaction1559WithIndex(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val nonce: String = call.argument("nonce") ?: ""
        val maxPriorityFeePerGas: String = call.argument("maxPriorityFeePerGas") ?: ""
        val maxFeePerGas: String = call.argument("maxFeePerGas") ?: ""
        val gasLimit: String = call.argument("gasLimit") ?: ""
        val to: String = call.argument("to") ?: error("missing recipient")
        val value: String = call.argument("value") ?: "0"
        val data: String = call.argument("data") ?: ""
        val chainId: Int = call.argument("chainId") ?: 0
        val index: Int = call.argument("index") ?: 0
        val rawTransaction = RawTransaction.createTransaction(
            chainId.toLong(),
            BigInteger(nonce),
            BigInteger(gasLimit),
            to,
            BigInteger(value),
            data,
            BigInteger(maxPriorityFeePerGas),
            BigInteger(maxFeePerGas),
        )

        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .ethSignTransactionWithIndex(rawTransaction, chainId.toLong(), index)
            .subscribe({ bytes ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = bytes
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("signTransaction1559 error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun ethSignTransactionWithIndex(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val nonce: String = call.argument("nonce") ?: ""
        val gasPrice: String = call.argument("gasPrice") ?: ""
        val gasLimit: String = call.argument("gasLimit") ?: ""
        val to: String = call.argument("to") ?: error("missing recipient")
        val value: String = call.argument("value") ?: "0"
        val data: String = call.argument("data") ?: ""
        val chainId: Int = call.argument("chainId") ?: 0
        val index: Int = call.argument("index") ?: 0
        val rawTransaction = RawTransaction.createTransaction(
            BigInteger(nonce),
            BigInteger(gasPrice),
            BigInteger(gasLimit),
            to,
            BigInteger(value),
            data
        )

        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .ethSignTransactionWithIndex(rawTransaction, chainId.toLong(), index)
            .subscribe({ bytes ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = bytes
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("signTransaction error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun encryptFile(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val inputPath: String = call.argument("inputPath") ?: ""
        val outputPath: String = call.argument("outputPath") ?: ""
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .encryptFile(File(inputPath), File(outputPath))
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = outputPath
                result.success(rev)
            }, { error ->
                result.error("Encrypt file failed", error.message, error)
            }).let { disposables.add(it) }
    }

    private fun decryptFile(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val inputPath: String = call.argument("inputPath") ?: ""
        val outputPath: String = call.argument("outputPath") ?: ""
        val usingLegacy: Boolean = call.argument("usingLegacy") ?: false
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .decryptFile(File(inputPath), File(outputPath), usingLegacy)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = outputPath
                result.success(rev)
            }, { error ->
                result.error("Decrypt file failed", error.message, error)
            }).let { disposables.add(it) }
    }

    private fun exportMnemonicWords(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .exportMnemonicWords()
            .subscribe({ words ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = words
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("exportMnemonicWords error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun getTezosPublicKey(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .getTezosPublicKey()
            .subscribe({ publicKey ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = publicKey
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("getTezosPublicKey error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun getTezosPublicKeyWithIndex(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val index: Int = call.argument("index") ?: 0

        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .getTezosPublicKeyWithIndex(index)
            .subscribe({ publicKey ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = publicKey
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("getTezosPublicKey error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun tezosSignMessage(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val message: ByteArray = call.argument("message") ?: error("missing message")

        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .tezosSignMessage(message)
            .subscribe({ data ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = data
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("tezosSignMessage error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun tezosSignMessageWithIndex(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val message: ByteArray = call.argument("message") ?: error("missing message")
        val index: Int = call.argument("index") ?: 0

        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .tezosSignMessageWithIndex(message, index)
            .subscribe({ data ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = data
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("tezosSignMessage error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun tezosSignTransaction(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val forgedHex: String = call.argument("forgedHex") ?: error("missing message")

        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .tezosTransaction(forgedHex)
            .subscribe({ data ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = data
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("tezosSignTransaction error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun tezosSignTransactionWithIndex(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val forgedHex: String = call.argument("forgedHex") ?: error("missing message")
        val index: Int = call.argument("index") ?: 0

        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .tezosTransactionWithIndex(forgedHex, index)
            .subscribe({ data ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = data
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("tezosSignTransaction error", it.message, it)
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

fun ByteArray.ethPersonalMessage() = Sign.getEthereumMessageHash(this)