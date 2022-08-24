package com.playfi.adgem_plugin;

import android.content.Context;
import android.app.Activity;

import androidx.annotation.NonNull;
import java.util.Map;
import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import com.adgem.android.AdGem;
import com.adgem.android.PlayerMetadata;
import com.adgem.android.OfferWallCallback;

/** AdgemPlugin */
public class AdgemPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  public static AdgemPlugin instance;

  public static AdgemPlugin getInstance() {
    return instance;
  }

  private MethodChannel channel;
  private AdGem sdk;
  private Context context;
  private ActivityPluginBinding lastActivityPluginBinding;

  public AdGem getSdk() {
    return sdk;
  }

  OfferWallCallback callback = new OfferWallCallback() {
      @Override
      public void onOfferWallStateChanged(int newState) {
          Map<String, Integer> params = new HashMap<>( 1 );
          params.put( "status", newState );
          channel.invokeMethod("ON_OFFERWALL_STATUS_CHANGE", params);
      }

      @Override
      public void onOfferWallReward(int amount) {
        channel.invokeMethod("ON_OFFERWALL_REWARD", null);
      }

      @Override
      public void onOfferWallClosed() {
        channel.invokeMethod("ON_OFFERWALL_CLOSED", null);
      }
  };

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    context = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.playfi.adgem");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("init")) {
      init(result);
    } else if (call.method.equals("showOfferWall")) {
      showOfferWall(result);
    } else if (call.method.equals("setPlayerId")) {
      String id = call.argument("id");
      setPlayerId(id, result);
    }
    else if(call.method.equals("isOfferWallReady")){
      boolean ready = sdk.isOfferWallReady();
      result.success(ready);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private void init(final Result result) {
    sdk = AdGem.get();
    sdk.registerOfferWallCallback(callback);
    result.success(null);
  }

  private void showOfferWall(final Result result) {
    sdk.showOfferWall(getCurrentActivity());
    result.success(null);
  }

  private void setPlayerId(final String playerId, final Result result) {
    PlayerMetadata player = new PlayerMetadata.Builder().id(playerId).build();
    sdk.setPlayerMetaData(player);
    result.success(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull final ActivityPluginBinding binding) {
    lastActivityPluginBinding = binding;
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull final ActivityPluginBinding binding) {
  }

  @Override
  public void onDetachedFromActivity() {
  }

  private Activity getCurrentActivity() {
    return (lastActivityPluginBinding != null) ? lastActivityPluginBinding.getActivity() : null;
  }
}
