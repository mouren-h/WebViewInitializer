Ticket‑Tear‑Android
 
Android 端票据撕票动画组件，基于 WebView + 本地嵌入式 HTTP 静态服务器实现逼真撕票交互动画，支持自定义全部票据文案、多种重置模式，Java 开发，开箱即用。
 
✨ 项目简介
 
本组件是一个可直接嵌入 Android XML 的票据撕票 UI 控件。内部打包静态网页资源，启动本地小型 HTTP 服务，WebView 访问本地服务渲染票据，完成撕票交互动画。
 
- 🎫 逼真撕票视觉动画效果，前端 HTML/CSS/JS 渲染票据样式
- 📝 34+ 项票据字段支持 Java 代码动态设置文字内容
- ⚙️ 三种撕票后点击重置模式： auto  /  manual  /  forbid 
- 📢 Java 层回调监听撕票事件、重置请求事件
- 📦 Assets 内置  web.zip ，自动解压、版本校验更新静态资源
- 🚀 内置简易 Java 静态 HTTP 服务器，无需网络，完全离线运行
- 🧩 继承  FrameLayout ，XML 布局直接引用，低侵入接入
 
适用场景：电子票、入场券、凭证、优惠券撕票效果展示。
 
📦 架构说明
 
plaintext
  
├── mr.x.jh
│   ├── WebViewInitializer.java  # 对外暴露的 View 控件，业务直接使用
│   └── WebServer.java           # 本地嵌入式静态HTTP服务
├── assets
│   └── web.zip                  # html/css/js 票据前端资源包
 
 
1.  WebViewInitializer ：继承 FrameLayout，内部封装 WebView，负责解压 web.zip、启动本地服务、参数传递、事件监听分发。
2.  WebServer ：纯 Java 实现轻量本地 http 服务器，监听 127.0.0.1，提供静态资源；提供两个 API 接口：
-  /api/torn ：前端撕票完成，生成  torn.flag ，Java 监听到撕票回调
-  /api/reset‑request ：用户点击已撕票据，生成  reset_request.flag ，触发重置请求回调
3. 前端资源放在 assets/web.zip，控件初始化自动解压，版本号校验自动覆盖更新。
4. 采用文件flag轮询机制，Java ↔ Web JS 双向通信。
 
🚀 快速接入
 
1.布局 XML
 
xml
  
<mr.x.jh.WebViewInitializer
    android:id="@+id/ticketView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
 
 
2.Activity / Fragment Java 代码
 
```java
  
WebViewInitializer ticketView = findViewById(R.id.ticketView);

// 设置票据文字
ticketView.setTitle("活动入场券");
ticketView.setCode("NO‑20261120‑0001");
ticketView.setVenue("某某大会会场");
ticketView.setDate("2026‑11‑20");
ticketView.setPrice("免费");

// 设置重置模式
// auto：点击撕完票据自动恢复新票据
// manual：点击只回调，Java代码手动调用resetPass()恢复票据
// forbid：点击无任何响应
ticketView.setResetMode("manual");

// 监听撕票完成
ticketView.setOnTornListener(() -> {
    // 撕票完成业务逻辑
});

// manual模式监听用户点击已撕下票据
ticketView.setOnResetRequest(() -> {
    // 业务判断是否允许重置票据
    ticketView.resetPass(); // 恢复全新票据
});
```
 
3.生命周期销毁
 
页面销毁务必调用 destroy，释放 WebView、线程资源，避免内存泄漏
 
java
  
@Override
protected void onDestroy() {
    super.onDestroy();
    if(ticketView != null){
        ticketView.destroy();
    }
}
 
 
📚 API 文档
 
票据文字 setXXX() 方法
 
全部34项可配置字段
 
java
  
setHeaderTag(String v)
setCode(String v)
setIssuer(String v)
setStock(String v)
setEyebrow(String v)
setTitle(String v)
setSubtitle(String v)
setSec1Title(String v)
setSec2Title(String v)
setSec3Title(String v)
setSec4Title(String v)
setVenue(String v)
setVenueSub(String v)
setDate(String v)
setDateSub(String v)
setLabel(String v)
setAccessBadge(String v)
setPrice(String v)
setSerialLabel(String v)
setSerialPrefix(String v)
setTearHint(String v)
setStubTitle(String v)
setStubCode(String v)
setHologramText(String v)
setTicketFooter(String v)
setBackHeader(String v)
setBackZonePrefix(String v)
setZone(String v)
setBackCertified(String v)
setBackCertifiedSub(String v)
setBackInfo1(String v)
setBackInfo2(String v)
setBackInfo3(String v)
setBackInfo4(String v)
setBackInfo5(String v)
setAuthor(String v)
 
 
核心控制API
 
方法 说明 
 setResetMode(String mode)  设置重置模式  auto / manual / forbid  
 setTornResettable(boolean resettable)  快捷设置是否可自动重置 
 resetPass()  手动恢复一张全新票据（manual模式调用） 
 isTorn()  查询当前票据是否已经被撕下 
 triggerTorn()  Java代码主动触发撕票 
 triggerResetRequest()  Java主动触发重置请求回调 
 reload()  重载票据页面，刷新UI文字 
 destroy()  销毁控件释放资源，页面销毁必须调用 
 
回调监听
 
java
  
// 撕票完成回调
public interface OnTornListener {
    void onTorn();
}

// manual模式，点击已撕票据回调
public interface OnResetRequestListener {
    void onResetRequest();
}
 
 
⚠️ 注意事项
 
1. Assets资源：需要将前端打包为  web.zip ，放置模块  src/main/assets/  目录下；版本号  VERSION  修改会自动覆盖解压更新资源。
2. 权限：不需要网络权限；会读写应用内部存储目录  getExternalFilesDir / getFilesDir 。
3. 内存泄漏：页面关闭必须调用  destroy() ，关闭webview、停止后台轮询线程，清空监听引用。
4. WebView硬件加速：控件内部开启硬件图层；如果页面出现花屏，可自行修改  setLayerType 。
5. 本地服务单例：进程全局只启动一个 WebServer 实例，多个 WebViewInitializer 共用同一个端口服务。
6. 后台有两个守护轮询线程，300ms轮询一次flag文件，daemon守护线程，进程退出自动销毁。
 
🛠️ 自定义前端页面
 
1. 修改html/css/js源文件；
2. 全部前端文件压缩为zip包，命名  web.zip  放入assets；
3. 修改代码中常量  VERSION ，旧版本缓存会被清空，重新解压zip。
4. JS可调用接口：
-  fetch("/api/torn") ：通知Java层撕票完成；
-  fetch("/api/reset‑request") ：通知Java层重置请求；
- JS 全局对象  window.__ticketApp.issueFreshPass()  Java通过evaluateJavascript调用重置票据。
 
📄 License
 
plaintext
  
MIT License

Copyright (c) 2026

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
