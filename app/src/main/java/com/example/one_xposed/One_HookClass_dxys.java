package com.example.one_xposed;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author glsite.com
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class One_HookClass_dxys implements IXposedHookLoadPackage {

    private String TAG = "fatjslog";
    private String currentPackageName = "cn.dxy.android.aspirin";
    private String wrapperProxyApplication = "com.wrapper.proxyapplication.WrapperProxyApplication";

    private static File file = new File(Environment.getExternalStorageDirectory() + "/QuestionFlowBeanPublic.txt");
    //Logger.i("com.stub.StubApp->"+i);

    /**************************************************************************************************/
    private void main(final ClassLoader classLoader) {
        Logger.addLogAdapter(new AndroidLogAdapter());

        //CommonItemArray的getItems
        XposedHelpers.findAndHookMethod("cn.dxy.aspirin.bean.common.CommonItemArray", classLoader, "getItems", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                //Logger.i("afterHookedMethod: cn.dxy.aspirin.bean.common.CommonItemArray.getItems() res \n" + param.getResult() + "\n" + json);
                //cn.dxy.aspirin.bean.question.QuestionFlowBeanPublic
                if (param.getResult().toString().contains("QuestionFlowBeanPublic")) {
                    parseDialogFormJson(param.getResult());
                }
            }
        });
    }

    private void parseDialogFormJson(Object result) {
        List list = (List) JSONArray.toJSON(result);
        for (Object o : list) {
            String str = String.valueOf(o);
            String jsonStr = String.valueOf(JSONObject.toJSON(str));
            if (jsonStr.contains("dialog_patient")) {//病人
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                JSONObject dialog_patient = (JSONObject) jsonObject.get("dialog_patient");
                String content = dialog_patient.getString("content");
                printContent("dialog_patient", content, jsonStr);
            }
            if (jsonStr.contains("dialog_doctor")) {//医生
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                JSONObject dialog_patient = (JSONObject) jsonObject.get("dialog_doctor");
                String content = dialog_patient.getString("content");
                printContent("dialog_doctor", content, jsonStr);
            }
        }
    }

    private void printContent(String user, String content, String jsonStr) {
        if (content.equals("")) {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
//            jsonObject.get()
        }else
            Log.i(TAG, content);
    }

    /**************************************************************************************************/

    public void printStack_0() {
        Logger.i("---------------start----------------");
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {

                Logger.i(TAG+i+": ", stackElements[i].getClassName()
                        +"----"+stackElements[i].getFileName()
                        +"----" + stackElements[i].getLineNumber()
                        +"----" +stackElements[i].getMethodName());
            }
        }
        Logger.i("---------------over----------------");
    }

    public String printStack_1() {
        RuntimeException e = new RuntimeException("<Start dump Stack !>");
        e.fillInStackTrace();
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder stackLog = new StringBuilder();
        for (StackTraceElement traceElement : stackTrace) {
            stackLog.append("\t\t").append(traceElement).append("\n");
        }
        return "\n++++++++++++++++\n" + stackLog;
    }

    public void GetClassLoaderClasslist(ClassLoader classLoader) {
        //private final DexPathList pathList;
        //public static java.lang.Object getObjectField(java.lang.Object obj, java.lang.String fieldName)
        Logger.i("start dealwith classloader:" + classLoader);
        Object pathListObj = XposedHelpers.getObjectField(classLoader, "pathList");
        //private final Element[] dexElements;
        Object[] dexElementsObj = (Object[]) XposedHelpers.getObjectField(pathListObj, "dexElements");
        for (Object i : dexElementsObj) {
            //private final DexFile dexFile;
            Object dexFileObj = XposedHelpers.getObjectField(i, "dexFile");
            //private Object mCookie;
            Object mCookieObj = XposedHelpers.getObjectField(dexFileObj, "mCookie");
            //private static native String[] getClassNameList(Object cookie);
            //    public static java.lang.Object callStaticMethod(java.lang.Class<?> clazz, java.lang.String methodName, java.lang.Object... args) { /* compiled code */ }
            Class DexFileClass = XposedHelpers.findClass("dalvik.system.DexFile", classLoader);

            String[] classlist = (String[]) XposedHelpers.callStaticMethod(DexFileClass, "getClassNameList", mCookieObj);
//            for (String classname : classlist) {
//                Logger.i(dexFileObj + "---" + classname);
//            }
        }
        //Logger.i("end dealwith classloader:" + classLoader);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        Logger.i(loadPackageParam.packageName);
        Logger.i("HookJava->app packagename" + loadPackageParam.packageName);
        if (loadPackageParam.packageName.equals(currentPackageName)) {
            Logger.i("kanxue " + loadPackageParam.packageName);
            /* public static de.robv.android.xposed.XC_MethodHook.Unhook findAndHookMethod(java.lang.Class<?> clazz, java.lang.String methodName, java.lang.Object... parameterTypesAndCallback) { *//* compiled code *//* }

            public static de.robv.android.xposed.XC_MethodHook.Unhook findAndHookMethod(java.lang.String className, java.lang.ClassLoader classLoader, java.lang.String methodName, java.lang.Object... parameterTypesAndCallback) { *//* compiled code *//* }
             */
            ClassLoader classLoader = loadPackageParam.classLoader;
            Logger.i("loadPackageParam.classLoader->" + classLoader);
            GetClassLoaderClasslist(classLoader);

            ClassLoader parent = classLoader.getParent();
            while (parent != null) {
                //Logger.i("parent->" + parent);
                if (parent.toString().contains("BootClassLoader")) {

                } else {
                    GetClassLoaderClasslist(parent);
                }
                parent = parent.getParent();
            }

            Class StubAppClass=XposedHelpers.findClass(wrapperProxyApplication,loadPackageParam.classLoader);
            Method[] methods=StubAppClass.getDeclaredMethods();
//            for(Method i:methods){
//                Logger.i("com.stub.StubApp->"+i);
//            }
            XposedHelpers.findAndHookMethod(wrapperProxyApplication, loadPackageParam.classLoader, "onCreate", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    ClassLoader finalClassLoader=getClassloader();
                    //Logger.i("finalClassLoader->" + finalClassLoader);
                    //GetClassLoaderClasslist(finalClassLoader);//展示classLoader List
                    main(finalClassLoader);
                }
            });
        }
    }

    public static Object invokeStaticMethod(String class_name,
                                            String method_name, Class[] pareTyple, Object[] pareVaules) {
        try {
            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getMethod(method_name, pareTyple);
            return method.invoke(null, pareVaules);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getFieldOjbect(String class_name, Object obj,
                                        String filedName) {
        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ClassLoader getClassloader() {
        ClassLoader resultClassloader = null;
        Object currentActivityThread = invokeStaticMethod(
                "android.app.ActivityThread", "currentActivityThread",
                new Class[]{}, new Object[]{});
        Object mBoundApplication = getFieldOjbect(
                "android.app.ActivityThread", currentActivityThread,
                "mBoundApplication");
        Application mInitialApplication = (Application) getFieldOjbect("android.app.ActivityThread",
                currentActivityThread, "mInitialApplication");
        Object loadedApkInfo = getFieldOjbect(
                "android.app.ActivityThread$AppBindData",
                mBoundApplication, "info");
        Application mApplication = (Application) getFieldOjbect("android.app.LoadedApk", loadedApkInfo, "mApplication");
        resultClassloader = mApplication.getClassLoader();
        return resultClassloader;
    }

    /* ********************************************************************************************** */

    public static Field getClassField(ClassLoader classloader, String class_name,
                                      String filedName) {
        try {
            Class obj_class = classloader.loadClass(class_name);//Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getClassFieldObject(ClassLoader classloader, String class_name, Object obj,
                                             String filedName) {
        try {
            Class obj_class = classloader.loadClass(class_name);//Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            Object result = null;
            result = field.get(obj);
            return result;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}


