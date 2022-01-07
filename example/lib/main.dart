import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:libauk_dart/libauk_dart.dart';
import 'package:uuid/uuid.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _ethereumAddress = 'Unknown';

  @override
  void initState() {
    super.initState();
    initLibAuk();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initLibAuk() async {
    String address;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      var uuid = Uuid().v4();
      print(uuid);
      var storage = LibAukDart.getWallet(uuid);
      await storage.createKey("Hello");
      address = (await storage.getTezosWallet()).address;
    } on PlatformException {
      address = 'Failed to get Address.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _ethereumAddress = address;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Libauk example app'),
        ),
        body: Center(
          child: Text('Ethereum Address: $_ethereumAddress\n'),
        ),
      ),
    );
  }
}
