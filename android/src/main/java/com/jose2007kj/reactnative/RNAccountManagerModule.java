
package com.jose2007kj.reactnative;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import android.accounts.Account;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import java.util.Iterator;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.net.Uri;
import java.net.URL;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.database.DatabaseUtils;
import java.net.URLConnection;
import android.provider.ContactsContract.PhoneLookup;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.content.ComponentName;
import android.util.Log;
import android.content.pm.PackageManager;
import android.os.Build;
import com.facebook.react.bridge.ActivityEventListener;
import android.provider.Telephony;
import java.security.MessageDigest;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import java.security.NoSuchAlgorithmException;
import android.content.ActivityNotFoundException;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
   //for paym
  //  @Override
  //  protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
  //      // Check which request it is that we're responding to
  //      if (requestCode == 103) {
  //        Log.d("paytm", "staret activity success: "+resultCode);
  //          // Make sure the request was successful

  //      }
  //  }
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
  public void sendSms(String mobileNo,String message){
    try{
      Intent sendIntent;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(reactContext);
        sendIntent = new Intent(Intent.ACTION_SEND);
        if (defaultSmsPackageName != null){
            sendIntent.setPackage(defaultSmsPackageName);
        }
        sendIntent.setType("text/plain");
      }else {
        sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("vnd.android-dir/mms-sms");
      }
      sendIntent.putExtra("sms_body", message);
      sendIntent.putExtra("exit_on_sent", true);
      sendIntent.putExtra("address", mobileNo);
      reactContext.startActivity(sendIntent);

    }catch(Exception e){
      Log.d("sms ", "sms error: "+e.toString());
    }
  }
  @ReactMethod
  public void openPaytmApp(String mobileNo,String amount) {
    final WritableMap map = Arguments.createMap();

    try {
      Bundle bundle  = new Bundle();
      bundle.putString("transaction_amount", amount);
      bundle.putString("transaction_mobile_email", mobileNo);
      bundle.putBoolean("isMobile Number Editable", false);
      bundle.putBoolean("is Amount Editable", true);
      Intent startPaytm = new Intent();
      startPaytm.setComponent(new ComponentName("net.one97.paytm", "net.one97.paytm.AJRJarvisSplash"));
      startPaytm.putExtra("paymentmode", 1);
      startPaytm.putExtra("bill", bundle);
      getCurrentActivity().startActivity(startPaytm);
      Log.d("paytm", "staretd activity: ");
      //todo:maybe in later stage we can handle on activiyresult
      // map.putString("account", s);

        // promise.resolve(map);
    } catch (Exception e) {
      Log.d("paytm", "staret activity exception: "+e);
        // map.putString("account", "COULD_NOT_FETCH");
        // promise.reject("COULD_NOT_FETCH", map.toString());
    }

  }



  @ReactMethod
  public void checkAccounts(final Promise promise) {
    AccountManager am = AccountManager.get(this.reactContext);
    Account [] acc = am.getAccounts();//am.getAccountsByType("com.google");
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
  @ReactMethod void getTimeStamp(final Promise promise){

        final WritableMap map = Arguments.createMap();
    try{
        String ts = String.valueOf(System.currentTimeMillis());
        map.putString("timestamp",ts);
        promise.resolve(map);
      }catch(Exception e){
         map.putString("timestamp",e.toString());
         promise.reject("failed",map.toString());
      }
  }
  @ReactMethod
  public void getRecentlyUpdatedConacts(String lastUpdate,final Promise promise){

      final WritableMap map = Arguments.createMap();
      try{
      ContentResolver contentResolver = this.reactContext.getContentResolver();
      Uri uri = ContactsContract.Contacts.CONTENT_URI;
      String ts = String.valueOf(System.currentTimeMillis());
      Cursor cursor = contentResolver.query(uri,
        null,
        ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + ">=?",
        new String[]{lastUpdate},
        null        // Ordering
        );
      WritableMap contactmap = Arguments.createMap();
      JSONObject updatedContact= new JSONObject();
      JSONArray contactList = new JSONArray();
      if (cursor.getCount() > 0) {
        JSONArray numberList;
        JSONObject numberObj;
        JSONArray emailList;
        JSONObject emailObj;
        JSONObject contactObj;
        String pno;
        String eml;
        int i;
        while(cursor.moveToNext()){
            contactObj = new JSONObject();
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contactObj.put("id",contactId);
            contactObj.put("name",name);
            Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            Cursor emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
            numberList= new JSONArray();
            i=0;
            while (phones.moveToNext()) {
                 numberObj= new JSONObject();
                 pno=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                 numberObj.put("id",i);
                 numberObj.put("number",pno);
                 numberList.put(numberObj);
                 i++;
            }
            i=0;
            emailList= new JSONArray();
            while (emails.moveToNext()) {
                    eml=emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    emailObj= new JSONObject();
                    emailObj.put("id",i);
                    emailObj.put("email",eml);
                    emailList.put(emailObj);
                    i++;
            }
            contactObj.put("numbers",numberList);
            contactObj.put("emails",emailList);
            contactList.put(contactObj);
        }
      }
      updatedContact.put("updatedContacts",contactList);
      contactmap= convertJsonToMap(updatedContact);
      Log.d("contact sync", "recently updated function contacts time stamp: "+ts);
      contactmap.putString("timestamp",ts);
      promise.resolve(contactmap);
      }catch(Exception e){
          map.putString("timestamp",e.toString());
          promise.reject("failed",map.toString());
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
//function for openining instgram app for authentication instead of web browser
@ReactMethod
public  void openInstagram(String url) {
  Log.d("instagram deeplink:", "insta 007"+url);
  Uri uri = Uri.parse(url);
  Intent insta = new Intent(Intent.ACTION_VIEW, uri);
  insta.setPackage("com.instagram.android");

  try {
      getCurrentActivity().startActivity(insta);
      Log.d("instagram deeplink inside:", "insta 008");
  } catch (ActivityNotFoundException e) {
      Log.d("KeyHash:", "insta 009"+e);
      getCurrentActivity().startActivity(new Intent(Intent.ACTION_VIEW,
      Uri.parse(url)));
  }
}
// function for converting url to base64

@ReactMethod
public void urlTobase64(String url, Promise promise) {
  try {
    URL imageUrl = new URL(url);
    URLConnection ucon = imageUrl.openConnection();
    InputStream is = ucon.getInputStream();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int read = 0;
    while ((read = is.read(buffer, 0, buffer.length)) != -1) {
        baos.write(buffer, 0, read);
    }
    baos.flush();
          final WritableMap map = Arguments.createMap();

        try {
                  map.putString("base64", Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT));
                  promise.resolve(map);
              } catch (Exception e) {
                  map.putString("base64", "COULD_NOT_FETCH");
                  promise.reject("COULD_NOT_FETCH", map.toString());
              }


} catch (Exception e) {
    final WritableMap map = Arguments.createMap();
    map.putString("error", "COULD_NOT_FETCH");
                  promise.reject("COULD_NOT_FETCH", map.toString());
    // Log.d("Error", e.toString());
}


}
//base64 function ends here
   private static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
    WritableMap map = new WritableNativeMap();

    Iterator<String> iterator = jsonObject.keys();
    while (iterator.hasNext()) {
        String key = iterator.next();
        Object value = jsonObject.get(key);
        if (value instanceof JSONObject) {
            map.putMap(key, convertJsonToMap((JSONObject) value));
        } else if (value instanceof  JSONArray) {
            map.putArray(key, convertJsonToArray((JSONArray) value));
        } else if (value instanceof  Boolean) {
            map.putBoolean(key, (Boolean) value);
        } else if (value instanceof  Integer) {
            map.putInt(key, (Integer) value);
        } else if (value instanceof  Double) {
            map.putDouble(key, (Double) value);
        } else if (value instanceof String)  {
            map.putString(key, (String) value);
        } else {
            map.putString(key, value.toString());
        }
    }
    return map;
}

private static WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
    WritableArray array = new WritableNativeArray();

    for (int i = 0; i < jsonArray.length(); i++) {
        Object value = jsonArray.get(i);
        if (value instanceof JSONObject) {
            array.pushMap(convertJsonToMap((JSONObject) value));
        } else if (value instanceof  JSONArray) {
            array.pushArray(convertJsonToArray((JSONArray) value));
        } else if (value instanceof  Boolean) {
            array.pushBoolean((Boolean) value);
        } else if (value instanceof  Integer) {
            array.pushInt((Integer) value);
        } else if (value instanceof  Double) {
            array.pushDouble((Double) value);
        } else if (value instanceof String)  {
            array.pushString((String) value);
        } else {
            array.pushString(value.toString());
        }
    }
    return array;
}

}
