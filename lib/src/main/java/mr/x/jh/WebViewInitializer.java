package mr.x.jh;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 可直接在 XML 布局中使用的 WebView 容器（继承 FrameLayout）。
 *
 * 支持： - 34 项票据文字逐项 setXxx(...) - 撕票后点击票据的重置控制（auto / manual / forbid 三种模式） - 撕票回调、重置请求回调
 */
public class WebViewInitializer extends FrameLayout {

    public interface OnTornListener {
        void onTorn();
    }

    public interface OnResetRequestListener {
        void onResetRequest();
    }

    private static final String VERSION = "v45";
    private static final String WEB_DIR_NAME = "web";
    private static final String VERSION_FILE_NAME = "web_version.txt";
    private static final String ZIP_ASSET_NAME = "web.zip";

    private static WebServer sharedServer;
    private static String sharedPortStr = null;
    private static boolean tornMonitorStarted = false;
    private static boolean resetMonitorStarted = false;
    private static String defaultTornTip = "没有初始化";

    private static final CopyOnWriteArrayList<WeakReference<WebViewInitializer>> ACTIVE = new CopyOnWriteArrayList<WeakReference<WebViewInitializer>>();

    private WebView webView;
    private File webDir;
    private boolean initialized = false;

    private OnTornListener tornListener;
    private Runnable tornRunnable;

    private OnResetRequestListener resetRequestListener;
    private Runnable resetRequestRunnable;

    // UI 文字 JSON
    private String uiTextJson = "{}";

    // 撕下后点击票据的行为："auto" | "manual" | "forbid"
    private String resetMode = "auto";

    // 防抖：一批 setXxx 只触发一次 reload
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Runnable reloadRunnable = new Runnable() {
        @Override
        public void run() {
            loadWithUiText(getPort());
        }
    };

    public WebViewInitializer(Context context) {
        super(context);
        setup(context);
    }

    public WebViewInitializer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public WebViewInitializer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    private void setup(final Context context) {
        webView = new WebView(context);
        webView.setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        webView.setBackgroundColor(Color.BLACK);
        addView(webView);

        ACTIVE.add(new WeakReference<WebViewInitializer>(this));

        post(new Runnable() {
            @Override
            public void run() {
                initialize(context);
            }
        });
    }

    private void initialize(Context context) {
        if (initialized)
            return;
        initialized = true;

        File baseDir = getBaseDir(context);
        webDir = new File(baseDir, WEB_DIR_NAME);
        File versionFile = new File(baseDir, VERSION_FILE_NAME);

        String oldV = readText(versionFile);
        if (!VERSION.equals(oldV) || !webDir.exists()) {
            deleteRecursively(webDir);
            try {
                unzipFromAssets(context, ZIP_ASSET_NAME, webDir);
                writeText(versionFile, VERSION);
            } catch (IOException ignored) {
            }
        }

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.clearCache(true);

        WebSettings ws = webView.getSettings();
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setDatabaseEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        ws.setSupportZoom(false);
        ws.setBuiltInZoomControls(false);
        ws.setDisplayZoomControls(false);
        ws.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(new WebViewClient());

        String portStr = ensureServer(webDir);
        startTornMonitor(context.getApplicationContext(), webDir);
        startResetMonitor(context.getApplicationContext(), webDir);

        loadWithUiText(portStr);
    }

    private void loadWithUiText(String portStr) {
        if (webView == null)
            return;
        long nowTs = System.currentTimeMillis();
        String targetUrl = "http://127.0.0.1:" + portStr + "/index.html?t=" + nowTs;
        try {
            if (uiTextJson != null && !"{}".equals(uiTextJson) && !uiTextJson.isEmpty()) {
                String b64 = Base64.encodeToString(uiTextJson.getBytes("UTF-8"), Base64.NO_WRAP);
                targetUrl += "&ui=" + URLEncoder.encode(b64, "UTF-8");
            }
            targetUrl += "&reset=" + URLEncoder.encode(resetMode, "UTF-8");
        } catch (Exception ignored) {
        }
        webView.loadUrl(targetUrl);
    }

    // ============================================================
    // 重置模式
    // ============================================================

    /**
     * 设置撕票后点击票据时的行为： "auto" 默认，点击立即恢复新票 "manual" 点击只触发 OnResetRequest 回调，由 Java 决定是否重置 "forbid" 点击完全无反应
     */
    public void setResetMode(String mode) {
        if ("auto".equals(mode) || "manual".equals(mode) || "forbid".equals(mode)) {
            this.resetMode = mode;
            scheduleReload();
        }
    }

    /** 快捷：是否允许点击票据自动重置（true → "auto"，false → "forbid"） */
    public void setTornResettable(boolean resettable) {
        setResetMode(resettable ? "auto" : "forbid");
    }

    /** 注册重置请求回调（manual 模式下，用户点击已撕下的票据时触发） */
    public void setOnResetRequest(OnResetRequestListener listener) {
        this.resetRequestListener = listener;
    }

    public void setOnResetRequest(Runnable runnable) {
        this.resetRequestRunnable = runnable;
    }

    /**
     * 主动让当前票据恢复为可撕状态（manual 模式回调里调用）
     */
    public void resetPass() {
        if (webView == null)
            return;
        try {
            webView.evaluateJavascript(
                    "(function(){try{if(window.__ticketApp&&window.__ticketApp.issueFreshPass){window.__ticketApp.issueFreshPass();}}catch(e){}})();",
                    null);
        } catch (Exception ignored) {
        }
    }

    // ============================================================
    // UI 文字设置
    // ============================================================

    public void setUiText(String json) {
        if (json == null || json.trim().isEmpty())
            return;
        this.uiTextJson = json;
        scheduleReload();
    }

    public void resetUiText() {
        this.uiTextJson = "{}";
        scheduleReload();
    }

    public void setHeaderTag(String v) {
        putText("headerTag", v);
    }

    public void setCode(String v) {
        putText("code", v);
    }

    public void setIssuer(String v) {
        putText("issuer", v);
    }

    public void setStock(String v) {
        putText("stock", v);
    }

    public void setEyebrow(String v) {
        putText("eyebrow", v);
    }

    public void setTitle(String v) {
        putText("title", v);
    }

    public void setSubtitle(String v) {
        putText("subtitle", v);
    }

    public void setSec1Title(String v) {
        putText("sec1Title", v);
    }

    public void setSec2Title(String v) {
        putText("sec2Title", v);
    }

    public void setSec3Title(String v) {
        putText("sec3Title", v);
    }

    public void setSec4Title(String v) {
        putText("sec4Title", v);
    }

    public void setVenue(String v) {
        putText("venue", v);
    }

    public void setVenueSub(String v) {
        putText("venueSub", v);
    }

    public void setDate(String v) {
        putText("date", v);
    }

    public void setDateSub(String v) {
        putText("dateSub", v);
    }

    public void setLabel(String v) {
        putText("label", v);
    }

    public void setAccessBadge(String v) {
        putText("accessBadge", v);
    }

    public void setPrice(String v) {
        putText("price", v);
    }

    public void setSerialLabel(String v) {
        putText("serialLabel", v);
    }

    public void setSerialPrefix(String v) {
        putText("serialPrefix", v);
    }

    public void setTearHint(String v) {
        putText("tearHint", v);
    }

    public void setStubTitle(String v) {
        putText("stubTitle", v);
    }

    public void setStubCode(String v) {
        putText("stubCode", v);
    }

    public void setHologramText(String v) {
        putText("hologramText", v);
    }

    public void setTicketFooter(String v) {
        putText("ticketFooter", v);
    }

    public void setBackHeader(String v) {
        putText("backHeader", v);
    }

    public void setBackZonePrefix(String v) {
        putText("backZonePrefix", v);
    }

    public void setZone(String v) {
        putText("zone", v);
    }

    public void setBackCertified(String v) {
        putText("backCertified", v);
    }

    public void setBackCertifiedSub(String v) {
        putText("backCertifiedSub", v);
    }

    public void setBackInfo1(String v) {
        putText("backInfo1", v);
    }

    public void setBackInfo2(String v) {
        putText("backInfo2", v);
    }

    public void setBackInfo3(String v) {
        putText("backInfo3", v);
    }

    public void setBackInfo4(String v) {
        putText("backInfo4", v);
    }

    public void setBackInfo5(String v) {
        putText("backInfo5", v);
    }

    public void setAuthor(String v) {
        putText("author", v);
    }

    private synchronized void putText(String key, String value) {
        try {
            JSONObject obj = new JSONObject(uiTextJson);
            obj.put(key, value == null ? "" : value);
            uiTextJson = obj.toString();
            scheduleReload();
        } catch (Exception ignored) {
        }
    }

    private void scheduleReload() {
        if (!initialized)
            return;
        mainHandler.removeCallbacks(reloadRunnable);
        mainHandler.postDelayed(reloadRunnable, 100);
    }

    // ============================================================
    // 基础 API
    // ============================================================

    public WebView getWebView() {
        return webView;
    }

    public String getPort() {
        return sharedPortStr != null ? sharedPortStr : "8888";
    }

    public File getWebDir() {
        return webDir;
    }

    public void reload() {
        if (webView == null)
            return;
        loadWithUiText(getPort());
    }

    public void loadUrl(String url) {
        if (webView != null && url != null)
            webView.loadUrl(url);
    }

    public void loadLocal(String page) {
        if (webView == null || page == null)
            return;
        String p = page.startsWith("/") ? page.substring(1) : page;
        long nowTs = System.currentTimeMillis();
        webView.loadUrl("http://127.0.0.1:" + getPort() + "/" + p + "?t=" + nowTs);
    }

    public void goBack() {
        if (webView != null && webView.canGoBack())
            webView.goBack();
    }

    public boolean canGoBack() {
        return webView != null && webView.canGoBack();
    }

    public boolean isTorn() {
        if (webDir == null)
            return false;
        return new File(webDir, "torn.flag").exists();
    }

    public static void setTornTip(String tip) {
        if (tip != null && tip.length() > 0)
            defaultTornTip = tip;
    }

    public void setOnTornListener(OnTornListener listener) {
        this.tornListener = listener;
    }

    public void setOnTorn(Runnable runnable) {
        this.tornRunnable = runnable;
    }

    public void triggerTorn() {
        if (webDir == null)
            return;
        try {
            new File(webDir, "torn.flag").createNewFile();
        } catch (IOException ignored) {
        }
    }

    /** 主动触发一次重置请求（等价于 manual 模式下用户点击已撕下票据） */
    public void triggerResetRequest() {
        if (webDir == null)
            return;
        try {
            new File(webDir, "reset_request.flag").createNewFile();
        } catch (IOException ignored) {
        }
    }

    public void destroy() {
        ACTIVE.remove(this);
        mainHandler.removeCallbacks(reloadRunnable);
        if (webView != null) {
            try {
                removeView(webView);
                webView.stopLoading();
                webView.loadUrl("about:blank");
                webView.clearHistory();
                webView.clearCache(true);
                webView.destroy();
            } catch (Exception ignored) {
            }
            webView = null;
        }
        tornListener = null;
        tornRunnable = null;
        resetRequestListener = null;
        resetRequestRunnable = null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ACTIVE.remove(this);
    }

    // ============================================================
    // 撕票 / 重置请求分发
    // ============================================================

    private static void dispatchTorn() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Iterator<WeakReference<WebViewInitializer>> it = ACTIVE.iterator();
                WebViewInitializer first = null;
                while (it.hasNext()) {
                    WeakReference<WebViewInitializer> ref = it.next();
                    WebViewInitializer w = ref.get();
                    if (w == null)
                        it.remove();
                    else if (first == null)
                        first = w;
                }
                if (first != null) {
                    Toast.makeText(first.getContext().getApplicationContext(), defaultTornTip,
                            Toast.LENGTH_SHORT).show();
                }
                for (WeakReference<WebViewInitializer> ref : ACTIVE) {
                    WebViewInitializer w = ref.get();
                    if (w == null)
                        continue;
                    try {
                        if (w.tornListener != null)
                            w.tornListener.onTorn();
                    } catch (Exception ignored) {
                    }
                    try {
                        if (w.tornRunnable != null)
                            w.tornRunnable.run();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    private static void dispatchResetRequest() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (WeakReference<WebViewInitializer> ref : ACTIVE) {
                    WebViewInitializer w = ref.get();
                    if (w == null)
                        continue;
                    try {
                        if (w.resetRequestListener != null) {
                            w.resetRequestListener.onResetRequest();
                        }
                    } catch (Exception ignored) {
                    }
                    try {
                        if (w.resetRequestRunnable != null) {
                            w.resetRequestRunnable.run();
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    private static synchronized String ensureServer(File webDir) {
        if (sharedServer == null || !sharedServer.isRunning()) {
            sharedServer = new WebServer();
            sharedPortStr = sharedServer.start(webDir.getAbsolutePath());
        }
        if (sharedPortStr == null)
            sharedPortStr = "8888";
        return sharedPortStr;
    }

    private static synchronized void startTornMonitor(Context appCtx, final File webDir) {
        if (tornMonitorStarted)
            return;
        tornMonitorStarted = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    File flagFile = new File(webDir, "torn.flag");
                    if (flagFile.exists()) {
                        flagFile.delete();
                        dispatchTorn();
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private static synchronized void startResetMonitor(Context appCtx, final File webDir) {
        if (resetMonitorStarted)
            return;
        resetMonitorStarted = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    File flagFile = new File(webDir, "reset_request.flag");
                    if (flagFile.exists()) {
                        flagFile.delete();
                        dispatchResetRequest();
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    // ============================================================
    // 文件工具
    // ============================================================

    private static File getBaseDir(Context context) {
        File baseDir = context.getExternalFilesDir(null);
        if (baseDir == null)
            baseDir = context.getFilesDir();
        return baseDir;
    }

    private static void unzipFromAssets(Context context, String assetName, File destDir)
            throws IOException {
        if (!destDir.exists() && !destDir.mkdirs())
            throw new IOException("Cannot create dest dir: " + destDir);
        String destCanonicalPath = destDir.getCanonicalPath();
        ZipInputStream zis = null;
        try {
            InputStream is = context.getAssets().open(assetName);
            zis = new ZipInputStream(is);
            ZipEntry entry;
            byte[] buffer = new byte[8192];
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(destDir, entry.getName());
                String outCanonicalPath = outFile.getCanonicalPath();
                if (!outCanonicalPath.equals(destCanonicalPath)
                        && !outCanonicalPath.startsWith(destCanonicalPath + File.separator)) {
                    throw new IOException("Bad zip entry: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    if (!outFile.exists() && !outFile.mkdirs())
                        throw new IOException("Cannot create dir: " + outFile);
                } else {
                    File parent = outFile.getParentFile();
                    if (parent != null && !parent.exists() && !parent.mkdirs())
                        throw new IOException("Cannot create parent dir: " + parent);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(outFile);
                        int len;
                        while ((len = zis.read(buffer)) != -1)
                            fos.write(buffer, 0, len);
                        fos.flush();
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                }
                zis.closeEntry();
            }
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static void deleteRecursively(File file) {
        if (file == null || !file.exists())
            return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null)
                for (File child : children)
                    deleteRecursively(child);
        }
        file.delete();
    }

    private static String readText(File file) {
        if (file == null || !file.exists())
            return null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (!first)
                    sb.append('\n');
                sb.append(line);
                first = false;
            }
            return sb.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static void writeText(File file, String text) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs())
            throw new IOException("Cannot create parent dir: " + parent);
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            writer.write(text);
            writer.flush();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}