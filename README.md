# MusicYou
Unofficial NetEase Cloud Music player


需要的target为33（bt sdk） 修改login为uwp


# 需要（可以custom）修改的地方

app/src/main/java/com/kyant/ncmapi/下的两个文件

Apiaction login  userapi

## api
修改request（在这个仓库已经修改好 可以直接构建使用）

#### 我们已经不推荐你对这一步的deviceId进行修改 因为我们去除了跟踪id 但是为了避免风控

详细参考![commit](https://github.com/GuihongWang/MusicYou/commit/5fca4b9b0c5e13e6370a64358fdc03d27ce8f07f)

Linux命令 cat /dev/urandom | tr -dc A-Z0-9 | head -c 32


将那32个1换成生成的内容

os=ios 使用ios api登录 但是登录不了会提示忙碌 需要修改成下面任意一个

UWP版


os=uwp;appver=1.4.1;osver=10.0.19041.1;deviceId=11111111111111111111111111111111


Windows 版(未测试)


os=pc;appver=2.10.2.200154;osver=Microsoft-Windows-10--build-19041-64bit;deviceId=0000000000000000000000000000000000000000000000000000;mode=To%20be%20filled%20by%20O.E.M.;channel=netease


## userapi 
修复我喜欢的音乐报错bug


（在这个仓库已经修改好 可以直接构建使用）


修改第33行 将30这个值修改成10000


## login 


如果你使用手机号登陆 而且是中国大陆手机号 可以略过中国修改


修改第23行的86为你需要登陆的国家手机号 （例：香港是852）


# 构建须知


神秘邀请函(构建命令)：gradlew assembleRelease


构建的apk会放在\app\build\outputs\apk\release文件夹下


构建得到的apk需要自行签名（可用MT管理器）
