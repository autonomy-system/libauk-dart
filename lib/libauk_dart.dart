import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

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

  Future<String> getAccountDID() async {
    Map res = await _channel.invokeMethod('getAccountDID', {"uuid": uuid});

    return res["data"];
  }

  Future<String> getAccountDIDSignature(String message) async {
    Map res = await _channel.invokeMethod(
        'getAccountDIDSignature', {"uuid": uuid, "message": message});

    return res["data"];
  }

  Future<String> getETHAddress() async {
    Map res = await _channel.invokeMethod('getETHAddress', {"uuid": uuid});

    return res["data"];
  }

  Future<String> signPersonalMessage(Uint8List bytes) async {
    Map res = await _channel
        .invokeMethod('signPersonalMessage', {"uuid": uuid, "message": bytes});

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
  }) async {
    Map res = await _channel.invokeMethod('decryptFile', {
      "uuid": uuid,
      "inputPath": inputPath,
      "outputPath": outputPath,
    });
    return res["data"];
  }

  Future<String> exportMnemonicWords() async {
    Map res =
        await _channel.invokeMethod('exportMnemonicWords', {"uuid": uuid});

    return res["data"];
  }

  Future<TezosWallet> getTezosWallet() async {
    Map res = await _channel.invokeMethod('getTezosWallet', {"uuid": uuid});

    final String address = res["address"];
    final Uint8List secretKey = res["secretKey"];
    final Uint8List publicKey = res["publicKey"];

    return TezosWallet(address, secretKey, publicKey);
  }

  Future<String> getBitmarkAddress() async {
    Map res = await _channel.invokeMethod('getBitmarkAddress', {"uuid": uuid});

    return res["data"];
  }

  Future<void> removeKeys() async {
    await _channel.invokeMethod('removeKeys', {"uuid": uuid});
  }

  Future setupSSKR() async {
    await _channel.invokeMethod('setupSSKR', {"uuid": uuid});
  }

  Future<String?> getShard(ShardType shardType) async {
    Map res = await _channel.invokeMethod(
        'getShard', {'uuid': uuid, 'shardType': shardType.intValue});
    return res["data"];
  }

  Future removeShard(ShardType shardType) async {
    await _channel.invokeMethod(
        'removeShard', {"uuid": uuid, 'shardType': shardType.intValue});
  }

  Future restoreByBytewordShards(
    List<String> shards, {
    String? name,
    DateTime? creationDate,
  }) async {
    await _channel.invokeMethod('restoreByBytewordShards', {
      "uuid": uuid,
      'shares': shards,
      'name': name,
      'date': creationDate?.millisecondsSinceEpoch
    });
  }
}

class TezosWallet {
  final String address;
  final Uint8List secretKey;
  final Uint8List publicKey;

  TezosWallet(this.address, this.secretKey, this.publicKey);
}

enum ShardType { Platform, ShardService, EmergencyContact }

extension ShardTypeExtension on ShardType {
  int get intValue {
    switch (this) {
      case ShardType.Platform:
        return 0;
      case ShardType.ShardService:
        return 1;
      case ShardType.EmergencyContact:
        return 2;
    }
  }
}
