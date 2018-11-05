
# react-native-account-manager

## Getting started

`$ npm install git+https://git@github.com/jose2007kj/accountManager.git --save`

### Mostly automatic installation

`$ react-native link react-native-account-manager`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.jose2007kj.reactnative.RNAccountManagerPackage;` to the imports at the top of the file
  - Add `new RNAccountManagerPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-account-manager'
  	project(':react-native-account-manager').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-account-manager/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-account-manager')
  	```
4. Insert the following permision in `android/src/app/main/androidmanifest.xml`:
  	```
      <uses-permission android:name="android.permission.GET_ACCOUNTS"/>
  	```


## Usage
```javascript
import RNAccountManager from 'react-native-account-manager';

// TODO: What to do with the module? this print all account info like in this question https://stackoverflow.com/questions/13244130/how-to-list-out-all-the-accounts-facebook-twitter-gmail-etc-in-android
 RNAccountManager.getAccounts().then(account => {
            console.log('account info'+account.account);
            })
            .catch(error => {
              console.log(error)
            })
```
  