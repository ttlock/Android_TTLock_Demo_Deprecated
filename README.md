"# Android_TTLock_Demo" 
=

## IDE
Android Studio

## Minimum SDK Version
18

## Introduce
### TTLockLock
#### TTLockAPI
Bluetooth Interface
#### DeviceFirmwareUpdateApi
Device Firmware Upgrade Interface
### TTLockLockGateway(you can control the lock by Network)
#### GatewayAPI
Gatwway Interface

## Usage
### Manifest Configure

#### uses-permission

##### Bluetooth Permission
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

##### Network Permission
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

#### Register Service
##### Register Bluetooth Service
<service android:name="com.ttlock.bl.sdk.service.BluetoothLeService" />

##### Register Device Firmware Upgrade Service
<service  android:name="com.ttlock.bl.sdk.service.DfuService" android:exported="true" />

### TTLock Usage
1. Import ttlock-sdk-2.0.aar
put ttlock-sdk-2.0.aar into libs directory in your project
2. Instantiate TTLockCallback Object
TTLockCallback mTTLockCallback = new TTLockCallback() {
//TODO:
Implement abstract methods
}
3. Init TTLockAPI Object
mTTLockAPI = new TTLockAPI(mContext, mTTLockCallback);









