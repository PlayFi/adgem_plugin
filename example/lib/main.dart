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
    OWListener listener = OWListener(
      onOfferWallStateChanged: (status) {
        print('onOfferWallStateChanged');
      },
      onOfferWallClosed: () {
        print('onOfferWallClosed');
      },
      onOfferWallReward: () {
        print('onOfferWallClosed');
      },
      adGateLoadSuccess: () {
        Adgem.showOfferWall(network: 'adgate');
      },
    );
    super.initState();
    Adgem.init(listener: listener);
    Adgem.setPlayerId(id: '');
    Adgem.loadOffertoro(id: '', appId: '', secret: '');
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
            child: Column(
          children: [
            ElevatedButton(
                onPressed: () async {
                  if (await Adgem.isOfferWallReady()) {
                    Adgem.showOfferWall(network: 'adgem');
                  }
                },
                child: const Text('AdGem')),
            ElevatedButton(
                onPressed: () async {
                  Adgem.loadAdGate(id: 'abc', wallCode: 'oK2bqQ');
                },
                child: const Text('AdGate')),
            ElevatedButton(
                onPressed: () async {
                  Adgem.showOfferWall(network: "offertoro");
                },
                child: const Text('Offertoro')),
          ],
        )),
      ),
    );
  }
}
