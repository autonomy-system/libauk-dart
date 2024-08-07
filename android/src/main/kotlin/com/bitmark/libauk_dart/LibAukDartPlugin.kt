package com.bitmark.libauk_dart

import AddressIndex
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.bitmark.libauk.LibAuk
import com.bitmark.libauk.model.Seed
import com.bitmark.libauk.storage.ETH_KEY_INFO_FILE_NAME
import com.bitmark.libauk.util.BiometricUtil
import com.bitmark.libauk.util.fromJson
import com.bitmark.libauk.util.newGsonInstance
import com.google.gson.Gson

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.Sign
import java.io.File
import java.math.BigInteger
import java.util.*

/** LibaukDartPlugin */
class LibAukDartPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var disposables: CompositeDisposable
    private lateinit var activity: Activity

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "libauk_dart")
        channel.setMethodCallHandler(this)
        disposables = CompositeDisposable()
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        activity = activityPluginBinding.activity
        context = activityPluginBinding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        disposables.dispose()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        context = binding.activity
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        disposables.dispose()
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "createKey" -> {
                createKey(call, result)
            }
            "importKey" -> {
                importKey(call, result)
            }
            "calculateFirstEthAddress" -> {
                calculateFirstEthAddress(call, result)
            }
            "isWalletCreated" -> {
                isWalletCreated(call, result)
            }
            "getName" -> {
                getName(call, result)
            }
            "getAccountDID" -> {
                getAccountDID(call, result)
            }
            "getAccountDIDSignature" -> {
                getAccountDIDSignature(call, result)
            }
            "getAddressesWithIndexes" -> {
                getAddressesWithIndexes(call, result)
            }

            "ethSignPersonalMessage" -> {
                signPersonalMessage(call, result)
            }
            "ethSignPersonalMessageWithIndex" -> {
                signPersonalMessageWithIndex(call, result)
            }
            "ethSignMessage" -> {
                signMessage(call, result)
            }
            "ethSignMessageWithIndex" -> {
                signMessageWithIndex(call, result)
            }
            "ethSignTransaction" -> {
                signTransaction(call, result)
            }
            "ethSignTransactionWithIndex" -> {
                signTransactionWithIndex(call, result)
            }
            "ethSignTransaction1559" -> {
                signTransaction1559(call, result)
            }
            "ethSignTransaction1559WithIndex" -> {
                signTransaction1559WithIndex(call, result)
            }
            "encryptFile" -> {
                encryptFile(call, result)
            }
            "decryptFile" -> {
                decryptFile(call, result)
            }
            "exportMnemonicPassphrase" -> {
                exportMnemonicPassphrase(call, result)
            }
            "exportMnemonicWords" -> {
                exportMnemonicWords(call, result)
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
            "toggleBiometric" -> {
                toggleBiometric(call, result)
            }
            "isBiometricEnabled" -> {
                isBiometricEnabled(call, result)
            }
            "migrate" -> migrate(call, result)
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        disposables.dispose()
    }

    private fun readAllKeyStoreFiles(nameFilterFunction: (String) -> Boolean): Single<Map<String, ByteArray>> {
        return Single.fromCallable {
            val files = context.filesDir.listFiles { _, name -> nameFilterFunction(name) }
            val map = mutableMapOf<String, ByteArray>()
            files?.forEach { file ->
                val name = file.name.substringAfter("-")
                val data = file.readBytes()
                map[name] = data
            }
            map
        }
    }

    private fun createKey(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val name: String = call.argument("name") ?: ""
        val passphrase: String = call.argument("passphrase") ?: ""
        val isPrivate = true
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .createKey(passphrase, name, isPrivate)
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
        val passphrase: String = call.argument("passphrase") ?: ""
        val dateInMili: Long? = call.argument("date")
        val date: Date = dateInMili?.let { Date(it) } ?: Date()
        val isPrivate = true
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .importKey(words.split(" "), passphrase, name, date, isPrivate)
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

    private fun calculateFirstEthAddress(call: MethodCall, result: Result) {
        val words: String = call.argument("words") ?: ""
        val passphrase: String = call.argument("passphrase") ?: ""
        Single.just(LibAuk.getInstance().calculateFirstEthAddress(words, passphrase))
            .subscribe({
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = it
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("calculateFirstEthAddress error", it.message, it)
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
                result.error("getAccountDID error", it.message, it)
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
                result.error("getAccountDIDSignature error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private  fun getAddressesWithIndexes(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        val listAddressIndexJson: List<String> = call.argument("addressIndexes") ?: error("missing addressIndexes")
        val addressIndexes: List<AddressIndex> = listAddressIndexJson.map { newGsonInstance().fromJson(it) }
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .getAddresses(addressIndexes = addressIndexes)
            .subscribe({ addresses ->
                val addressMap = addresses.mapKeys { Gson().toJson(it.key).toString() }
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = addressMap
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("getAddresses error", it.message, it)
            })
            .let { disposables.add(it) }
    }

    private fun signMessage(call: MethodCall, result: Result) {
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

    private fun signMessageWithIndex(call: MethodCall, result: Result) {
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

    private fun signPersonalMessage(call: MethodCall, result: Result) {
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

    private fun signPersonalMessageWithIndex(call: MethodCall, result: Result) {
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

    private fun signTransaction(call: MethodCall, result: Result) {
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

    private fun signTransaction1559(call: MethodCall, result: Result) {
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

    private fun signTransaction1559WithIndex(call: MethodCall, result: Result) {
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

    private fun signTransactionWithIndex(call: MethodCall, result: Result) {
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

    private fun exportMnemonicPassphrase(call: MethodCall, result: Result) {
        val id: String? = call.argument("uuid")
        LibAuk.getInstance().getStorage(UUID.fromString(id), context)
            .exportMnemonicPassphrase()
            .subscribe({ passphrase ->
                val rev: HashMap<String, Any> = HashMap()
                rev["error"] = 0
                rev["data"] = passphrase
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("exportMnemonicPassphrase error", it.message, it)
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
                rev["data"] = words
                result.success(rev)
            }, {
                it.printStackTrace()
                result.error("exportMnemonicWords error", it.message, it)
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
                result.error("getTezosPublicKeyWithIndex error", it.message, it)
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

    private fun toggleBiometric(call: MethodCall, result: Result) {
        val isEnabled: Boolean = call.argument("isEnable") ?: false
        if (isEnabled == false) {
            val didAuthen = BiometricUtil.withAuthenticate<Boolean>(context as FragmentActivity, onAuthenticationSucceeded = {
                setBiometric(isEnabled)
                true
            }, onAuthenticationFailed = {
                false
            }, onAuthenticationError = {_, _ ->
                    false
            }).subscribe( {didAuthen ->
            if (didAuthen) {
                result.success(
                    mapOf(
                        "error" to 0,
                        "data" to true
                    )
                )
            }
            else {
                result.success(
                    mapOf(
                        "error" to 0,
                        "data" to false
                    )
                )
            }}).let { disposables.add(it) }
        } else {
            setBiometric(isEnabled)
            result.success(
                mapOf(
                    "error" to 0,
                    "data" to true
                )
            )
        }
    }

    private fun setBiometric(isEnabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences("FlutterSharedPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("flutter.device_passcode", isEnabled).apply()
    }

    private fun isBiometricEnabled(call: MethodCall, result: Result) {
        val sharedPreferences = context.getSharedPreferences("FlutterSharedPreferences", Context.MODE_PRIVATE)
        val isEnabled = sharedPreferences.getBoolean("flutter.device_passcode", false)
        result.success(
            mapOf(
                "error" to 0,
                "data" to isEnabled
            )
        )
    }

    private fun migrate(call: MethodCall, result: Result) {
        this.migrate(context)
            .doOnSubscribe {
                // Log the start of migration
                Log.d("Migration", "Starting migration process.")
            }
            .doOnComplete {
                // On successful completion
                result.success(
                    mapOf(
                        "error" to 0,
                        "data" to true
                    )
                )
                // Log successful completion
                Log.d("Migration", "Migration process completed successfully.")
            }
            .doOnError { error ->
                // Log any errors encountered
                Log.e("Migration", "Migration process failed with error: ${error.message}", error)
                result.error("MigrationError", "Migration process failed", error)
            }
            .subscribe()
            .let { disposables.add(it) }
    }
    private fun migrate(context: Context): Completable {
        return migrateV1(context)
    }

    private fun readAllKeyStoreFiles(
        nameFilterFunction: (String) -> Boolean,
        context: Context
    ): Single<Map<String, ByteArray>> {
        return Single.fromCallable {
            val files = context.filesDir.listFiles { _, name -> nameFilterFunction(name) }
            val map = mutableMapOf<String, ByteArray>()
            files?.forEach { file ->
                val data = file.readBytes()
                map[file.name] = data
            }
            map
        }
    }

    private fun getUUIDFromFileName(fileName: String): String {
        val lastIndex = fileName.lastIndexOf("-")
        return fileName.substring(0, lastIndex)
    }

    private fun migrateV1(context: Context): Completable {
        return readAllKeyStoreFiles(
            { name -> name.endsWith(ETH_KEY_INFO_FILE_NAME) },  // Filter files ending with ETH_KEY_INFO_FILE_NAME
            context
        ).flatMapCompletable { filesMap ->
            // Convert map to a sequence of (name, data) pairs
            Observable.fromIterable(filesMap.toList())
                .flatMapCompletable { (name, data) ->
                    try {
                        // Extract UUID from file name
                        val uuidString = getUUIDFromFileName(name)
                        val uuid = UUID.fromString(uuidString)
                        val storage = LibAuk.getInstance().getStorage(uuid, context)

                        // Export seed without authentication and process it
                        storage.exportSeed(withAuthentication = false).flatMapCompletable { seed ->
                            // Generate public data from the seed
                            val seedPublicData = storage.generateSeedPublicData(seed)

                            // Write public data to files directory
                            storage.writeOnFilesDir(
                                "libauk_seed_public_data.dat",
                                newGsonInstance().toJson(seedPublicData).toByteArray(),
                                false
                            )

                            // Remove the key file
                            storage.removeKey(ETH_KEY_INFO_FILE_NAME)
                        }
                    } catch (e: Exception) {
                        // Handle exceptions gracefully
                        Completable.error(e)
                    }
                }
        }
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