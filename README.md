## wvp-GB28181-pro Android客户端

## 基本功能

### wpv服务配置

请参考 https://github.com/648540858/wvp-GB28181-pro

### 国标设备

- 设备列表展示 上下滚动
- 通道列表展示 左右滚动
- 通道视频流自动点播播放,暂时仅支持单通道观看
- 不支持翻页,当前最大加载 设备*15,通道*15

### 同屏播放

- 添加/删除设备
- 垂直滚动播放
- 自动播放

### 云端录像

- 按设备通道展示录像列表
- 按天展示录像列表
- 录像片段回放

### 设置

- 可配置wvp服务器 域名/IP, 端口
- 暂不支持HTTPS

## Compile

### 编译环境版本

- Gradle 8.3
- java 17.0.9 2023-10-17 LTS
- 最低支持 Android 11, 如有低版本需求自行修改 build.gradle minSdk版本号


## Reference

日历控件来源于 https://github.com/huanghaibin-dev/CalendarView, 以源码方式引入并在其基础上做了优化和个性化修改

播放器来源于 https://github.com/CarGuo/GSYVideoPlayer

## Licenses

The MIT License (MIT)
Copyright © 2023 orchingly orchingly@gmai.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.