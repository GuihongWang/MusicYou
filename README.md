# MusicYou
Unofficial NetEase Cloud Music player


需要的target为33（bt sdk） 修改login为uwp


# 需要（可以custom）修改的地方

app/src/main/java/com/kyant/ncmapi/下的两个文件

Apiaction login  userapi

### api
修改request（在这个仓库已经修改好 可以直接构建使用）


os=ios 使用ios api登录 但是登录不了会提示忙碌 需要修改成下面任意一个

UWP版


os=uwp;appver=1.4.1;osver=10.0.19041.1;deviceId=11111111111111111111111111111111


Windows 版(未测试)


os=pc;appver=2.10.2.200154;osver=Microsoft-Windows-10--build-19041-64bit;deviceId=0000000000000000000000000000000000000000000000000000;mode=To%20be%20filled%20by%20O.E.M.;channel=netease


### userapi 
修复我喜欢的音乐报错bug


（在这个仓库已经修改好 可以直接构建使用）


修改第33行 将30这个值修改成10000


### login 


如果你使用手机号登陆 而且是中国大陆手机号 可以略过中国修改


修改第23行的86为你需要登陆的国家手机号 （例：香港是852）
