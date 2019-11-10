package com.finance.biiid.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SMWebView extends WebView {

    private static final boolean DEBUG = true;
    private static final String VAR_ARG_PREFIX = "arg";
    private static final String MSG_PROMPT_HEADER = "MyApp:";
    private static final String KEY_INTERFACE_NAME = "obj";
    private static final String KEY_FUNCTION_NAME = "func";
    private static final String KEY_ARG_ARRAY = "args";
    private static final String[] mFilterMethods = {"getClass", "hashCode",
            "notify", "notifyAll", "equals", "toString", "wait",};
    public OnScrollChangeListener listener;
    private HashMap<String, Object> mJsInterfaceMap = new HashMap<String, Object>();
    private String mJsStringCache = null;
    private Context mContext;

    public SMWebView(Context context) {
        super(context);
        this.mContext = context;
        initSettings();
    }

    public SMWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initSettings();
    }

    public SMWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initSettings();
    }

    public SMWebView(Context context, AttributeSet attrs, int defStyle,
                     boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
        this.mContext = context;
        initSettings();
    }

    private void initSettings() {
        setTag("tag_sm_WebView");

        removeSearchBoxImpl(); // 删除掉Android默认注册的JS接口

        //默认参数
        setNetworkAvailable(true);
        setVerticalScrollBarEnabled(false);

        WebSettings settings = this.getSettings();
        settings.setSupportZoom(true);// 设置可以支持缩放
        //自适应屏幕
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);//扩大比例的缩放
        settings.setLoadWithOverviewMode(true);
        //settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setSaveFormData(true);
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
//        settings.setUserAgentString(settings.getUserAgentString() + "sunmi");//自定义ua

        if (0 != (mContext.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        settings.setPluginState(WebSettings.PluginState.ON);

        // 设置appCache大小
        //        settings.setAppCacheMaxSize(8 * 1024 * 1024);
        // 设置启用的viewport meta
        // 设置缓存路径
        //        settings.setAppCachePath(cacheDir);
        // 设置数据库路径
        //        settings.setDatabasePath(dbDir);
        // 地理db存储路径
        //        settings.setGeolocationDatabasePath(dbDir);
        // 设置数据库启用
        //        settings.setDatabaseEnabled(true);
        // 启用地理定位
        //        settings.setGeolocationEnabled(true);
        // 设置启用applicationCache
        //        settings.setAppCacheEnabled(true);
        // 设置从缓存中加载，如果缓存中没有再从网络上下载
        // settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 启用硬件加速
        // settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 设置js能够打开一个新的webview进行页面加载，可以调window.open()
        //        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 支持hover伪类和active伪类 android4.3有用
        // settings.setLightTouchEnabled(true);
        // 设置的WebView是否能顺利过渡，而平移或缩放或在主办的WebView窗口没有焦点。
        // 如果这是真的，web视图会选择一个解决方案，以最大限度地提高性能。
        // 例如web视图的内容不得在过渡期间进行更新。如果是假的，web视图将保持其保真度。
        // 默认值是false。 API 17 4.2.2
        // settings.setEnableSmoothTransition(true);

        // this.setChildrenDrawnWithCacheEnabled(true);
        // this.setChildrenDrawingCacheEnabled(true);
        // this.setChildrenDrawingOrderEnabled(true);
        // this.setAlwaysDrawnWithCacheEnabled(true);
        // this.setAnimationCacheEnabled(true);
        //		bbCacheTool = BBCacheTool.getInstance(_context);
    }

    /**
     * 移除 searchBoxJavaBridge_ 对象 注释：其中searchBoxJavaBridge_
     * 是WebView内部注入的对象，这是在3.0以后的Android系统上添加的。
     */
    private boolean removeSearchBoxImpl() {
        super.removeJavascriptInterface("accessibility");
        super.removeJavascriptInterface("accessibilityTraversal");
        if (!hasJellyBeanMR1()) {
            super.removeJavascriptInterface("searchBoxJavaBridge_");
            return true;
        }
        return false;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void addJavascriptInterface(Object obj, String interfaceName) {
        if (TextUtils.isEmpty(interfaceName)) {
            return;
        }
        // 如果在4.4以上，直接调用基类的方法来注册
        if (hasKitkat()) {
            super.addJavascriptInterface(obj, interfaceName);
        } else {
            mJsInterfaceMap.put(interfaceName, obj);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void removeJavascriptInterface(String interfaceName) {
        if (hasKitkat()) {
            super.removeJavascriptInterface(interfaceName);
        } else {
            mJsInterfaceMap.remove(interfaceName);
            mJsStringCache = null;
            injectJavascriptInterfaces();
        }
    }

    /**
     * 这个方法在Android 4.4系统引入，因此只能在Android4.4系统中才能使用，
     * 提供在当前页面显示上下文中异步执行javascript代码
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void evaluateJavascript(String script, ValueCallback<String> resultCallback) {
        super.evaluateJavascript(script, resultCallback);
    }

    /**
     * Android4.2
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean hasJellyBeanMR1() {
        return true;
    }

    /**
     * Android4.4
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean hasKitkat() {
        return true;
    }

    /**
     * 注入js调用接口
     */
    public void injectJavascriptInterfaces() {
        if (!TextUtils.isEmpty(mJsStringCache)) {
            loadJavascriptInterfaces();
            return;
        }

        mJsStringCache = genJavascriptInterfacesString();
        loadJavascriptInterfaces();
    }

    /**
     * 加载js
     */
    private void loadJavascriptInterfaces() {
        this.loadUrl(mJsStringCache);
    }

    /**
     * 生成脚本接口字符串
     */
    private String genJavascriptInterfacesString() {
        if (mJsInterfaceMap.size() == 0) {
            mJsStringCache = null;
            return null;
        }

        /*
         * 要注入的JS的格式，其中XXX为注入的对象的方法名，例如注入的对象中有一个方法A，那么这个XXX就是A
         * 如果这个对象中有多个方法，则会注册多个window.XXX_js_interface_name块，我们是用反射的方法遍历
         * 注入对象中的所有带有@JavaScripterInterface标注的方法
         *
         * javascript:(function JsAddJavascriptInterface_(){
         * if(typeof(window.XXX_js_interface_name)!='undefined'){
         * console.log('window.XXX_js_interface_name is exist!!'); }else{
         * window.XXX_js_interface_name={ XXX:function(arg0,arg1){ return
         * prompt(
         * 'MyApp:'+JSON.stringify({obj:'XXX_js_interface_name',func:'XXX_',args:[arg0,arg1]}));
         * }, }; } })()
         */

        Iterator<Map.Entry<String, Object>> iterator = mJsInterfaceMap.entrySet()
                .iterator();
        // Head
        StringBuilder script = new StringBuilder();
        script.append("javascript:(function JsAddJavascriptInterface_(){");

        // Add methods
        try {
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                String interfaceName = entry.getKey();
                Object obj = entry.getValue();

                createJsMethod(interfaceName, obj, script);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // End
        script.append("})()");

        return script.toString();
    }

    /**
     * 创建jS函数
     */
    private void createJsMethod(String interfaceName, Object obj,
                                StringBuilder script) {
        if (TextUtils.isEmpty(interfaceName) || (null == obj) || (null == script)) {
            return;
        }

        Class<?> objClass = obj.getClass();

        script.append("if(typeof(window.").append(interfaceName)
                .append(")!='undefined'){");
        if (DEBUG) {
            script.append("    console.log('window.").append(interfaceName)
                    .append("_js_interface_name is exist!!');");
        }

        script.append("}else {");
        script.append("    window.").append(interfaceName).append("={");

        // Add methods
        Method[] methods = objClass.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            // 过滤掉Object类的方法，包括getClass()方法，因为在Js中就是通过getClass()方法来得到Runtime实例
            if (filterMethods(methodName)) {
                continue;
            }

            script.append("        ").append(methodName).append(":function(");
            // 添加方法的参数
            int argCount = method.getParameterTypes().length;
            if (argCount > 0) {
                int maxCount = argCount - 1;
                for (int i = 0; i < maxCount; ++i) {
                    script.append(VAR_ARG_PREFIX).append(i).append(",");
                }
                script.append(VAR_ARG_PREFIX).append(argCount - 1);
            }

            script.append(") {");

            // Add implementation
            if (method.getReturnType() != void.class) {
                script.append("            return ").append("prompt('")
                        .append(MSG_PROMPT_HEADER).append("'+");
            } else {
                script.append("            prompt('").append(MSG_PROMPT_HEADER)
                        .append("'+");
            }

            // Begin JSON
            script.append("JSON.stringify({");
            script.append(KEY_INTERFACE_NAME).append(":'")
                    .append(interfaceName).append("',");
            script.append(KEY_FUNCTION_NAME).append(":'").append(methodName)
                    .append("',");
            script.append(KEY_ARG_ARRAY).append(":[");
            // 添加参数到JSON串中
            if (argCount > 0) {
                int max = argCount - 1;
                for (int i = 0; i < max; i++) {
                    script.append(VAR_ARG_PREFIX).append(i).append(",");
                }
                script.append(VAR_ARG_PREFIX).append(max);
            }

            // End JSON
            script.append("]})");
            // End prompt
            script.append(");");
            // End function
            script.append("        }, ");
        }

        // End of obj
        script.append("    };");
        // End of if or else
        script.append("}");
    }

    /**
     * 响应js prompt 函数请求
     */
    public boolean handleJsInterface(WebView view, String url, String message,
                                     String defaultValue, JsPromptResult result) {
        String prefix = MSG_PROMPT_HEADER;
        if (!message.startsWith(prefix)) {
            return false;
        }

        String jsonStr = message.substring(prefix.length());
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            String interfaceName = jsonObj.getString(KEY_INTERFACE_NAME);
            String methodName = jsonObj.getString(KEY_FUNCTION_NAME);
            JSONArray argsArray = jsonObj.getJSONArray(KEY_ARG_ARRAY);
            Object[] args = null;
            if (null != argsArray) {
                int count = argsArray.length();
                if (count > 0) {
                    args = new Object[count];

                    for (int i = 0; i < count; ++i) {
                        args[i] = argsArray.get(i);
                    }
                }
            }

            if (invokeJSInterfaceMethod(result, interfaceName, methodName, args)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.cancel();
        return false;
    }

    /**
     * 反射调用js接口函数
     */
    private boolean invokeJSInterfaceMethod(JsPromptResult result, String interfaceName,
                                            String methodName, Object[] args) {
        boolean succeed = false;
        final Object obj = mJsInterfaceMap.get(interfaceName);
        if (null == obj) {
            result.cancel();
            return false;
        }

        Class<?>[] parameterTypes = null;
        int count = 0;
        if (args != null) {
            count = args.length;
        }

        if (count > 0) {
            parameterTypes = new Class[count];
            for (int i = 0; i < count; ++i) {
                parameterTypes[i] = getClassFromJsonObject(args[i]);
            }
        }

        try {
            Method method = obj.getClass()
                    .getMethod(methodName, parameterTypes);
            Object returnObj = method.invoke(obj, args); // 执行接口调用
            boolean isVoid = returnObj == null
                    || returnObj.getClass() == void.class;
            String returnValue = isVoid ? "" : returnObj.toString();
            result.confirm(returnValue); // 通过prompt返回调用结果
            succeed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.cancel();
        return succeed;
    }

    /**
     * 解析参数类型
     */
    private Class<?> getClassFromJsonObject(Object obj) {
        Class<?> cls = obj.getClass();

        // js对象只支持int boolean string三种类型
        if (cls == Integer.class) {
            cls = Integer.TYPE;
        } else if (cls == Boolean.class) {
            cls = Boolean.TYPE;
        } else {
            cls = String.class;
        }
        return cls;
    }

    /**
     * 过滤Object的公有方法
     */
    private boolean filterMethods(String methodName) {
        for (String method : mFilterMethods) {
            if (method.equals(methodName)) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    public void callJavascript(String javascript, ValueCallback callback) {
        evaluateJavascript(javascript, callback);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener == null) {
            return;
        }
        float webContent = getContentHeight() * getScale();// webview的高度
        float webNow = getHeight() + getScrollY();// 当前webview的高度
        if (Math.abs(webContent - webNow) < 2000) {
            // 已经处于底端
            listener.onPageEnd(l, t, oldl, oldt);
        } else if (getScrollY() == 0) {
            listener.onPageTop(l, t, oldl, oldt);
        } else {
            listener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        this.listener = listener;
    }

    public interface OnScrollChangeListener {
        void onPageEnd(int l, int t, int oldl, int oldt);

        void onPageTop(int l, int t, int oldl, int oldt);

        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

}
