import 'package:flutter/services.dart';

class Adgem {
  static const MethodChannel _channel = MethodChannel('com.playfi.offerwall');
  static OWListener? _listener;
  static OWListener? getListener() {
    return _listener;
  }

  static Future<dynamic> init({OWListener? listener}) async {
    _listener = listener;
    _channel.setMethodCallHandler((MethodCall call) async {
      if (call.method == 'ON_OFFERWALL_STATUS_CHANGE') {
        _listener?.onOfferWallStateChanged(call.arguments['status']);
      } else if (call.method == 'ON_OFFERWALL_CLOSED') {
        _listener?.onOfferWallClosed();
      } else if (call.method == 'ON_OFFERWALL_REWARD') {
        _listener?.onOfferWallReward();
      } else if (call.method == 'ADGATE_LOAD_SUCCESS') {
        _listener?.adGateLoadSuccess();
      }
    });
    await _channel.invokeMethod('init');
  }

  static Future<dynamic> showOfferWall({required String network}) async {
    await _channel.invokeMethod('showOfferWall', {'network': network});
  }

  static Future<dynamic> setPlayerId({required String id}) async {
    await _channel.invokeMethod('setPlayerId', {'id': id});
  }

  static Future<void> loadAdGate(
      {required String id, required String wallCode}) async {
    await _channel.invokeMethod('loadAdGate', {'id': id, 'wallCode': wallCode});
  }

  static Future<void> loadOffertoro(
      {required String id,
      required String appId,
      required String secret}) async {
    await _channel.invokeMethod(
        'loadOffertoro', {'id': id, 'appId': appId, 'secret': secret});
  }

  static Future<bool> isOfferWallReady() async {
    return await _channel.invokeMethod('isOfferWallReady');
  }
}

class OWListener {
  final Function(String status) onOfferWallStateChanged;
  final Function() onOfferWallClosed;
  final Function() onOfferWallReward;
  final Function() adGateLoadSuccess;

  OWListener(
      {required this.onOfferWallStateChanged,
      required this.onOfferWallClosed,
      required this.onOfferWallReward,
      required this.adGateLoadSuccess});
}
