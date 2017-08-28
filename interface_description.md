# TTLockAPI
## Parameter
ExtendedBluetoothDevice extendedBluetoothDevice   蓝牙设备对象(不方便的话,目前传null也可以)     Bluetooth Device Object</br>
int uid                                           用户账号id(服务端获取) 即openid              Equate to openid</br>
String lockVersion                                锁版本信息(json格式,跟上传到服务器格式一致)    Lock Version information</br>
String adminPs                                    判断管理员(添加管理员之后会返回,原样传输就行,锁内使用) </br>
String unlockKey                                  开门Key(添加管理员之后会返回,原样传输就行,锁内使用) </br>
int lockFlagPos                                   锁标志位用于重置电子钥匙(初始化值为0,每重置一次+1)  </br>
String aesKeyStr                                  加密Key(添加管理员之后会返回,原样传输就行,锁内使用)   </br>
long unlockDate                                   开锁时间(时间戳 long类型 三代锁用于校准锁时间,传入0或者小于0表示不进行校准操作) </br>
long startDate                                    开始时间(时间戳 long类型)  </br>
long endDate                                      结束时间(时间戳 long类型)  </br>
long timezoneOffset                               (传入-1不考虑偏移量，使用默认值)锁时区和UTC时区时间的差数，单位milliseconds(以服务端返回的为准)</br>
## API
1. public boolean isBLEEnabled(Context context)</br>
用于判断蓝牙是否打开                          Determine if Bluetooth is turn on
2. public void requestBleEnable(Activity activity)</br>
请求打开蓝牙                                 Request to turn on Bluetooth
3. public void startBleService(Context context)</br>
启动蓝牙服务 使用蓝牙接口必须要启动蓝牙服务     Start Bluetooth Service
4. public void stopBleService(Context context)</br>
关闭蓝牙服务                                 Stop Bluetooth Service
5. public void startBTDeviceScan()</br>
启动蓝牙扫描                                 Start Bluetooth Scan 
6. public void stopBTDeviceScan()</br>
停止扫描                                     Stop Bluetooth Scan 
7. public void connect(String address)</br>
通过mac地址连接蓝牙设备                       Connect Device by mac address
8. public void connect(ExtendedBluetoothDevice device)</br>
通过设备对象连接蓝牙设备(推荐使用这种方式连接)  Connect Device by ExtendedBluetoothDevice object </br>
9. public void disconnect()                           
断开蓝牙连接                                 Disconnect Bluetooth
10. public void addAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice)</br>
添加管理员   Add Administrator
11. public void setAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr, String password) </br>
设置管理员键盘密码       Set Admin Keyboard Password
12. public void unlockByAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long unlockDate, String aesKeyStr, long timezoneOffset) </br>
管理员开门             Admin Unlock
13. public void unlockByUser(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, long startDate, long endDate, String unlockKey, int lockFlagPos, String aesKeyStr, long timezoneOffset) </br>
 电子钥匙开门         Ekey Unlock
 14. public void setLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String unlockKey, long date, int lockFlagPos, String aesKeyStr, long timezoneOffset) </br>
 校准时间            Set Lock Time</br>
 date   需要校准的时间(传入时间毫秒数)
 15. public void resetKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) </br>
 密码初始化        Init Passwords
 16. public void resetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, @NonNull String adminPs, int lockFlagPos, String aesKeyStr) </br>
 重置电子钥匙      Reset EKey
 17. public void getLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr, long timezoneOffset) </br>
 获取锁时间       
 18. public void resetLock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) </br>
 恢复出厂设置 管理员删除锁的时候 也要调用该指令删除锁内管理员 否则无法重新添加
 19. public void getOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr, long timezoneOffset) </br>
 读取操作日志(包括车位锁的警报记录)
 20. public void addPeriodKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String password, long startDate, long endDate, String aesKeyStr, long timezoneOffset) </br>
 添加期限密码 自定义密码
 21. public void deleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, int keyboardPwdType, @NonNull String password, String aesKeyStr) </br>
 删除单个键盘密码
 22. public void modifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, int keyboardPwdType, String originalPwd, String newPwd, long startDate, long endDate, String aesKeyStr, long timezoneOffset) </br>
修改键盘密码 </br>
originalPwd                   原始密码</br>
newPwd                        新密码(传null或者空字符串表示不修改密码)</br>
startDate                     开始时间(时间戳 long类型  传<=0表示不修改时间)</br>
endDate                       结束时间(时间戳 long类型  传<=0表示不修改时间)</br>
23. public void searchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) </br>
获取设备特征值(用于判断所支持的设备)
24. public void addICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) </br>
添加IC卡
25. public void searchICCardNumber(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) </br>
查询IC卡号
26. public void modifyICPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long cardNo, long startDate, long endDate, String aesKeyStr, long timezoneOffset) </br>
修改IC卡有效期
27. public void deleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long cardNo, String aesKeyStr) </br>
删除IC卡
28. public void clearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) </br>
清空IC卡
29. public void addFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) </br>
添加指纹
30. public void modifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long FRNo, long startDate, long endDate, String aesKeyStr, long timezoneOffset) </br>
修改指纹效期
31. public void deleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, long cardNo, String aesKeyStr) </br>
删除指纹
32. public void clearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, String lockVersion, String adminPs, String unlockKey, int lockFlagPos, String aesKeyStr) </br>
清空指纹
33. public void readDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersion, String aesKeyStr) </br>
获取设备信息
## TTLockCallback
1. onFoundDevice(final ExtendedBluetoothDevice extendedBluetoothDevice)</br>
发现设备回调
2. onDeviceConnected(final ExtendedBluetoothDevice extendedBluetoothDevice)</br>
连接上蓝牙
3. onDeviceDisconnected(ExtendedBluetoothDevice extendedBluetoothDevice)</br>
蓝牙断开
4. onGetLockVersion(ExtendedBluetoothDevice extendedBluetoothDevice, int protocolType, int protocolVersion, int scene, int groupId, int orgId, Error error) </br>
获取版本信息</br>
extendedBluetoothDevice  蓝牙设备对象</br>
protocolType             协议类型</br>
protocolVersion          协议版本</br>
scene                    场景</br>
groupId                  组ID</br>
orgId                    子组ID</br>
5. onAddAdministrator(ExtendedBluetoothDevice extendedBluetoothDevice, String lockVersionString, String adminPs, String unlockKey, String adminKeyboardPwd, String deletePwd, String pwdInfo, long timestamp, String aesKeystr, int feature, String modelNumber, String hardwareRevision, String firmwareRevision, Error error)</br>
adminKeyboardPwd  管理码</br>
deletePwd         清空码</br>
pwdInfo           密码数据</br>
timestamp         时间戳</br>
aesKeystr         加密Key,使用时按照原数据传入接口即可</br>
feature           设备特征</br>
modelNumber       型号</br>
hardwareRevision  硬件版本号</br>
firmwareRevision  固件版本号</br>
6. onResetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, int lockFlagPos, Error error)</br>
重置电子钥匙回调
7. onSetAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String adminKeyboardPwd, Error error)</br>
设置管理员键盘密码回调
8. onUnlock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, int uniqueid, long lockTime, Error error)</br>
开锁回调</br>
uniqueid 开锁的唯一标识id    只有三代锁有用 其它默认(取系统时间)</br>
lockTime 锁时间           只有三代锁有用 其它默认(取系统时间)</br>
9. onSetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, Error error)
校准时间成功回调
10. onGetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, long lockTime, Error error)
获取锁时间
11. onResetKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String pwdInfo, long timestamp, Error error)</br>
键盘密码初始化
12. onResetLock(ExtendedBluetoothDevice extendedBluetoothDevice, Error error)</br>
恢复出厂设置
13. onAddKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String password, long startDate, long endDate, Error error) </br>
添加键盘密码回调</br>
keyboardPwdType           2-永久 1-单次 3-期限(同服务端一致)</br>
password                  添加的密码</br>
startDate                 密码使用起始时间</br>
endDate                   密码使用截止时间(永久密码类型此参数无意义)</br>
14. onModifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String originPwd, String newPwd, Error error) </br>
修改密码</br>
keyboardPwdType               密码类型</br>
originPwd                     原始密码</br>
newPwd                        修改后密码</br>
15. onDeleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String deletedPwd, Error error) </br>
删除单个密码回调
16. onDeleteAllKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) </br>
删除所有键盘密码回调
17. onGetOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String records, Error error)</br>
获取操作日志(包含车位锁的警报记录)</br>
records                   日志记录(json格式，传入服务端即可)</br>
18. onSearchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int feature, Error error)</br>
feature                   锁特征值(用于判断锁支持的功能)
19. onAddICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long cardNo, Error error)</br>
cardNo                        IC卡卡号</br>
status                        1 - 进入添加模式  2 - 添加成功</br>
20. onModifyICCardPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNo, long startDate, long endDate, Error error) </br>
修改IC卡有效期
21. onDeleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNo, Error error) </br>
删除IC卡
22. onClearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error)</br>
清空IC卡
23. onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long fingerPrintNo, Error error)</br>
添加指纹回调
24. onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error)</br>
指纹采集
25. onModifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long FRNo, long startDate, long endDate, Error error) </br>
修改指纹有效期
26. onDeleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long FRNo, Error error)</br>
删除指纹
27. onClearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error)</br>
清空指纹
28. onReadDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, String modelNumber, String hardwareRevision, String firmwareRevision, String manufactureDate, String lockClock) </br>
读取设备信息</br>
modelNumber               产品型号("M201")</br>
hardwareRevision          硬件版本号("1.3")</br>
firmwareRevision          固件版本号("2.1.16.705")</br>
manufactureDate           生产日期("20160707")</br>
lockClock                 时钟("170105153105") 年月日时分秒</br>

## DeviceFirmwareUpdateApi
### Status  升级过程中的状态
public static final  int UpgradeOprationPreparing  = 1;             //准备中</br>
public static final  int UpgradeOprationUpgrading = 2;              //升级中</br>
public static final  int UpgradeOprationRecovering = 3;             //恢复中</br>
public static final  int UpgradeOprationSuccess = 4;                //升级成功</br>
### Error  升级过程的错误码
public static final int DfuFailed = 1;                              //固件升级失败</br>
public static final int BLEDisconnected = 2;                        //蓝牙断开</br>
public static final int BLECommandError = 3;                        //蓝牙指令错误</br>
public static final int RequestError = 4;                           //服务器请求错误</br>
public static final int NetError = 5;                               //网络错误</br>

## DeviceFirmwareUpdateCallback
1. onGetLockFirmware(int specialValue, String module, String hardware, String firmware);
获取设备固件信息
2. onStatusChanged(int status)
升级状态改变回调
3. onDfuProcessStarting(final String deviceAddress)
DFU启动
4. onEnablingDfuMode(final String deviceAddress)
进入DFU模式
5. onDfuCompleted(final String deviceAddress)
固件升级过程完成
6. onDfuAborted(final String deviceAddress)
固件升级过程中断
7. onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) </br>
固件升级过程中的进度
8. onError(int errorCode, Error error, String errorContent)</br>
错误回调
errorCode       错误码</br>
error           蓝牙错误</br>
errorContent    服务器错误</br>








 
 
