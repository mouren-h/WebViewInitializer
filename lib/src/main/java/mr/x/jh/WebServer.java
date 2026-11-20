package mr.x.jh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 简易静态文件 HTTP 服务器
 *
 * 说明： 1. start(rootDir) 返回实际监听的端口号字符串； 2. 访问 /api/torn 会生成 torn.flag 文件； 3. 访问 /api/reset-request 会生成 reset_request.flag 文件； 4. 其余请求按 rootDir 下的静态资源返回（带 no-cache 头）； 5. 不打印任何控制台数据。
 */
public class WebServer {

    private ServerSocket serverSocket = null;
    private volatile boolean isRunning = false;
    private int currentPort = 8888;

    /**
     * 启动服务，返回实际监听端口
     */
    public String start(String rootDir) {
        if (isRunning && serverSocket != null && !serverSocket.isClosed()) {
            return String.valueOf(currentPort);
        }

        if (rootDir == null) {
            rootDir = "";
        }
        final String fRootDir = rootDir;

        int[] ports = { 8888, 8889, 8890, 8899, 9988 };
        for (int i = 0; i < ports.length; i++) {
            final int p = ports[i];
            try {
                ServerSocket ss = new ServerSocket();
                ss.setReuseAddress(true);
                ss.bind(new InetSocketAddress(p));

                serverSocket = ss;
                currentPort = p;
                isRunning = true;

                Thread t = new Thread(new Runnable() {
                    public void run() {
                        while (isRunning) {
                            try {
                                final Socket socket = serverSocket.accept();
                                Thread worker = new Thread(new Runnable() {
                                    public void run() {
                                        handle(socket, fRootDir);
                                    }
                                });
                                worker.setDaemon(true);
                                worker.start();
                            } catch (Exception e) {
                                if (!isRunning) {
                                    break;
                                }
                                if (serverSocket == null || serverSocket.isClosed()) {
                                    break;
                                }
                            }
                        }
                    }
                });
                t.setDaemon(true);
                t.start();
                return String.valueOf(p);
            } catch (Exception e) {
                try {
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                } catch (Exception ex) {
                    // ignore
                }
                serverSocket = null;
                isRunning = false;
            }
        }
        return "8888";
    }

    /**
     * 停止服务
     */
    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            // ignore
        }
        serverSocket = null;
    }

    /**
     * 返回当前端口
     */
    public int getCurrentPort() {
        return currentPort;
    }

    /**
     * 判断服务是否在运行
     */
    public boolean isRunning() {
        return isRunning && serverSocket != null && !serverSocket.isClosed();
    }

    /**
     * 处理单个 HTTP 请求
     */
    public void handle(Socket socket, String rootDir) {
        try {
            InputStream rawIn = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(rawIn));
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.trim().isEmpty()) {
                socket.close();
                return;
            }

            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                socket.close();
                return;
            }

            String path = parts[1];
            if (path.contains("?")) {
                path = path.substring(0, path.indexOf("?"));
            }
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            String relPath = path;
            while (relPath.startsWith("/")) {
                relPath = relPath.substring(1);
            }

            // 撕券回调接口（web 端撕券时调用）
            if (relPath.startsWith("api/torn")) {
                try {
                    File flagFile = new File(rootDir, "torn.flag");
                    flagFile.createNewFile();
                } catch (Exception fe) {
                    // ignore
                }

                OutputStream out = socket.getOutputStream();
                String okResp = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/plain; charset=utf-8\r\n"
                        + "Cache-Control: no-cache, no-store, must-revalidate\r\n"
                        + "Access-Control-Allow-Origin: *\r\n" + "Connection: close\r\n\r\n" + "OK";
                out.write(okResp.getBytes("UTF-8"));
                out.flush();
                socket.close();
                return;
            }

            // 撕下后点击票据时的重置请求接口（manual 模式下由 JS 调用）
            if (relPath.startsWith("api/reset-request")) {
                try {
                    File flagFile = new File(rootDir, "reset_request.flag");
                    flagFile.createNewFile();
                } catch (Exception fe) {
                    // ignore
                }

                OutputStream out = socket.getOutputStream();
                String okResp = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/plain; charset=utf-8\r\n"
                        + "Cache-Control: no-cache, no-store, must-revalidate\r\n"
                        + "Access-Control-Allow-Origin: *\r\n" + "Connection: close\r\n\r\n" + "OK";
                out.write(okResp.getBytes("UTF-8"));
                out.flush();
                socket.close();
                return;
            }

            File targetFile = new File(rootDir, relPath);
            if (!targetFile.exists() || targetFile.isDirectory()) {
                OutputStream out = socket.getOutputStream();
                String notFound = "HTTP/1.1 404 Not Found\r\n" + "Content-Length: 0\r\n"
                        + "Connection: close\r\n\r\n";
                out.write(notFound.getBytes("UTF-8"));
                out.flush();
                socket.close();
                return;
            }

            String mimeType = "application/octet-stream";
            String lower = targetFile.getName().toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) {
                mimeType = "text/html; charset=utf-8";
            } else if (lower.endsWith(".css")) {
                mimeType = "text/css; charset=utf-8";
            } else if (lower.endsWith(".js")) {
                mimeType = "application/javascript; charset=utf-8";
            } else if (lower.endsWith(".png")) {
                mimeType = "image/png";
            } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                mimeType = "image/jpeg";
            } else if (lower.endsWith(".webp")) {
                mimeType = "image/webp";
            } else if (lower.endsWith(".svg")) {
                mimeType = "image/svg+xml";
            } else if (lower.endsWith(".json")) {
                mimeType = "application/json; charset=utf-8";
            } else if (lower.endsWith(".woff2")) {
                mimeType = "font/woff2";
            } else if (lower.endsWith(".woff")) {
                mimeType = "font/woff";
            } else if (lower.endsWith(".mp3")) {
                mimeType = "audio/mpeg";
            } else if (lower.endsWith(".wav")) {
                mimeType = "audio/wav";
            }

            long length = targetFile.length();
            OutputStream out = socket.getOutputStream();
            String header = "HTTP/1.1 200 OK\r\n" + "Content-Type: " + mimeType + "\r\n"
                    + "Content-Length: " + length + "\r\n"
                    + "Cache-Control: no-cache, no-store, must-revalidate, max-age=0\r\n"
                    + "Pragma: no-cache\r\n" + "Expires: 0\r\n"
                    + "Access-Control-Allow-Origin: *\r\n" + "Connection: close\r\n\r\n";
            out.write(header.getBytes("UTF-8"));

            FileInputStream fis = new FileInputStream(targetFile);
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            fis.close();
            out.flush();
            socket.close();
        } catch (Exception e) {
            try {
                socket.close();
            } catch (Exception ex) {
                // ignore
            }
        }
    }
}