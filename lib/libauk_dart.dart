import 'dart:async';

import 'package:flutter/services.dart';


const MethodChannel _channel = const MethodChannel('libauk_dart');

class LibAukDart {
  static WalletStorage getWallet(String uuid) {
    return WalletStorage(uuid);
  }
  static Future<String> calculateFirstEthAddress(String words, String? passphrase) async {
    Map res = await _channel.invokeMethod('calculateFirstEthAddress', {"words": words, "passphrase": passphrase ?? ""});
    return res["data"];
  }
}

class WalletStorage {
  final String uuid;

  WalletStorage(this.uuid);

  Future<void> createKey(String? password, String name) async {
    await _channel.invokeMethod(
        'createKey', {"uuid": uuid, "password": password ?? "", "name": name});
  }

  Future<void> importKey( String words, String? password, String name, int date) async {
    await _channel.invokeMethod('importKey', {
      "uuid": uuid,
      "words": words,
      "password": password ?? "",
      "name": name,
      "date": date,
    });
  }

  Future<bool> isWalletCreated() async {
    Map res = await _channel.invokeMethod('isWalletCreated', {"uuid": uuid});

    return res["data"];
  }

  Future<String> getName() async {
    Map res = await _channel.invokeMethod('getName', {"uuid": uuid});

    return res["data"];
  }

  Future<void> updateName(String name) async {
    await _channel.invokeMethod('updateName', {"uuid": uuid, "name": name});
  }

  Future<String> getAccountDID() async {
    Map res = await _channel.invokeMethod('getAccountDID', {"uuid": uuid});

    return res["data"];
  }

  Future<String> getAccountDIDSignature(String message) async {
    Map res = await _channel.invokeMethod(
        'getAccountDIDSignature', {"uuid": uuid, "message": message});

    return res["data"];
  }

  Future<String> getETHAddress({int index = 0}) async {

    Map res = index == 0
        ? await _channel.invokeMethod('getETHAddress', {
            "uuid": uuid,
          })
        : await _channel.invokeMethod('getETHAddressWithIndex', {
            "uuid": uuid,
            "index": index,
          });

    return res["data"];
  }

  Future<String> ethSignPersonalMessage(Uint8List bytes, {int index = 0}) async {
    Map res = await _channel.invokeMethod('ethSignPersonalMessageWithIndex',
            {"uuid": uuid, "message": bytes, "index": index});

    return res["data"];
  }

  Future<String> ethSignMessage(Uint8List bytes, {int index = 0}) async {
    Map res = await _channel.invokeMethod('ethSignMessageWithIndex',
            {"uuid": uuid, "message": bytes, "index": index});

    return res["data"];
  }

  Future<Uint8List> ethSignTransaction({
    required int nonce,
    required BigInt gasPrice,
    required BigInt gasLimit,
    required String to,
    required BigInt value,
    required String data,
    required int chainId,
    int? index,
  }) async {
    Map res = index == null
        ? await _channel.invokeMethod('ethSignTransaction', {
            "uuid": uuid,
            "nonce": nonce.toString(),
            "gasPrice": gasPrice.toString(),
            "gasLimit": gasLimit.toString(),
            "to": to,
            "value": value.toString(),
            "data": data,
            "chainId": chainId
          })
        : await _channel.invokeMethod('ethSignTransactionWithIndex', {
            "uuid": uuid,
            "nonce": nonce.toString(),
            "gasPrice": gasPrice.toString(),
            "gasLimit": gasLimit.toString(),
            "to": to,
            "value": value.toString(),
            "data": data,
            "chainId": chainId,
            "index": index,
          });

    return res["data"];
  }

  Future<Uint8List> ethSignTransaction1559({
    required int nonce,
    required BigInt gasLimit,
    required BigInt maxPriorityFeePerGas,
    required BigInt maxFeePerGas,
    required String to,
    required BigInt value,
    required String data,
    required int chainId,
    int index = 0,
  }) async {
    Map res = await _channel.invokeMethod('ethSignTransaction1559WithIndex', {
            "uuid": uuid,
            "nonce": nonce.toString(),
            "gasLimit": gasLimit.toString(),
            "maxPriorityFeePerGas": maxPriorityFeePerGas.toString(),
            "maxFeePerGas": maxFeePerGas.toString(),
            "to": to,
            "value": value.toString(),
            "data": data,
            "chainId": chainId,
            "index": index
          });

    return res["data"];
  }

  Future<String> encryptFile({
    required String inputPath,
    required String outputPath,
  }) async {
    Map res = await _channel.invokeMethod('encryptFile', {
      "uuid": uuid,
      "inputPath": inputPath,
      "outputPath": outputPath,
    });
    return res["data"];
  }

  Future<String> decryptFile({
    required String inputPath,
    required String outputPath,
    bool usingLegacy = false,
  }) async {
    Map res = await _channel.invokeMethod('decryptFile', {
      "uuid": uuid,
      "inputPath": inputPath,
      "outputPath": outputPath,
      "usingLegacy": usingLegacy,
    });
    return res["data"];
  }

  Future<String> exportMnemonicPassphrase() async {
    Map res =
        await _channel.invokeMethod('exportMnemonicPassphrase', {"uuid": uuid});

    return res["data"];
  }

  Future<String> exportMnemonicWords() async {
    Map res =
        await _channel.invokeMethod('exportMnemonicWords', {"uuid": uuid});

    return res["data"];
  }

  Future<String> getTezosPublicKey({int index = 0}) async {
    Map res = await _channel.invokeMethod(
            'getTezosPublicKeyWithIndex', {"uuid": uuid, "index": index});

    return res["data"];
  }

  Future<Uint8List> tezosSignMessage(Uint8List message, {int index = 0}) async {
    Map res = await _channel.invokeMethod('tezosSignMessageWithIndex',
            {"uuid": uuid, "message": message, "index": index});

    return res["data"];
  }

  Future<Uint8List> tezosSignTransaction(String forgedHex, {int index = 0}) async {
    Map res = await _channel.invokeMethod('tezosSignTransactionWithIndex',
            {"uuid": uuid, "forgedHex": forgedHex, "index": index});

    return res["data"];
  }

  Future<void> removeKeys() async {
    await _channel.invokeMethod('removeKeys', {"uuid": uuid});
  }
}
