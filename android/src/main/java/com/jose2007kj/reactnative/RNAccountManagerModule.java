
package com.jose2007kj.reactnative;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import android.accounts.Account;
import android.accounts.AccountManager;

public class RNAccountManagerModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNAccountManagerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNAccountManager";
  }
  @ReactMethod
	public void getAccounts (final Promise promise) {
		AccountManager am = AccountManager.get(this.reactContext);
		Account [] acc = am.getAccounts();
		if (acc.length > 0){
			String s = "";
			for (int i=0; i<acc.length; i++){
				s += acc[i] + "\n";
			}
			

		try {
                            map.putString("account", s);
                            
                            promise.resolve(map);
                        } catch (Exception e) {
                            map.putString("account", "COULD_NOT_FETCH");
                            promise.reject("COULD_NOT_FETCH", map.toString());
                        }
	}
}