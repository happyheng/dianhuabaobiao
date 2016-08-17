#电话保镖
##一个支持"一键免打扰模式"、"闹钟免打扰模式"、"自定义通讯录"的App，已上线，现在开源给大家。

各大应用市场均已上线。

豌豆荚 : [地址](http://www.wandoujia.com/apps/com.hengswings.phoneguard)

腾讯应用宝 : [地址](http://android.myapp.com/myapp/detail.htm?apkName=com.hengswings.phoneguard)

百度手机助手 : [地址](http://shouji.baidu.com/software/7861528.html)

#功能描述
在工作和生活中，我们总有很多不想被打扰的时刻，电话保镖将为您提供多种方式，满足您拦截电话的多种需求。

1）一键进入免打扰模块

按下“一键进入免打扰模式”后，手机立刻进入免打扰状态。如在开会时，可使用该功能，既不会在开会时被打扰，又能自动回复对方短信，告知对方自己在忙。可在会议后得知哪些人曾给自己打电话，以便及时回电话。

另外也可在主页面翻转手机或者在桌面点击快捷方式进入免打扰模式

2）闹钟免打扰模块

可设置在某个时间段内，如睡觉时，不被打扰，到时间后响铃提醒自己起床。

3）通讯录模块

将工作或生活中只在短时间内需要联系的手机号码，加入私人通讯录，如快递员电话等。与常联系的人分开，方便查找和删除。
同时可在其中设置黑白名单，不被黑名单中的人打扰。或只允许白名单中的人打进电话。

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

