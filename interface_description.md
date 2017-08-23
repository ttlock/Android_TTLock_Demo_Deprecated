# TTLockAPI
## Parameter
ExtendedBluetoothDevice extendedBluetoothDevice   蓝牙设备对象(不方便的话,目前传null也可以)
int uid                                           用户账号id(服务端获取) 即openid
String lockVersion                                锁版本信息(json格式,跟上传到服务器格式一致)
String adminPs                                    判断管理员(添加管理员之后会返回,原样传输就行,锁内使用)
String unlockKey                                  开门Key(添加管理员之后会返回,原样传输就行,锁内使用)
int lockFlagPos                                   锁标志位用于重置电子钥匙(初始化值为0,每重置一次+1)
String aesKeyStr                                  加密Key(添加管理员之后会返回,原样传输就行,锁内使用)   
long unlockDate                                   开锁时间(时间戳 long类型 三代锁用于校准锁时间,传入0或者小于0表示不进行校准操作)
