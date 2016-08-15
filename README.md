#电话保镖
##一个支持"一键免打扰模式"、"闹钟免打扰模式"、"自定义通讯录"的App，已上线，现在开源给大家。

各大应用市场均已上线。

豌豆荚 : [地址](http://www.wandoujia.com/apps/com.hengswings.phoneguard)

腾讯应用宝 : [地址](http://android.myapp.com/myapp/detail.htm?apkName=com.hengswings.phoneguard)

百度手机助手 : [地址](http://shouji.baidu.com/software/7861528.html)


##程序功能及相关截图
<img src="https://github.com/happyheng/dianhuabaobiao/blob/master/res/drawable-xhdpi/newfeatureone.png?raw=true" width = "300" height = "533" alt="截图5" align=center />
<img src="https://github.com/happyheng/dianhuabaobiao/blob/master/res/drawable-xhdpi/newfeatureonefour.png?raw=true" width = "300" height = "533" alt="截图5" align=center />
<img src="https://github.com/happyheng/dianhuabaobiao/blob/master/res/drawable-xhdpi/newfeaturethree.png?raw=true" width = "300" height = "533" alt="截图5" align=center />
<img src="https://github.com/happyheng/dianhuabaobiao/blob/master/res/drawable-xhdpi/newfeaturetwo.png?raw=true" width = "300" height = "533" alt="截图5" align=center />


###后台拦截电话并回发短信的原理是:
 下面给大家简单说一下"拦截电话并回发短信功能"的实现原理:

 点击按钮后，启动PhoneService，PhoneService注册两个广播，一个监听电话的打入，一个监听系统系统栏的点击事件。
 
 当电话打入时，系统会调用TelInternalBroadcastReceiver广播接受者，TelInternalBroadcastReceiver判断电话事件，发现是来电，将做以下事情:

  * 如果是来电，得到来电号码，判断是否在白名单中，如果不在，挂掉电话
  * 判断今天的回发短信次数是否超限，如果没有超限，调用系统接口回发短信

