
import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';
import 'package:uuid/uuid.dart';

class LibAukDart {

  static WalletStorage getWallet(String uuid) {
    return WalletStorage(uuid);
  }
}

class WalletStorage {

  static const MethodChannel _channel = const MethodChannel('libauk_dart');

  final String uuid;

  WalletStorage(this.uuid);

  Future<void> createKey(String name) async {
    await _channel.invokeMethod('createKey', {"uuid": uuid, "name": name});
  }

  Future<void> importKey(String words, String name, int date) async {
    await _channel.invokeMethod('importKey', {
      "uuid": uuid,
      "words": words,
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

  Future<String> getETHAddress() async {
    Map res = await _channel.invokeMethod('getETHAddress', {"uuid": uuid});

    return res["data"];
  }

  Future<String> signPersonalMessage(Uint8List bytes) async {
    Map res = await _channel.invokeMethod('signPersonalMessage', {"uuid": uuid, "message": bytes});

    return res["data"];
  }

  Future<Uint8List> signTransaction({
    required int nonce,
    required BigInt gasPrice,
    required BigInt gasLimit,
    required String to,
    required BigInt value,
    required String data,
    required int chainId,
  }) async {
    Map res = await _channel.invokeMethod('signTransaction', {
      "uuid": uuid,
      "nonce": nonce.toString(),
      "gasPrice": gasPrice.toString(),
      "gasLimit": gasLimit.toString(),
      "to": to,
      "value": value.toString(),
      "data": data,
      "chainId": chainId
    });

    return res["data"];
  }

  Future<String> exportMnemonicWords() async {
    Map res = await _channel.invokeMethod('exportMnemonicWords', {"uuid": uuid});

    return res["data"];
  }

  Future<void> removeKeys() async {
    await _channel.invokeMethod('removeKeys', {"uuid": uuid});
  }
}