# simpleuse 包下面是关于Fragment的基础知识
# lazyload 包下是关于懒加载的例子
Fragment完整生命周期图请参考根目录下的fragment_lifecycle.png
相关博客
#### Fragment 在ViewPager中的生命周期
1.左右滑动 onResume()方法是不会调用的。
2.点击home键回到后台 fragment 会回调 onPause(),onStop()。重新回到前台，fragment会调用onStart(),onResume()。

3. Fragment生命周期只要走到onDestroyView。当再次显示当时候一定会重新调用onCreateView方法。如果Fragment不再继续向下走，
那么但是Fragment的视图会被保存。

4. Fragment生命周期只要走到onDestroyView。当再次显示当时候一定会重新调用onCreateView方法。
如果Fragment继续onDestroy,onDetach向下走，那么但是Fragment的视图会被销毁。

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


### FragmentTransaction commit 和commitAllowingStateLoss的区别
```
/**
 * 安排提交此事物。这个提交不会立即发生；
 * 它会作为主线程的一个工作在线程下一次准备好的时候会完成这个工作。
 *
 * 注意 一个事物只有在activity保存状态之前提交。如果尝试在activity保存状态之后提交会抛出一个异常。
 * 这是因为activity需要从保存的状态恢复的时候，在activity保存状态之后提交之后的状态会丢失。
 * 
 * @return Returns the identifier of this transaction's back stack entry,
 * if {@link #addToBackStack(String)} had been called.  Otherwise, returns
 * a negative number.
 */
 public abstract int commit();
```

```
/**
 * 和{@link #commit}类似，但是允许在activity保存状态之后提交。这很危险，
 * 因为如果activity需要稍后从保存的状态中恢复，这个提交肯能会丢失，所以这个方法只应该在UI状态意外的改变对用户来说是okay的
 * 时候才能被使用。
 */
public abstract int commitAllowingStateLoss();
```

FragmentTransaction的子类
```
final class BackStackRecord extends FragmentTransaction implements
        FragmentManager.BackStackEntry, FragmentManagerImpl.OpGenerator {
}
```

```
@Override
public int commit() {
    return commitInternal(false);
}

@Override
public int commitAllowingStateLoss() {
    return commitInternal(true);
}
```

```
int commitInternal(boolean allowStateLoss) {
    if (mCommitted) throw new IllegalStateException("commit already called");
        //...
        mCommitted = true;
        if (mAddToBackStack) {
            mIndex = mManager.allocBackStackIndex(this);
        } else {
            mIndex = -1;
        }
    //mManager是一个FragmentManagerImpl对象
    mManager.enqueueAction(this, allowStateLoss);
    return mIndex;
}
```

```
/**
 * 将action 添加到待处理的队列中。to the queue of pending actions.
 *
     * @param action the action to add
     * @param allowStateLoss whether to allow loss of state information
     * @throws IllegalStateException if the activity has been destroyed
     */
    public void enqueueAction(OpGenerator action, boolean allowStateLoss) {
        if (!allowStateLoss) {
        	  //如果检查状态
            checkStateLoss();
        }
        synchronized (this) {
            if (mDestroyed || mHost == null) {
                if (allowStateLoss) {
                    // This FragmentManager isn't attached, so drop the entire transaction.
                    return;
                }
                throw new IllegalStateException("Activity has been destroyed");
            }
            if (mPendingActions == null) {
                mPendingActions = new ArrayList<>();
            }
            //添加action
            mPendingActions.add(action);
            //开始调度
            scheduleCommit();
        }
    }
```

```
private void checkStateLoss() {
        if (mStateSaved) {//如果状态已保存则抛出异常
            throw new IllegalStateException(
                    "Can not perform this action after onSaveInstanceState");
        }
        if (mNoTransactionsBecause != null) {
            throw new IllegalStateException(
                    "Can not perform this action inside of " + mNoTransactionsBecause);
        }
    }
```

```
/**
 * Schedules the execution when one hasn't been scheduled already. This should happen
 * the first time {@link #enqueueAction(OpGenerator, boolean)} is called or when
 * a postponed transaction has been started with
 * {@link Fragment#startPostponedEnterTransition()}
 */
    private void scheduleCommit() {
        synchronized (this) {
            boolean postponeReady =
                    mPostponedTransactions != null && !mPostponedTransactions.isEmpty();
            boolean pendingReady = mPendingActions != null && mPendingActions.size() == 1;
            if (postponeReady || pendingReady) {
                //发送消息
                mHost.getHandler().removeCallbacks(mExecCommit);
                mHost.getHandler().post(mExecCommit);
            }
        }
    }
```

```
Runnable mExecCommit = new Runnable() {
    @Override
    public void run() {
        execPendingActions();
    }
};
```

```
/**
     * Only call from main thread!
     */
    public boolean execPendingActions() {
    		//确保准备好执行了
        ensureExecReady(true);

        boolean didSomething = false;
        while (generateOpsForPendingActions(mTmpRecords, mTmpIsPop)) {
            mExecutingActions = true;
            try {
                removeRedundantOperationsAndExecute(mTmpRecords, mTmpIsPop);
            } finally {
                cleanupExec();
            }
            didSomething = true;
        }

        doPendingDeferredStart();
        burpActive();

        return didSomething;
    }
```

```
/**
     * 从execPendingActions中分离出来，为收集和执行操作做好准备
     *
     * @param allowStateLoss true if state loss should be ignored or false if it should be
     *                       checked.
     */
    private void ensureExecReady(boolean allowStateLoss) {
        if (mExecutingActions) {
            throw new IllegalStateException("FragmentManager is already executing transactions");
        }

        if (Looper.myLooper() != mHost.getHandler().getLooper()) {
            throw new IllegalStateException("Must be called from main thread of fragment host");
        }

        if (!allowStateLoss) {
            checkStateLoss();
        }

        if (mTmpRecords == null) {
            mTmpRecords = new ArrayList<>();
            mTmpIsPop = new ArrayList<>();
        }
        mExecutingActions = true;
        try {
        	  //先准备
            executePostponedTransaction(null, null);
        } finally {
            mExecutingActions = false;
        }
    }
```

