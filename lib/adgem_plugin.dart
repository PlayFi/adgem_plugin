import 'package:flutter/services.dart';

class Adgem {
  static const MethodChannel _channel = MethodChannel('com.playfi.adgem');
  static AdGemListener? _listener;
  static AdGemListener? getListener() {
    return _listener;
  }

  static Future<dynamic> init({AdGemListener? listener}) async {
    _listener = listener;
    _channel.setMethodCallHandler((MethodCall call) async {
      if (call.method == 'ON_OFFERWALL_STATUS_CHANGE') {
        _listener?.onOfferWallStateChanged(call.arguments['status']);
      } else if (call.method == 'ON_OFFERWALL_CLOSED') {
        _listener?.onOfferWallClosed();
      } else if (call.method == 'ON_OFFERWALL_REWARD') {
        _listener?.onOfferWallReward();
      }
    });
    await _channel.invokeMethod('init');
  }

  static Future<dynamic> showOfferWall() async {
    await _channel.invokeMethod('showOfferWall');
  }

  static Future<dynamic> setPlayerId({required String id}) async {
    await _channel.invokeMethod('setPlayerId', {'id': id});
  }

  static Future<bool> isOfferWallReady() async {
    return await _channel.invokeMethod('isOfferWallReady');
  }
}

class AdGemListener {
  final Function(String status) onOfferWallStateChanged;
  final Function() onOfferWallClosed;
  final Function() onOfferWallReward;

  AdGemListener({
    required this.onOfferWallStateChanged,
    required this.onOfferWallClosed,
    required this.onOfferWallReward,
  });
}
