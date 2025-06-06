# MediaStats-iOS-ObjC

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现通话过程中数据统计分析功能。

## 环境准备

- Xcode 10.0+
- iOS真机设备
- 支持模拟器运行，但是部分功能无法使用

## 运行示例项目

这个段落主要讲解了如何编译和运行实例程序。

#### 获取AppKey

在编译和启动实例程序前，您需要首先获取一个可用的App Key：

1. 若您已经与专属客户经理取得联系，可直接向他获取Appkey

2. 若您并未与专属客户经理取得联系那么请按后续步骤获取Appkey

3. 首先在 [网易云信](https://id.163yun.com/register?h=media&t=media&clueFrom=nim&from=bdjjnim0035&referrer=https://app.yunxin.163.com/?clueFrom=nim&from=bdjjnim0035) 注册账号

4. 然后在「应用」一栏中创建您的项目
5. 等待专属客户经理联系您，并向他获取Appkey

6. 将AppKey填写进AppKey.h

```objective-c
#define kAppKey @"<#请输入您的AppKey#>"
```

#### 集成实时音视频SDK

1. 进入Demo根路径，执行`pod install`
2. 使用Xcode打开MediaStats-iOS-ObjC.xcworkspace，连接iPhone/iPad测试设备，设置有效的开发者签名后即可运行

## 使用方式

如果需要使用质量透明的功能，需要通过`addEngineMediaStatsObserver:`主动设置相应的回调，取消调用`removeEngineMediaStatsObserver:` 即可。

## NERtcEngineMediaStatsObserver 接口预览

1. 当前通话统计回调: `-(void)onRtcStats:(NERtcStats *)stat`
2. 本地音频流统计信息回调: `- (void)onLocalAudioStat:(NERtcAudioSendStats *)stat`
3. 远端音频流的统计信息回调: `- (void)onRemoteAudioStats:(NSArray<NERtcAudioRecvStats*> *)stats`
4. 本地视频流统计信息回调: `-(void)onLocalVideoStat:(NERtcVideoSendStats *)stat`
5. 远端视频流的统计信息回调: `- (void)onRemoteVideoStats:(NSArray<NERtcVideoRecvStats*> *)stats`
6. 所有用户的网络状态回调: `- (void)onNetworkQuality:(NSArray<NERtcNetworkQualityStats *> *)stats`

详细使用请见[质量透明](https://dev.yunxin.163.com/docs/product/%E9%9F%B3%E8%A7%86%E9%A2%91%E9%80%9A%E8%AF%9DG2/SDK%E5%BC%80%E5%8F%91%E9%9B%86%E6%88%90/iOS%E5%BC%80%E5%8F%91%E9%9B%86%E6%88%90/%E8%B4%A8%E9%87%8F%E9%80%8F%E6%98%8E)

