package com.example.kuaishouhook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    private static final String KUAISHOU_PACKAGE = "com.smile.gifmaker";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        String pkg = lpparam.packageName;

        if (!pkg.equals(KUAISHOU_PACKAGE)) return;

        XposedBridge.log("[KuaishouHook] 模块已加载");

        // 解除下载限制
        try {
            XposedHelpers.findAndHookMethod(
                "com.smile.gifmaker.util.CommonUtils",
                lpparam.classLoader,
                "isCanDownload",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        param.setResult(true);
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("[KuaishouHook] Hook isCanDownload 失败: " + e);
        }

        // 视频无水印
        try {
            XposedHelpers.findAndHookMethod(
                "com.smile.gifmaker.model.FeedInfo",
                lpparam.classLoader,
                "getPlayUrl",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Object url = param.getResult();
                        if (url != null) {
                            String newUrl = url.toString()
                                .replace("watermark=1", "watermark=0")
                                .replace("&watermark=1", "")
                                .replace("watermark=1&", "");
                            param.setResult(newUrl);
                        }
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("[KuaishouHook] Hook getPlayUrl 失败: " + e);
        }

        // 图片无水印
        try {
            XposedHelpers.findAndHookMethod(
                "com.smile.gifmaker.model.PhotoInfo",
                lpparam.classLoader,
                "getUrl",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Object url = param.getResult();
                        if (url != null) {
                            String newUrl = url.toString()
                                .replace("watermark=1", "watermark=0")
                                .replace("&watermark=1", "")
                                .replace("watermark=1&", "");
                            param.setResult(newUrl);
                        }
                    }
                }
            );
        } catch (Throwable e) {
            XposedBridge.log("[KuaishouHook] Hook getUrl 失败: " + e);
        }
    }
}
