用了这么多年的Fragment，今天学习一下Fragment的源码。本篇主要分析Fragment不在ViewPager中使用的场景(但是感觉在Fragment中使用的场景应该也是大同小异)。


```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <FrameLayout
        android:id="@+id/frame_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_above="@+id/ll_bottom">

</RelativeLayout>

```
我们使用一个FrameLayout作为Fragment的容器。

向该容器中加入Fragment。

第一种方法，使用FragmentTransaction的`replace`方法。

```java
private void testReplace() {
    MainFragment fragment =new MainFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    //别忘了commit
    fragmentTransaction.replace(R.id.frame_layout,fragment).commit();
}
```

第二种方法，使用FragmentTransaction的`add`方法。

```java
private void testAdd() {
    MainFragment fragment = new MainFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    //别忘了commit
    fragmentTransaction.add(R.id.frame_layout, fragment).commit();
}
```

我们就从这里入手开始进行分析。

```java
FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
```

先看FragmentTransaction是何方神圣。

FragmentActivity的getSupportFragmentManager方法。

```java
/**
 * Return the FragmentManager for interacting with fragments associated
 * with this activity.
 */
public FragmentManager getSupportFragmentManager() {
    return mFragments.getSupportFragmentManager();
}
```

最终调用FragmentHostCallback的getFragmentManagerImpl方法。返回一个FragmentManagerImpl对象。

```java
public abstract class FragmentHostCallback<E> extends FragmentContainer {

    final FragmentManagerImpl mFragmentManager = new FragmentManagerImpl();
    
    //返回
    FragmentManagerImpl getFragmentManagerImpl() {
        return mFragmentManager;
    }
}

```


```java
final class FragmentManagerImpl extends FragmentManager implements LayoutInflater.Factory2 {
    //...

    @Override
    public FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }

}

```

返回的FragmentTransaction是一个BackStackRecord对象。

BackStackRecord类的部分成员变量和方法，注意BackStackRecord类实现了FragmentManagerImpl.OpGenerator接口

```java
final class BackStackRecord extends FragmentTransaction implements
        FragmentManager.BackStackEntry, FragmentManagerImpl.OpGenerator {
    static final String TAG = FragmentManagerImpl.TAG;

    final FragmentManagerImpl mManager;

    static final int OP_NULL = 0;
    static final int OP_ADD = 1;
    static final int OP_REPLACE = 2;
    static final int OP_REMOVE = 3;
    static final int OP_HIDE = 4;
    static final int OP_SHOW = 5;
    static final int OP_DETACH = 6;
    static final int OP_ATTACH = 7;
    static final int OP_SET_PRIMARY_NAV = 8;
    static final int OP_UNSET_PRIMARY_NAV = 9;

    static final class Op {
        int cmd;
        Fragment fragment;
        int enterAnim;
        int exitAnim;
        int popEnterAnim;
        int popExitAnim;

        Op() {
        }

        Op(int cmd, Fragment fragment) {
            this.cmd = cmd;
            this.fragment = fragment;
        }
    }

    ArrayList<Op> mOps = new ArrayList<>();

    //...
    public BackStackRecord(FragmentManagerImpl manager) {
        mManager = manager;
    }

}
```

接下来看FragmentTransaction的replace方法。

```java
fragmentTransaction.replace(R.id.frame_layout, fragment).commit();
```

BackStackRecord的replace方法。


```java
@Override
public FragmentTransaction replace(int containerViewId, Fragment fragment) {
    return replace(containerViewId, fragment, null);
}

@Override
public FragmentTransaction replace(int containerViewId, Fragment fragment, String tag) {
    if (containerViewId == 0) {
        throw new IllegalArgumentException("Must use non-zero containerViewId");
    }
    //注释1处
    doAddOp(containerViewId, fragment, tag, OP_REPLACE);
    return this;
}
```

注释1处，调用doAddOp方法，传入的opcmd是OP_REPLACE。opcmd感觉应该是operationCommand的意思(操作命令)

```java
private void doAddOp(int containerViewId, Fragment fragment, String tag, int opcmd) {
    final Class fragmentClass = fragment.getClass();
    final int modifiers = fragmentClass.getModifiers();
    if (fragmentClass.isAnonymousClass() || !Modifier.isPublic(modifiers)
            || (fragmentClass.isMemberClass() && !Modifier.isStatic(modifiers))) {
        throw new IllegalStateException("Fragment " + fragmentClass.getCanonicalName()
                + " must be a public static class to be  properly recreated from"
                + " instance state.");
    }
    //为fragment的mFragmentManager赋值
    fragment.mFragmentManager = mManager;
    //为fragment设置tag，且fragment的tag不能更改
    if (tag != null) {
        if (fragment.mTag != null && !tag.equals(fragment.mTag)) {
            throw new IllegalStateException("Can't change tag of fragment "
                    + fragment + ": was " + fragment.mTag
                    + " now " + tag);
        }
        fragment.mTag = tag;
    }
    //放置Fragment的容器id不能为null，且不能更改一个fragment的容器id
    if (containerViewId != 0) {
        if (containerViewId == View.NO_ID) {
            throw new IllegalArgumentException("Can't add fragment "
                    + fragment + " with tag " + tag + " to container view with no id");
        }
        if (fragment.mFragmentId != 0 && fragment.mFragmentId != containerViewId) {
            throw new IllegalStateException("Can't change container ID of fragment "
                    + fragment + ": was " + fragment.mFragmentId
                    + " now " + containerViewId);
        }
        fragment.mContainerId = fragment.mFragmentId = containerViewId;
    }
    //注释1处
    addOp(new Op(opcmd, fragment));
}
```

doAddOp方法做了一些检查操作，然后构建了一个`Op`对象，然后调用addOp方法。

```java
Op(int cmd, Fragment fragment) {
    this.cmd = cmd;
    this.fragment = fragment;
}
```

```java
void addOp(Op op) {
    mOps.add(op);
    op.enterAnim = mEnterAnim;
    op.exitAnim = mExitAnim;
    op.popEnterAnim = mPopEnterAnim;
    op.popExitAnim = mPopExitAnim;
}
```

然后就完了。那么重点肯定在commit方法中了。

```java
@Override
public int commit() {
    return commitInternal(false);
}
```

BackStackRecord的commitInternal方法。

```java
int commitInternal(boolean allowStateLoss) {
    if (mCommitted) throw new IllegalStateException("commit already called");
    if (FragmentManagerImpl.DEBUG) {
        Log.v(TAG, "Commit: " + this);
        LogWriter logw = new LogWriter(TAG);
        PrintWriter pw = new PrintWriter(logw);
        dump("  ", null, pw, null);
        pw.close();
    }
    mCommitted = true;
    //注释1处，默认mAddToBackStack为false
    if (mAddToBackStack) {
        mIndex = mManager.allocBackStackIndex(this);
    } else {
        mIndex = -1;
    }
    //注释2处，调用FragmentManagerImpl的enqueueAction方法
    mManager.enqueueAction(this, allowStateLoss);
    return mIndex;
}
```

注释2处，调用FragmentManagerImpl的enqueueAction方法，传入的第一个参数是OpGenerator类型，BackStackRecord类实现了FragmentManagerImpl.OpGenerator接口。


```java
/**
 * Adds an action to the queue of pending actions.
 *
 * @param action the action to add
 * @param allowStateLoss whether to allow loss of state information
 * @throws IllegalStateException if the activity has been destroyed
 */
public void enqueueAction(OpGenerator action, boolean allowStateLoss) {
    //如果不允许状态丢失，则做检查决定是否要抛出异常
    if (!allowStateLoss) {
        checkStateLoss();
    }
    synchronized (this) {
        //不合法的状态，如果允许状态丢失，直接返回。否则抛出异常 Activity has been destroyed
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
        //将action加入列表 
        mPendingActions.add(action);
        //调度提交
        scheduleCommit();
    }
}
```

```java
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
            mHost.getHandler().removeCallbacks(mExecCommit);
            //注释1处，post一个Runnable到主线程执行
            mHost.getHandler().post(mExecCommit);
        }
    }
}
```

```java
Runnable mExecCommit = new Runnable() {
    @Override
    public void run() {
        execPendingActions();
    }
};
```

FragmentManagerImpl的execPendingActions方法，只能在主线程执行。

```java
/**
 * Only call from main thread!
 */
public boolean execPendingActions() {
    //注释1处，这里就先假定不存在延迟的action，
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

```java
/**
 * Broken out from exec*, this prepares for gathering and executing operations.
 *
 * @param allowStateLoss true if state loss should be ignored or false if it should be
 *                       checked.
 */
private void ensureExecReady(boolean allowStateLoss) {
    if (mExecutingActions) {
        throw new IllegalStateException("FragmentManager is already executing transactions");
    }
    //只能在主线程执行
    if (Looper.myLooper() != mHost.getHandler().getLooper()) {
        throw new IllegalStateException("Must be called from main thread of fragment host");
    }
    //这里allowStateLoss为true，不用检查
    if (!allowStateLoss) {
        checkStateLoss();
    }

    if (mTmpRecords == null) {
        mTmpRecords = new ArrayList<>();
        mTmpIsPop = new ArrayList<>();
    }
    mExecutingActions = true;
    try {
        //注释1处
        executePostponedTransaction(null, null);
    } finally {
        mExecutingActions = false;
    }
}
```

ensureExecReady方法的注释1处，调用executePostponedTransaction方法。传递的参数都为null。

```java
/**
 * 完成先前延迟但是现在已经准备好了的事务的执行。
 */
private void executePostponedTransaction(ArrayList<BackStackRecord> records,
        ArrayList<Boolean> isRecordPop) {
    int numPostponed = mPostponedTransactions == null ? 0 : mPostponedTransactions.size();
    //numPostponed大于0，存在延迟的事务
    for (int i = 0; i < numPostponed; i++) {
        StartEnterTransitionListener listener = mPostponedTransactions.get(i);
        //传入的records位null的话，这if代码块个不会执行
        if (records != null && !listener.mIsBack) {
            int index = records.indexOf(listener.mRecord);
            if (index != -1 && isRecordPop.get(index)) {
                listener.cancelTransaction();
                continue;
            }
        }
        if (listener.isReady() || (records != null
                && listener.mRecord.interactsWith(records, 0, records.size()))) {
            //如果条件满足，从延迟的action中移除
            mPostponedTransactions.remove(i);
            i--;
            numPostponed--;
            int index;
            if (records != null && !listener.mIsBack
                    && (index = records.indexOf(listener.mRecord)) != -1
                    && isRecordPop.get(index)) {
                // This is popping a postponed transaction
                listener.cancelTransaction();
            } else {
                //注释2处，传入的records为null，执行这里
                listener.completeTransaction();
            }
        }
    }
}
```
executePostponedTransaction方法的注释2处，调用StartEnterTransitionListener的completeTransaction方法。


```java
/**
 * Completes the transaction and start the animations and transitions. This may skip
 * the transitions if this is called before all fragments have called
 * {@link Fragment#startPostponedEnterTransition()}.
 */
public void completeTransaction() {
    final boolean canceled;
    canceled = mNumPostponed > 0;
    FragmentManagerImpl manager = mRecord.mManager;
    final int numAdded = manager.mAdded.size();
    for (int i = 0; i < numAdded; i++) {
        final Fragment fragment = manager.mAdded.get(i);
        fragment.setOnStartEnterTransitionListener(null);
        if (canceled && fragment.isPostponed()) {
            fragment.startPostponedEnterTransition();
        }
    }
    mRecord.mManager.completeExecute(mRecord, mIsBack, !canceled, true);
}
```































