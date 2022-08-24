import 'package:flutter/services.dart';

class Adgem {
  static const MethodChannel _channel = MethodChannel('com.playfi.adgem');
  static AdGemListener? _listener;
  static AdGemListener? getListener() {
    return _listener;
  }

  static Future<dynamic> init({AdGemListener? listener}) async {
    _listener = listener;
    _channel.setMethodCallHandler(_listener?._handle);
    await _channel.invokeMethod('init');
  }

  static Future<dynamic> showOfferWall() async {
    await _channel.invokeMethod('showOfferWall');
  }

  static Future<dynamic> setPlayerId({required String id}) async {
    await _channel.invokeMethod('setPlayerId', {'id': id});
  }
}

abstract class AdGemListener {
  Future<dynamic> _handle(MethodCall call) async {
    if (call.method == 'ON_OFFERWALL_STATUS_CHANGE') {
      onOfferWallStateChanged(call.arguments['status']);
    } else if (call.method == 'ON_OFFERWALL_CLOSED') {
      onOfferWallClosed();
    } else if (call.method == 'ON_OFFERWALL_REWARD') {
      onOfferWallReward();
    }
  }

  void onOfferWallStateChanged(status) {}

  void onOfferWallClosed() {}

  void onOfferWallReward() {}
}
