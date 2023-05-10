package com.example.one_xposed;

import android.app.Application;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
public class One_HookClass_jkd implements IXposedHookLoadPackage {

    private String TAG = "fatjslog";
    private String currentPackageName = "com.android.jkdexam";

    /**************************************************************************************************/

    private void main(ClassLoader finalClassLoader) {
        XposedHelpers.findAndHookMethod("com.android.module.util.FileDownloader", finalClassLoader, "downloadFileInThread", String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.i(TAG, "beforeHookedMethod: param_0" + param.args[0]);
                Log.i(TAG, "beforeHookedMethod: param_1" + param.args[1]);
            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }

    /**************************************************************************************************/

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

    public void GetClassLoaderClasslist(ClassLoader classLoader) {
        //private final DexPathList pathList;
        //public static java.lang.Object getObjectField(java.lang.Object obj, java.lang.String fieldName)
        Log.i(TAG, "start dealwith classloader:" + classLoader);
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
            for (String classname : classlist) {
                Log.i(TAG, dexFileObj + "---" + classname);
            }
        }
        Log.i(TAG, "end dealwith classloader:" + classLoader);
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        Log.i(TAG, loadPackageParam.packageName);
        Log.i(TAG, "HookJava->app packagename" + loadPackageParam.packageName);
        if (loadPackageParam.packageName.equals(currentPackageName)) {
            Log.i(TAG, "kanxue " + loadPackageParam.packageName);
            /* public static de.robv.android.xposed.XC_MethodHook.Unhook findAndHookMethod(java.lang.Class<?> clazz, java.lang.String methodName, java.lang.Object... parameterTypesAndCallback) { *//* compiled code *//* }

            public static de.robv.android.xposed.XC_MethodHook.Unhook findAndHookMethod(java.lang.String className, java.lang.ClassLoader classLoader, java.lang.String methodName, java.lang.Object... parameterTypesAndCallback) { *//* compiled code *//* }
             */
            ClassLoader classLoader = loadPackageParam.classLoader;
            Log.i(TAG, "loadPackageParam.classLoader->" + classLoader);
            GetClassLoaderClasslist(classLoader);

            ClassLoader parent = classLoader.getParent();
            while (parent != null) {
                Log.i(TAG, "parent->" + parent);
                if (parent.toString().contains("BootClassLoader")) {

                } else {
                    GetClassLoaderClasslist(parent);
                }
                parent = parent.getParent();
            }

            Class StubAppClass=XposedHelpers.findClass("com.stub.StubApp",loadPackageParam.classLoader);
            Method[] methods=StubAppClass.getDeclaredMethods();
            for(Method i:methods){
                Log.i(TAG, "com.stub.StubApp->"+i);
            }
            XposedHelpers.findAndHookMethod("com.stub.StubApp", loadPackageParam.classLoader, "onCreate", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Log.i(TAG, "com.stub.StubApp->onCreate beforeHookedMethod");
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Log.i(TAG, "com.stub.StubApp->onCreate afterHookedMethod");

                    ClassLoader finalClassLoader=getClassloader();
                    Log.i(TAG, "finalClassLoader->" + finalClassLoader);
                    //GetClassLoaderClasslist(finalClassLoader);//展示classLoader List

                    main(finalClassLoader);
                }
            });
        }
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


