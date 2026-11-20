<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Ticket-Tear-Android · 票据撕票动画组件</title>
<style>
  :root{
    --bg:#ffffff; --fg:#1f2328; --muted:#59636e; --border:#d1d9e0;
    --accent:#0969da; --accent-bg:#ddf4ff; --code-bg:#f6f8fa;
    --warn-bg:#fff8c5; --warn-border:#d4a72c; --green:#1a7f37;
  }
  *{box-sizing:border-box;}
  body{
    margin:0; padding:40px 24px; background:var(--bg); color:var(--fg);
    font-family:-apple-system,BlinkMacSystemFont,"Segoe UI","Noto Sans SC",Helvetica,Arial,sans-serif;
    line-height:1.7; max-width:960px; margin-left:auto; margin-right:auto;
  }
  h1{font-size:2.1em;margin:0 0 8px;border-bottom:1px solid var(--border);padding-bottom:12px;}
  h2{font-size:1.5em;margin-top:40px;border-bottom:1px solid var(--border);padding-bottom:6px;}
  h3{font-size:1.15em;margin-top:24px;}
  p{margin:10px 0;}
  .subtitle{color:var(--muted);font-size:1.05em;margin:0 0 24px;}
  .badges{margin:12px 0 24px;}
  .badge{
    display:inline-block;background:var(--accent-bg);color:var(--accent);
    border:1px solid var(--accent);border-radius:20px;padding:2px 12px;
    font-size:.85em;margin:0 6px 6px 0;
  }
  .features{display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:14px;margin:18px 0;}
  .card{border:1px solid var(--border);border-radius:10px;padding:16px;background:#fafbfc;}
  .card h3{margin-top:0;color:var(--accent);}
  pre{
    background:var(--code-bg);border:1px solid var(--border);border-radius:8px;
    padding:14px;overflow-x:auto;font-size:.88em;line-height:1.55;
    font-family:"SFMono-Regular",Consolas,"Liberation Mono",Menlo,monospace;
  }
  code{background:var(--code-bg);border:1px solid var(--border);border-radius:4px;padding:1px 5px;font-size:.9em;}
  pre code{background:none;border:none;padding:0;}
  table{border-collapse:collapse;width:100%;margin:16px 0;font-size:.92em;}
  th,td{border:1px solid var(--border);padding:8px 12px;text-align:left;vertical-align:top;}
  th{background:var(--code-bg);}
  .note{background:var(--warn-bg);border-left:4px solid var(--warn-border);padding:12px 16px;border-radius:6px;margin:16px 0;}
  .tag-list{display:flex;flex-wrap:wrap;gap:8px;margin:12px 0;}
  .tag{background:var(--code-bg);border:1px solid var(--border);border-radius:6px;padding:3px 10px;font-size:.85em;color:var(--muted);}
  ul,ol{padding-left:24px;}
  li{margin:6px 0;}
  .desc-box{background:var(--code-bg);border:1px dashed var(--border);border-radius:8px;padding:14px 16px;margin:16px 0;}
  .desc-box code{display:block;white-space:normal;}
  footer{margin-top:48px;color:var(--muted);font-size:.85em;border-top:1px solid var(--border);padding-top:16px;}
</style>
</head>
<body>

<h1>🎫 Ticket-Tear-Android</h1>
<p class="subtitle">Android 端票据撕票动画组件，基于 WebView + 本地嵌入式静态 HTTP 服务器，纯 Java、完全离线运行，支持自定义票据文案与多种重置模式。</p>

<div class="badges">
  <span class="badge">Java</span>
  <span class="badge">Android Custom View</span>
  <span class="badge">WebView</span>
  <span class="badge">Local HTTP Server</span>
  <span class="badge">Offline</span>
</div>

<div class="desc-box">
  <strong>GitHub 仓库描述（Description，直接复制）：</strong>
  <code>Android ticket tear animation component based on WebView and an embedded local HTTP server, fully offline, Java only. Customizable ticket text and multiple reset modes.</code>
</div>

<h2>✨ 项目简介</h2>
<p>本组件是一个可直接在 XML 布局中使用的票据撕票 UI 控件（继承 <code>FrameLayout</code>）。内部打包静态网页资源，启动一个纯 Java 实现的本地小型 HTTP 服务，由 WebView 访问 <code>127.0.0.1</code> 渲染票据并完成撕票交互动画。</p>

<div class="features">
  <div class="card">
    <h3>🎬 撕票动画</h3>
    <p>票据样式与撕票动画由前端 HTML / CSS / JS 渲染，视觉效果逼真，动画流畅。</p>
  </div>
  <div class="card">
    <h3>📝 34 项文案可配置</h3>
    <p>标题、票号、场地、日期、价格、副券、票背信息等全部字段均可通过 Java 代码逐项设置。</p>
  </div>
  <div class="card">
    <h3>⚙️ 三种重置模式</h3>
    <p><code>auto</code> 点击自动恢复新票；<code>manual</code> 仅回调，由业务代码决定是否重置；<code>forbid</code> 点击无反应。</p>
  </div>
  <div class="card">
    <h3>📢 事件回调</h3>
    <p>提供撕票完成回调与重置请求回调，支持监听器接口与 Runnable 两种注册方式。</p>
  </div>
  <div class="card">
    <h3>📦 资源版本管理</h3>
    <p>前端资源以 <code>web.zip</code> 存放于 assets，初始化自动解压并按版本号校验更新缓存。</p>
  </div>
  <div class="card">
    <h3>🚀 零三方依赖</h3>
    <p>不引入任何第三方库，仅使用 Android / JDK 原生 API，监听本地回环地址，无需网络权限。</p>
  </div>
</div>

<h2>📦 架构说明</h2>
<pre><code>mr.x.jh
├── WebViewInitializer.java  # 对外暴露的 View 控件（继承 FrameLayout），业务直接使用
└── WebServer.java           # 纯 Java 轻量本地静态 HTTP 服务器

src/main/assets
└── web.zip                  # html / css / js 票据前端资源包（需自行准备）</code></pre>

<ul>
  <li><strong>WebViewInitializer</strong>：封装 WebView，负责解压 <code>web.zip</code>、启动本地服务、拼装 UI 文案、管理事件监听与生命周期释放。</li>
  <li><strong>WebServer</strong>：轻量静态文件服务，监听本机回环端口（依次尝试 8888 / 8889 / 8890 / 8899 / 9988），提供静态资源与两个事件接口：
    <ul>
      <li><code>/api/torn</code>：前端撕票完成，生成 <code>torn.flag</code>，Java 层轮询检测后分发撕票回调。</li>
      <li><code>/api/reset-request</code>：用户点击已撕下票据（manual 模式），生成 <code>reset_request.flag</code>，触发重置请求回调。</li>
    </ul>
  </li>
  <li><strong>跨层通信</strong>：JS → Java 通过本地 HTTP 接口 + flag 文件轮询；Java → JS 通过 <code>evaluateJavascript</code> 调用前端 <code>window.__ticketApp.issueFreshPass()</code>。</li>
</ul>

<h2>🚀 快速接入</h2>

<h3>1. 布局 XML</h3>
<pre><code>&lt;mr.x.jh.WebViewInitializer
    android:id="@+id/ticketView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" /&gt;</code></pre>

<h3>2. Activity / Fragment Java 代码</h3>
<pre><code>WebViewInitializer ticketView = findViewById(R.id.ticketView);

// 设置票据文字
ticketView.setTitle("活动入场券");
ticketView.setCode("NO-20261120-0001");
ticketView.setVenue("某某大会会场");
ticketView.setDate("2026-11-20");
ticketView.setPrice("免费");

// 设置重置模式：auto / manual / forbid
ticketView.setResetMode("manual");

// 监听撕票完成
ticketView.setOnTornListener(() -&gt; {
    // 撕票完成后的业务逻辑
});

// manual 模式：监听用户点击已撕下票据
ticketView.setOnResetRequest(() -&gt; {
    // 业务判断是否允许重置票据
    ticketView.resetPass(); // 恢复一张全新票据
});</code></pre>

<h3>3. 生命周期销毁</h3>
<div class="note">页面销毁时<strong>务必调用 <code>destroy()</code></strong>，释放 WebView 与后台监听资源，避免内存泄漏。</div>
<pre><code>@Override
protected void onDestroy() {
    super.onDestroy();
    if (ticketView != null) {
        ticketView.destroy();
    }
}</code></pre>

<h2>📚 API 文档</h2>

<h3>核心控制方法</h3>
<table>
  <thead>
    <tr><th>方法</th><th>说明</th></tr>
  </thead>
  <tbody>
    <tr><td><code>setResetMode(String mode)</code></td><td>设置重置模式：<code>auto</code> / <code>manual</code> / <code>forbid</code></td></tr>
    <tr><td><code>setTornResettable(boolean resettable)</code></td><td>快捷设置是否允许点击自动重置（true → auto，false → forbid）</td></tr>
    <tr><td><code>resetPass()</code></td><td>手动恢复一张全新票据（manual 模式回调中调用）</td></tr>
    <tr><td><code>isTorn()</code></td><td>查询当前票据是否已被撕下</td></tr>
    <tr><td><code>triggerTorn()</code></td><td>Java 层主动触发撕票事件</td></tr>
    <tr><td><code>triggerResetRequest()</code></td><td>Java 层主动触发重置请求回调</td></tr>
    <tr><td><code>reload()</code></td><td>重载票据页面，刷新 UI 文案</td></tr>
    <tr><td><code>loadLocal(String page)</code></td><td>加载本地服务中的指定页面</td></tr>
    <tr><td><code>goBack() / canGoBack()</code></td><td>WebView 历史回退相关</td></tr>
    <tr><td><code>getWebView()</code></td><td>获取内部 WebView 实例</td></tr>
    <tr><td><code>destroy()</code></td><td>销毁控件、释放资源，页面销毁必须调用</td></tr>
  </tbody>
</table>

<h3>回调监听</h3>
<pre><code>// 撕票完成回调
public interface OnTornListener {
    void onTorn();
}

// manual 模式下，点击已撕下票据回调
public interface OnResetRequestListener {
    void onResetRequest();
}</code></pre>
<p>同时提供 <code>setOnTorn(Runnable)</code> 与 <code>setOnResetRequest(Runnable)</code> 两种 Runnable 形式的注册方式。</p>

<h3>票据文案 setXxx() 方法</h3>
<pre><code>setHeaderTag(String v)
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
setAuthor(String v)</code></pre>

<h2>🧠 设计思路</h2>

<h3>为什么手写本地 HTTP 服务，而不引入第三方服务器库？</h3>
<ul>
  <li><strong>零三方依赖</strong>：仅使用 JDK 原生 <code>ServerSocket</code> / <code>Socket</code>，拷贝源码即可接入，体积可控。</li>
  <li><strong>够用就好</strong>：只需提供静态资源 + 两个简单事件接口，不需要完整 Web 服务器能力。</li>
  <li><strong>离线运行</strong>：服务绑定本机回环地址 <code>127.0.0.1</code>，不需要网络权限。</li>
  <li><strong>端口自动探测</strong>：优先使用 8888，被占用时自动切换备选端口。</li>
</ul>

<h3>为什么用 flag 文件轮询，而不用 addJavascriptInterface 桥接？</h3>
<ul>
  <li>WebView 页面刷新、重载或多实例共存时，JS 桥接对象容易丢失；HTTP 接口 + 文件轮询的方式更稳定。</li>
  <li>前端侧只使用标准 <code>fetch()</code> API，调试方便，不依赖特殊绑定对象。</li>
  <li>两个守护线程每 300ms 轮询一次 flag 文件，检测到后删除文件并在主线程分发回调；线程为 daemon，进程退出自动结束。</li>
</ul>

<h3>其他关键设计</h3>
<ul>
  <li><strong>进程内单例服务</strong>：全局只启动一个 <code>WebServer</code> 实例，多个控件共用同一端口，减少线程与 Socket 开销。</li>
  <li><strong>资源版本校验</strong>：assets 中的 <code>web.zip</code> 解压到应用私有目录，记录 <code>web_version.txt</code>；版本号变更时自动清空旧目录并重新解压。</li>
  <li><strong>文案防抖刷新</strong>：连续多次调用 <code>setXxx()</code> 时，通过 Handler 延迟 100ms 合并为一次页面刷新，避免频繁重载 WebView。</li>
  <li><strong>弱引用管理实例</strong>：使用 <code>WeakReference</code> + <code>CopyOnWriteArrayList</code> 保存活跃控件，已销毁的 View 会被 GC 自动回收，避免内存泄漏。</li>
  <li><strong>解压路径校验</strong>：解压 zip 时校验条目路径，防止目录穿越（zip slip）风险。</li>
</ul>

<h2>⚠️ 注意事项</h2>
<ol>
  <li><strong>前端资源</strong>：需将 HTML / CSS / JS 打包为 <code>web.zip</code>，放入模块 <code>src/main/assets/</code>；修改代码中 <code>VERSION</code> 常量后会自动覆盖解压更新缓存。</li>
  <li><strong>权限</strong>：不需要网络权限；读写应用私有目录（<code>getExternalFilesDir</code> / <code>getFilesDir</code>）。</li>
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
