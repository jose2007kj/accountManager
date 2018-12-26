
package com.jose2007kj.reactnative;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import android.accounts.Account;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.net.Uri;
import android.database.DatabaseUtils;

import android.provider.ContactsContract.PhoneLookup;
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
	public void getAccounts(final Promise promise) {
		AccountManager am = AccountManager.get(this.reactContext);
	  Account [] acc = am.getAccountsByType("com.google");//am.getAccounts();//
		if (acc.length > 0){
			String s = "";
			for (int i=0; i<acc.length; i++){
				s += acc[i] + "\n";
			}
			final WritableMap map = Arguments.createMap();

		try {
                            map.putString("account", s);
                            
                            promise.resolve(map);
                        } catch (Exception e) {
                            map.putString("account", "COULD_NOT_FETCH");
                            promise.reject("COULD_NOT_FETCH", map.toString());
                        }
	}
  }

  
  @ReactMethod
  public void identifyWhatsappContact(final Promise promise) {
        ContentResolver contentResolver = this.reactContext.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode("+919847533977"));

        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);
        String contactId,contactName1;
        contactId = "test";
        if(cursor!=null) {
            while(cursor.moveToNext()){
                contactName1 = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                // Log.d("fsdf", "contactMatch name: " + contactName1);
                // Log.d("dfdsfds", "contactMatch id: " + contactId);
            }
            cursor.close();
        }
        String[] projection1 = new String[] { ContactsContract.RawContacts._ID };
        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND account_type IN (?)";
        String[] selectionArgs = new String[] { contactId, "com.whatsapp" };
        Cursor cursor1 = this.reactContext.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection1, selection, selectionArgs, null);
        boolean hasWhatsApp = cursor1.moveToNext();
        final WritableMap map = Arguments.createMap();
        
        if (hasWhatsApp){
            map.putString("contact","true");
            promise.resolve(map);
        }else{
        map.putString("contact","false");
        promise.resolve(map);}
}


}