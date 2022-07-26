import 'dart:async';

import 'package:flutter/services.dart';

class GeneralStorage {
  static const MethodChannel _channel = const MethodChannel('libauk_dart');

  Future<bool> hasPlatformShards() async {
    final result = await _channel.invokeMethod('hasPlatformShards');
    return result['result'];
  }

  Future<List<String>> scanPersonaUUIDs() async {
    final result = await _channel.invokeMethod('scanPersonaUUIDs');
    return List<String>.from(result['result']);
  }

  Future migrateAccountsFromV0ToV1() async {
    await _channel.invokeMethod('migrateAccountsFromV0ToV1');
  }
}
