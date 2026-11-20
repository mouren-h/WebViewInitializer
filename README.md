<p align="center">
  <img src="./public/ticket.svg" alt="Ticket-Tear-Android" width="96" />
</p>

<h1 align="center">Ticket-Tear-Android</h1>

<p align="center">
  <strong>Android 票据撕票动画组件｜WebView + 嵌入式本地 HTTP 服务｜纯 Java 零依赖｜完全离线</strong><br/>
  继承 FrameLayout，XML 直接引用；34 项票据文案可配置，支持 auto / manual / forbid 三种重置模式
</p>

<p align="center">
  <img src="https://img.shields.io/badge/language-Java-orange?logo=openjdk" alt="Java">
  <img src="https://img.shields.io/badge/component-CustomView-green" alt="CustomView">
  <img src="https://img.shields.io/badge/WebView-Local%20127.0.0.1-blue" alt="Local-Http">
  <img src="https://img.shields.io/badge/license-MIT-success" alt="MIT">
  <img src="https://img.shields.io/github/stars/yourname/Ticket-Tear-Android?style=social" alt="Stars">
</p>

<p align="center">
  <a href="#简介">简介</a> ·
  <a href="#特性">特性</a> ·
  <a href="#架构">架构</a> ·
  <a href="#快速开始">快速开始</a> ·
  <a href="#api-参考">API 参考</a> ·
  <a href="#设计思路">设计思路</a> ·
  <a href="#自定义前端页面">自定义前端</a> ·
  <a href="#注意事项">注意事项</a> ·
  <a href="#常见问题">FAQ</a> ·
  <a href="#github-仓库配置">GitHub 仓库配置</a>
</p>

---

## 简介

Ticket-Tear-Android 是 Android 平台的票据撕票动画自定义 View。
基于 WebView 渲染 HTML/CSS/JS 实现撕票交互动画；内置极简本地 HTTP 服务，**全程离线，不需要访问外网**。
业务层只调用 Java API 设置票据文字、监听撕票事件，无需关心 WebView 与 JS 之间的通信细节。

> 适用场景：电子入场券、活动票据、优惠券、凭证撕票动画展示。

**核心约束**

| 约束 | 实现方案 |
|---|---|
| **零第三方依赖** | 仅两份 Java 源码，不引入任何 jar/aar，复制即可接入 |
| **完全离线运行** | 本地回环地址 `127.0.0.1` HTTP 服务，不访问外网 |
| **多实例兼容** | WebServer 进程单例，多个 View 实例共享同一个本地服务 |
| **资源版本管理** | `assets/web.zip` 自动解压；版本变更自动清空旧缓存 |
| **Java-JS 双向通信** | 文件 flag 轮询（JS→Java）+ `evaluateJavascript`（Java→JS） |

## 特性

- 🎫 逼真撕票交互动画，HTML/CSS/JS 渲染票据视觉效果
- 📝 **34+ 票据字段**，Java 代码动态设置票据全部文案
- ⚙️ 三种撕票后点击重置模式：`auto` / `manual` / `forbid`
- 📢 Java 层回调：撕票完成回调、重置请求回调
- 📦 assets 中 `web.zip` 自动解压，版本号校验更新前端静态资源
- 🚀 内置极简本地 HTTP 服务器，零依赖、不访问外网
- 🧩 继承 `FrameLayout`，XML 布局直接引用，低侵入接入
- 🛡️ WeakReference 管理活跃 View，避免事件分发内存泄漏
- ⚡ UI 设置防抖：批量 setXxx 仅触发一次页面重载

## 架构

```text
Ticket-Tear-Android
├── TicketTearView.java      # 自定义 View（继承 FrameLayout），封装 WebView 与全部 Java API
├── WebServer.java           # 极简本地 HTTP 服务（进程单例，监听 127.0.0.1）
└── assets/
    └── web.zip              # 前端静态资源包（HTML/CSS/JS），首次启动自动解压到应用私有目录
```

**通信链路**

```text
┌─────────────┐  evaluateJavascript   ┌──────────────────┐
│  Java 层     │ ────────────────────▶ │  WebView (前端)   │
│ TicketTear-  │                       │  HTML / CSS / JS  │
│ View         │ ◀──────────────────── │                  │
└─────────────┘  fetch → 文件flag轮询  └──────────────────┘
        ▲                                    │
        │            127.0.0.1               │
        └────────── WebServer ◀──────────────┘
                 （静态资源 + /api 事件）
```

- **Java → JS**：通过 `evaluateJavascript` 调用前端方法（如重置票据）。
- **JS → Java**：前端 `fetch` 本地 `/api/*` 接口，WebServer 写入文件 flag，Java 侧 Handler 轮询检测（延迟上限约 300ms）。
- **静态资源**：WebView 页面与资源全部由本地 WebServer 提供，不加载任何外网内容。

## 快速开始

### 1. 复制源码

将 `TicketTearView.java` 与 `WebServer.java` 两个文件复制到你的项目（无需任何 Gradle 依赖）。

### 2. 放置前端资源

将前端静态资源打包为 `web.zip`，放入 `src/main/assets/` 目录。

> ⚠️ 若本仓库未附带 `web.zip`：本仓库仅提供 Android Java 封装代码，需自行准备前端静态资源并打包为 `web.zip` 放入 assets 目录（打包与接口约定见[自定义前端页面](#自定义前端页面)）。

### 3. XML 布局引用

```xml
<com.yourpackage.TicketTearView
    android:id="@+id/ticket_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

### 4. Java 代码使用

```java
TicketTearView ticketView = findViewById(R.id.ticket_view);

// 设置票据文案（批量 setXxx 内部防抖，只触发一次页面重载）
ticketView.setTitle("年度技术大会");
ticketView.setSubTitle("VIP 入场券");
ticketView.setTicketNo("NO.20261121-0001");
// ...其余票据字段同理，共 34+ 项

// 撕票后点击重置模式：auto / manual / forbid
ticketView.setResetMode("manual");

// 撕票完成回调
ticketView.setOnTicketTornListener(() -> {
    // 票据已撕开：核销、上报业务状态等
});

// 重置请求回调（manual 模式下由业务层决定是否重发新票）
ticketView.setOnResetRequestListener(() -> {
    ticketView.resetTicket(); // 重新出票
});

// 页面关闭时必须销毁，防止内存泄漏
@Override
protected void onDestroy() {
    super.onDestroy();
    ticketView.destroy();
}
```

> 📌 以上方法名为示例写法，请以仓库内 `TicketTearView.java` 实际公开的 API 为准。

## API 参考

### 票据文案设置（34+ 字段）

所有 `setXxx` 方法均带防抖：同一批次连续调用只触发一次页面重载，可放心批量设置。

| 方法（示例） | 说明 |
|---|---|
| `setTitle(String)` | 票据主标题 |
| `setSubTitle(String)` | 副标题 |
| `setTicketNo(String)` | 票号 |
| `setXxx(String)` … | 其余票据字段，完整列表见源码 |

### 重置模式

| 模式 | 撕票后再次点击票据的行为 |
|---|---|
| `auto` | 自动重置，直接重新出票 |
| `manual` | 触发重置请求回调，由业务层决定是否调用重置 |
| `forbid` | 禁止重置，撕票后保持已撕状态 |

### 事件回调

| 回调（示例） | 触发时机 |
|---|---|
| `setOnTicketTornListener` | 前端撕票动画完成（`/api/torn`） |
| `setOnResetRequestListener` | 前端发起重置请求（`/api/reset-request`） |

### 生命周期

| 方法 | 说明 |
|---|---|
| `destroy()` | **页面关闭必须调用**：关闭 WebView、移除 Handler 回调、清空监听引用 |

> 📌 完整方法签名以 `TicketTearView.java` 源码为准。

## 设计思路

- **为什么用本地 HTTP 而不是 `file://` 或 `loadDataWithBaseURL`？**
  本地 HTTP 提供标准的同源环境，前端可正常使用 `fetch` 等 Web API，资源路径与线上开发一致，前端代码无需为 Android 做特殊改造。
- **为什么 JS→Java 用文件 flag 轮询而不是 `@JavascriptInterface`？**
  不向 JS 暴露 Java 对象，攻击面更小；实现简单，轮询延迟上限约 300ms，对撕票交互体感几乎无影响。
- **为什么 WebServer 做进程单例？**
  多个票据 View 实例共享同一端口与服务，避免端口冲突和重复开销。
- **为什么用 WeakReference 管理活跃 View？**
  WebServer 生命周期长于单个页面，弱引用持有 View 可避免事件分发链路造成内存泄漏。
- **为什么 setXxx 要防抖？**
  票据字段多达 34+，逐字段触发重载会造成明显闪烁与性能浪费；防抖合并为一次重载。

## 自定义前端页面

1. 修改 HTML / CSS / JS 源文件；
2. 将全部前端文件压缩为 zip 包，命名 `web.zip` 放入 assets；
3. 更新代码中的 `VERSION` 常量，旧版本缓存会被清空并重新解压。

前端可调用的接口：

- `fetch("/api/torn")`：通知 Java 层撕票完成；
- `fetch("/api/reset-request")`：通知 Java 层发起重置请求；
- Java 通过 `evaluateJavascript` 调用前端 `window.__ticketApp.issueFreshPass()` 重置票据。

## 注意事项

- **网络权限**：访问 `127.0.0.1` 仍属于网络请求，需要声明 `android.permission.INTERNET`；该权限仅用于回环地址通信，组件本身不访问外网。
- **明文流量**：Android 9（API 28）及以上默认禁止明文 HTTP，需为 `127.0.0.1` 放行 cleartext（如通过 `networkSecurityConfig` 仅对回环地址放开），否则 WebView 无法加载本地页面。
- **存储目录**：`web.zip` 解压到应用私有目录（`getFilesDir()`），无需外部存储权限。
- **内存泄漏**：页面关闭必须调用 `destroy()`，关闭 WebView、移除 Handler 回调、清空监听引用。
- 内置的 `WebServer` 是面向本票据场景的极简实现，**不适用于通用生产级 HTTP 服务**。
- 跨层事件轮询存在最多约 300ms 的延迟，对撕票交互动感几乎不可感知。

## 常见问题

**Q：需要联网吗？会消耗用户流量吗？**
A：不需要。所有资源与接口都在 `127.0.0.1` 回环地址上，完全离线，不产生任何外网流量。

**Q：一个页面能放多个票据 View 吗？**
A：可以。WebServer 为进程单例，多个 View 实例共享同一个本地服务。

**Q：更新了 `web.zip` 但页面还是旧的？**
A：请同步更新代码中的 `VERSION` 常量。版本号不变时会复用已解压的缓存；版本号变更后会清空旧缓存并重新解压。

**Q：撕票后点击票据没反应？**
A：确认重置模式：`forbid` 模式下撕票后禁止重置；`manual` 模式需要业务层在重置请求回调中主动调用重置。

**Q：WebView 白屏 / 加载失败？**
A：依次检查：① `web.zip` 是否已放入 assets；② 是否声明 INTERNET 权限；③ Android 9+ 是否已为 `127.0.0.1` 放行明文流量。

## GitHub 仓库配置

发布仓库时建议完成以下配置：

1. **替换徽章占位符**：将 README 中 Stars 徽章里的 `yourname` 替换为你的 GitHub 用户名。
2. **About 描述**（仓库简介栏）：
   `Android ticket tear-off animation View. WebView + embedded local HTTP server. Pure Java, zero dependency, fully offline.`
3. **Topics 标签**（复制到仓库 Settings → Topics）：

   ```text
   android android-ui java webview custom-view frame-layout ticket entry-ticket coupon animation tear-animation offline local-http-server view
   ```

4. **LICENSE 文件**：在仓库根目录新建 `LICENSE` 文件并粘贴下方 MIT 全文，GitHub 才会自动识别许可证。
5. **前端资源**：若仓库不包含 `web.zip`，请保留快速开始中的说明，提示使用者自行准备前端资源。

## License

```text
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
```

---

<p align="center">Ticket-Tear-Android · 纯 Java 实现 · 完全离线运行 · MIT License</p>

</body>
</html>
