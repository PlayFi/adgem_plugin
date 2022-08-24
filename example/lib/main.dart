import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:adgem_plugin/adgem_plugin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    AdGemListener listener = AdGemListener(
      onOfferWallStateChanged: (status) {
        print('onOfferWallStateChanged');
      },
      onOfferWallClosed: () {
        print('onOfferWallClosed');
      },
      onOfferWallReward: () {
        print('onOfferWallClosed');
      },
    );
    super.initState();
    Adgem.init(listener: listener);
    Adgem.setPlayerId(id: '12345');
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: ElevatedButton(
              onPressed: () async {
                print(await Adgem.isOfferWallReady());
                Adgem.showOfferWall();
              },
              child: const Text('Button')),
        ),
      ),
    );
  }
}
