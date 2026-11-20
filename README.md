<p align="center">
  <img src="./public/ticket.svg" alt="Ticket‑Tear‑Android" width="96" />
</p>
<h1 align="center">Ticket‑Tear‑Android</h1>
<p align="center">
  <strong>Android 票据撕票动画组件｜WebView + 嵌入式本地HTTP服务｜纯Java零依赖｜完全离线</strong><br/>
  继承 FrameLayout，XML直接引用；34项票据文案可配置，支持 auto / manual / forbid 三种重置模式
</p>
<p align="center">
  <img src="https://img.shields.io/badge/language-Java-orange?logo=openjdk" alt="Java">
  <img src="https://img.shields.io/badge/component-CustomView-green" alt="CustomView">
  <img src="https://img.shields.io/badge/WebView‑Local‑127.0.0.1-blue" alt="Local‑Http">
  <img src="https://img.shields.io/badge/license‑MIT‑success" alt="MIT">
  <img src="https://img.shields.io/github/stars/yourname/Ticket‑Tear‑Android?style=social" alt="Stars">
</p>
<p align="center">
  <a href="#简介">简介</a> ·
  <a href="#特性">特性</a> ·
  <a href="#架构">架构</a> ·
  <a href="#快速开始">快速开始</a> ·
  <a href="#api参考">API参考</a> ·
  <a href="#设计思路">设计思路</a> ·
  <a href="#自定义前端">自定义前端</a> ·
  <a href="#注意事项">注意事项</a> ·
  <a href="#常见问题">FAQ</a> ·
  <a href="#github仓库配置">GitHub仓库配置</a>
</p>

---

## 简介
Ticket‑Tear‑Android 是 Android 平台票据撕票动画自定义 View。
基于 WebView 渲染 HTML/CSS/JS 实现撕票交互动画；内置极简本地 HTTP 服务，**全程离线，不需要网络权限**。
业务层只调用 Java API 设置票据文字、监听撕票事件，无需关心 WebView 与 JS 之间的通信细节。

> 适用场景：电子入场券、活动票据、优惠券、凭证撕票动画展示。

**核心约束**
| 约束 | 实现方案 |
|---|---|
| **零第三方依赖** | 仅两份 Java 源码，不引入任何 jar/aar，复制即可接入 |
| **完全离线运行** | 本地回环地址 `127.0.0.1` HTTP服务，不访问外网 |
| **多实例兼容** | WebServer进程单例，多个View实例共享同一个本地服务 |
| **资源版本管理** | assets/web.zip 自动解压；版本变更自动清空旧缓存 |
| **Java‑JS双向通信** | 文件flag轮询（JS→Java） + evaluateJavascript（Java→JS） |

## ✨ 特性
- 🎫 逼真撕票交互动画，HTML/CSS/JS渲染票据视觉效果
- 📝 **34+票据字段**，Java代码动态设置票据全部文案
- ⚙️ 三种撕票后点击重置模式：`auto` / `manual` / `forbid`
- 📢 Java层回调：撕票完成回调、重置请求回调
- 📦 assets中`web.zip`自动解压，版本号校验更新前端静态资源
- 🚀 内置极简本地HTTP服务器，零依赖、无网络权限
- 🧩 继承`FrameLayout`，XML布局直接引用，低侵入接入
- 🛡️ WeakReference 管理活跃View，避免事件分发内存泄漏
- ⚡ UI设置防抖：批量setXxx仅执行一次页面重载

## 📦 架构
Dir</code>）。</li>
  <li><strong>内存泄漏</strong>：页面关闭必须调用 <code>destroy()</code>，关闭 WebView、移除 Handler 回调、清空监听引用。</li>
  <li>内置的 <code>WebServer</code> 是面向本票据场景的极简实现，<strong>不适用于通用生产级 HTTP 服务</strong>。</li>
  <li>跨层事件轮询存在最多约 300ms 的延迟，对撕票交互动感几乎不可感知。</li>
</ol>

<h2>🛠️ 自定义前端页面</h2>
<ol>
  <li>修改 HTML / CSS / JS 源文件；</li>
  <li>将全部前端文件压缩为 zip 包，命名 <code>web.zip</code> 放入 assets；</li>
  <li>更新代码中的 <code>VERSION</code> 常量，旧版本缓存会被清空并重新解压。</li>
</ol>
<p>前端可调用的接口：</p>
<ul>
  <li><code>fetch("/api/torn")</code>：通知 Java 层撕票完成；</li>
  <li><code>fetch("/api/reset-request")</code>：通知 Java 层发起重置请求；</li>
  <li>Java 通过 <code>evaluateJavascript</code> 调用前端 <code>window.__ticketApp.issueFreshPass()</code> 重置票据。</li>
</ul>

<h2>🏷️ GitHub Topics 标签</h2>
<div class="tag-list">
  <span class="tag">android</span>
  <span class="tag">android-ui</span>
  <span class="tag">java</span>
  <span class="tag">webview</span>
  <span class="tag">custom-view</span>
  <span class="tag">frame-layout</span>
  <span class="tag">ticket</span>
  <span class="tag">entry-ticket</span>
  <span class="tag">coupon</span>
  <span class="tag">animation</span>
  <span class="tag">tear-animation</span>
  <span class="tag">offline</span>
  <span class="tag">local-http-server</span>
  <span class="tag">view</span>
</div>

<h2>📄 License</h2>
<pre><code>MIT License

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
SOFTWARE.</code></pre>

<div class="note">
  <strong>提示：</strong>若仓库不包含 <code>web.zip</code> 前端资源，请补充说明：本仓库仅提供 Android Java 封装代码，需自行准备前端静态资源并打包为 <code>web.zip</code> 放入 assets 目录。
</div>

<footer>
  Ticket-Tear-Android · 纯 Java 实现 · 完全离线运行 · MIT License
</footer>

</body>
</html>
