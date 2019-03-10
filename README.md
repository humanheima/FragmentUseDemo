# simpleuse 包下面是关于Fragment的基础知识
# lazyload 包下是关于懒加载的例子
Fragment完整生命周期图请参考根目录下的fragment_lifecycle.png
相关博客
#### Fragment 在ViewPager中的生命周期
1.左右滑动 onResume()方法是不会调用的。
2.点击home键回到后台 fragment 会回调 onPause(),onStop()。重新回到前台，fragment会调用onStart(),onResume()。

####  使用FragmentTransaction的时候
```java
public abstract FragmentTransaction show(Fragment fragment);

public abstract FragmentTransaction hide(Fragment fragment);

```
使用show(),hide()这种方式的时候 onCreateView(),onResume()方法不会被重复调用

```java
public abstract FragmentTransaction replace(@IdRes int containerViewId, Fragment fragment);

```
使用replace()方法的时候fragment都会被onDetach()，所以重新onAttach的时候会走onCreateView(),onResume()方法


```java
public abstract FragmentTransaction replace(@IdRes int containerViewId, Fragment fragment);

fragmentTransaction.addToBackStack(null);
fragmentTransaction.commit();
```
replace()方法配合addToBackStack()使用，那么前一个被replace的Fragment的声明周期只会走到onDestroyView()；
onDestroy(),onDetach()方法不会调用。

http://blog.csdn.net/leilifengxingmw/article/details/79069504

## Fragment的startActivity
```java
public void startActivity(Intent intent) {
        startActivity(intent, null);
}
```
```java
public void startActivity(Intent intent, @Nullable Bundle options) {
        if (mHost == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to Activity");
        }
        mHost.onStartActivityFromFragment(this /*fragment*/, intent, -1, options);
    }
```
FragmentActivity.HostCallbacks的onStartActivityFromFragment方法
```java
@Override
        public void onStartActivityFromFragment(
                Fragment fragment, Intent intent, int requestCode, @Nullable Bundle options) {
            FragmentActivity.this.startActivityFromFragment(fragment, intent, requestCode, options);
        }
```
FragmentActivity的startActivityFromFragment方法
```java
public void startActivityFromFragment(Fragment fragment, Intent intent,
            int requestCode, @Nullable Bundle options) {
        mStartedActivityFromFragment = true;
        try {
            if (requestCode == -1) {
                //不要求返回结果
                ActivityCompat.startActivityForResult(this, intent, -1, options);
                return;
            }
            checkForValidRequestCode(requestCode);
            int requestIndex = allocateRequestIndex(fragment);
            ActivityCompat.startActivityForResult(
                    this, intent, ((requestIndex + 1) << 16) + (requestCode & 0xffff), options);
        } finally {
            mStartedActivityFromFragment = false;
        }
    }
```
ActivityCompat的startActivityForResult方法
```java
public static void startActivityForResult(@NonNull Activity activity, @NonNull Intent intent,
            int requestCode, @Nullable Bundle options) {
        if (Build.VERSION.SDK_INT >= 16) {
            activity.startActivityForResult(intent, requestCode, options);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

```
Activity的startActivityForResult方法精简版
```java
 public void startActivityForResult(@RequiresPermission Intent intent, int requestCode,
            @Nullable Bundle options) {
        if (mParent == null) {
            //调用Instrumentation的execStartActivity方法
            Instrumentation.ActivityResult ar =
                mInstrumentation.execStartActivity(
                    this, mMainThread.getApplicationThread(), mToken, this,
                    intent, requestCode, options);
            
        }
            
    }
```
在Activity中调用startActivity方法，最终是调用ContextImpl的startActivity方法

ContextImpl的startActivity方法精简版

```java
@Override
    public void startActivity(Intent intent, Bundle options) {
        warnIfCallingFromSystemProcess();
        //最后也是调用Instrumentation的execStartActivity方法
        mMainThread.getInstrumentation().execStartActivity(
                getOuterContext(), mMainThread.getApplicationThread(), null,
                (Activity) null, intent, -1, options);
    }
```
