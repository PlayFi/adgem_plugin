package com.playfi.adgem_plugin;

import android.content.Context;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

import com.adgatemedia.sdk.network.OnOfferWallLoadFailed;
import com.adgatemedia.sdk.network.OnOfferWallLoadSuccess;
import com.adgem.android.AdGem;
import com.adgem.android.PlayerMetadata;
import com.adgem.android.OfferWallCallback;
import com.adgatemedia.sdk.classes.AdGateMedia;
import com.offertoro.sdk.OTOfferWallSettings;
import com.offertoro.sdk.interfaces.OfferWallListener;
import com.offertoro.sdk.sdk.OffersInit;

/** AdgemPlugin */
public class AdgemPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  public static AdgemPlugin instance;

  public static AdgemPlugin getInstance() {
    return instance;
  }

  private MethodChannel channel;
  private AdGem adGem;
  private AdGateMedia adGateMedia;
  private Context context;
  private ActivityPluginBinding lastActivityPluginBinding;

  public AdGem getSdk() {
    return adGem;
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

  OfferWallListener lister = new OfferWallListener() {
    @Override
    public void onOTOfferWallInitSuccess() {
      channel.invokeMethod("OFFERTORO_INIT_SUCCESS", null);
    }

    @Override
    public void onOTOfferWallInitFail(String s) {
      channel.invokeMethod("OFFERTORO_INIT_FAIL", s);
    }

    @Override
    public void onOTOfferWallOpened() {
      channel.invokeMethod("OFFERTORO_OPEN",null);
    }

    @Override
    public void onOTOfferWallCredited(double v, double v1) {

    }

    @Override
    public void onOTOfferWallClosed() {
      channel.invokeMethod("OFFERTORO_CLOSED",null);
    }
  };

  OnOfferWallLoadSuccess adGateSuccess = new OnOfferWallLoadSuccess(){
    @Override
    public void onOfferWallLoadSuccess() {
      channel.invokeMethod("ADGATE_LOAD_SUCCESS", null);
    }
  };

  OnOfferWallLoadFailed adGateFail = new OnOfferWallLoadFailed(){

    @Override
    public void onOfferWallLoadFailed(String reason) {
      channel.invokeMethod("ADGATE_LOAD_FAIL", reason);
    }
  };

  AdGateMedia.OnOfferWallClosed adGateClose = new AdGateMedia.OnOfferWallClosed(){

    @Override
    public void onOfferWallClosed() {
      channel.invokeMethod("ON_OFFERWALL_CLOSED", null);
    }
  };

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    context = flutterPluginBinding.getApplicationContext();
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "com.playfi.offerwall");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("init")) {
      init(result);
    } else if (call.method.equals("showOfferWall")) {
      String provider = call.argument("network");
      showOfferWall(provider,result);
    } else if (call.method.equals("setPlayerId")) {
      String id = call.argument("id");
      setPlayerId(id, result);
    }else if(call.method.equals("loadAdGate")){
      String id = call.argument("id");
      String wallcode = call.argument("wallCode");
      loadAdGate(wallcode, id,result);
    }else if(call.method.equals("loadOffertoro")){
      String id = call.argument("id");
      String appId = call.argument("appId");
      String secret = call.argument("secret");
      loadOffertoro(appId, secret, id,result);
    }
    else if(call.method.equals("isOfferWallReady")){
      boolean ready = adGem.isOfferWallReady();
      result.success(ready);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private void init(final Result result) {
    AdGateMedia.initializeSdk(getCurrentActivity());
    adGateMedia = AdGateMedia.getInstance();
            AdGateMedia.getInstance().setDebugMode(true);
    adGem = AdGem.get();
    adGem.registerOfferWallCallback(callback);
    result.success(null);
  }

  private void loadOffertoro(final String appId, final String secretKey, final String playerId,final Result result){
    OTOfferWallSettings.getInstance().configInit(appId, secretKey, playerId);
    OffersInit.getInstance().create(getCurrentActivity());
    OffersInit.getInstance().setOfferWallListener(lister);
    result.success(null);
  }

  private void loadAdGate(final String wallcode, final String playerId, final Result result){
    final HashMap<String, String> subids = new HashMap<String, String>();
    Log.d("LOG", wallcode);
    Log.d("LOG", playerId);
    adGateMedia.loadOfferWall(getCurrentActivity(), wallcode, playerId, subids, adGateSuccess, adGateFail);
    result.success(null);
  }

  private void showOfferWall(final String network, final Result result) {
    if(network.equals("adgem")){
      adGem.showOfferWall(getCurrentActivity());
    }else if(network.equals("adgate")){
      adGateMedia.showOfferWall(getCurrentActivity(), adGateClose);
    }else if(network.equals("offertoro")){
      OffersInit.getInstance().showOfferWall(getCurrentActivity());
    }
    result.success(null);
  }

  private void setPlayerId(final String playerId, final Result result) {
    PlayerMetadata player = new PlayerMetadata.Builder().id(playerId).build();
    adGem.setPlayerMetaData(player);
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
