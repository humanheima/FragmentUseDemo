用了这么多年的Fragment，今天学习一下Fragment的源码。本篇主要分析通过replace或者add这种向一个容器中添加Fragment的方式。


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
我们使用一个`FrameLayout`作为Fragment的容器。

我们在Activity的`onCreate()`方法中向该容器中加入Fragment。我们的Activity继承了FragmentActivity。

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_fragment);
    //使用replace的方式
    testReplace();
    //或者时候用add的方式
    testAdd();
    //...
}
```

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

Fragment类

```java
public class Fragment implements ComponentCallbacks, OnCreateContextMenuListener, LifecycleOwner {
    private static final SimpleArrayMap<String, Class<?>> sClassMap =
            new SimpleArrayMap<String, Class<?>>();

    static final Object USE_DEFAULT_TRANSITION = new Object();

    static final int INITIALIZING = 0;     // Not yet created.
    static final int CREATED = 1;          // Created.
    static final int ACTIVITY_CREATED = 2; // The activity has finished its creation.
    static final int STOPPED = 3;          // Fully created, not started.
    static final int STARTED = 4;          // Created and started, not resumed.
    static final int RESUMED = 5;          // Created started and resumed.

    int mState = INITIALIZING;

}
```

Fragment有一个`mState`属性，标志当前Fragment的生命周期状态

接下来开始分析，我们就从FragmentTransaction入手。

```java
FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
```

FragmentActivity的getSupportFragmentManager方法。

```java
public FragmentManager getSupportFragmentManager() {
    //mFragments是一个FragmentController对象
    return mFragments.getSupportFragmentManager();
}
```

最终调用FragmentHostCallback的`getFragmentManagerImpl`方法。返回一个`FragmentManagerImpl`对象。

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

FragmentManagerImpl的`beginTransaction`方法返回的FragmentTransaction是一个BackStackRecord对象。

BackStackRecord类的部分成员变量和方法，注意BackStackRecord类实现了`FragmentManagerImpl.OpGenerator`接口

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

BackStackRecord的`replace`方法。


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
    //...
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
        //为fragment的mContainerId和mFragmentId赋值。
        fragment.mContainerId = fragment.mFragmentId = containerViewId;
    }
    //注释1处
    addOp(new Op(opcmd, fragment));
}
```

doAddOp方法做了一些检查操作，然后构建了一个cmd是是OP_REPLACE的`Op`对象，然后调用addOp方法。

```java
Op(int cmd, Fragment fragment) {
    this.cmd = cmd;
    this.fragment = fragment;
}
```

BackStackRecord的addOp方法。

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

BackStackRecord的commit方法。

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
    //...
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

注释2处，调用`FragmentManagerImpl`的`enqueueAction`方法，传入的第一个参数是OpGenerator类型，BackStackRecord类实现了`FragmentManagerImpl.OpGenerator`接口。


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
FragmentManagerImpl的scheduleCommit方法。

```java
private void scheduleCommit() {
    synchronized (this) {
        boolean postponeReady =
                mPostponedTransactions != null && !mPostponedTransactions.isEmpty();
        boolean pendingReady = mPendingActions != null && mPendingActions.size() == 1;
        if (postponeReady || pendingReady) {
            //每次先移除Runnable
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

FragmentManagerImpl的 execPendingActions 方法，只能在主线程执行。

```java
public boolean execPendingActions() {
    //注释1处，此方法内部会执行先前延迟但是现在已经准备好了的事务。
    ensureExecReady(true);

    boolean didSomething = false;
    //注释2处，将所有的mPendingAction加入到mTmpRecords
    while (generateOpsForPendingActions(mTmpRecords, mTmpIsPop)) {
        mExecutingActions = true;
        try {
            //注释3处
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

**注意：**注释1处，此方法内部会执行先前延迟但是现在已经准备好了的事务。此方法的具体逻辑我们暂时不看。

注释2处，将所有的mPendingAction加入到mTmpRecords。

FragmentManagerImpl的 generateOpsForPendingActions 方法。

```java
private boolean generateOpsForPendingActions(ArrayList<BackStackRecord> records, ArrayList<Boolean> isPop) {
    boolean didSomething = false;
    synchronized (this) {
        if (mPendingActions == null || mPendingActions.size() == 0) {
            return false;
        }

        final int numActions = mPendingActions.size();
        for (int i = 0; i < numActions; i++) {
            //注释1处，循环调用BackStackRecord的generateOps方法
            didSomething |= mPendingActions.get(i).generateOps(records, isPop);
        }
        mPendingActions.clear();
        //移除mExecCommit
        mHost.getHandler().removeCallbacks(mExecCommit);
    }
    return didSomething;
}
```

BackStackRecord的`generateOps`方法

```java
@Override
public boolean generateOps(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop) {
    //...
    //BackStackRecord将自己加入到records，isRecordPop添加false
    records.add(this);
    isRecordPop.add(false);
    if (mAddToBackStack) {
        mManager.addBackStackState(this);
    }
    //总是返回true
    return true;
}
```

FragmentManagerImpl 的 execPendingActions 方法的注释3处。调用 removeRedundantOperationsAndExecute 方法。

```java
private void removeRedundantOperationsAndExecute(ArrayList<BackStackRecord> records,
        ArrayList<Boolean> isRecordPop) {
    //..
    // 注释1处，强制开始执行延迟的事务。暂且不看。
    executePostponedTransaction(records, isRecordPop);

    final int numRecords = records.size();
    int startIndex = 0;
    for (int recordNum = 0; recordNum < numRecords; recordNum++) {
        final boolean canReorder = records.get(recordNum).mReorderingAllowed;
        if (!canReorder) {
            // execute all previous transactions
            if (startIndex != recordNum) {
                executeOpsTogether(records, isRecordPop, startIndex, recordNum);
            }
            // 执行所有不允许重排序的 pop 操作，或者执行一个添加操作。
            int reorderingEnd = recordNum + 1;
            if (isRecordPop.get(recordNum)) {
                while (reorderingEnd < numRecords
                        && isRecordPop.get(reorderingEnd)
                        && !records.get(reorderingEnd).mReorderingAllowed) {
                    reorderingEnd++;
                }
            }
            //执行所有不允许重排序的 pop 操作，或者执行一个添加操作。
            executeOpsTogether(records, isRecordPop, recordNum, reorderingEnd);
            startIndex = reorderingEnd;
            recordNum = reorderingEnd - 1;
        }
    }
    if (startIndex != numRecords) {
        executeOpsTogether(records, isRecordPop, startIndex, numRecords);
    }
}

```

**注意：**注释1处，强制开始执行延迟的事务，暂且不看。

该方法内部最重要的逻辑是调用 FragmentManagerImpl 的 executeOpsTogether 方法。

```java
 /**
  * Executes a subset of a list of BackStackRecords, all of which either allow reordering or
  * do not allow ordering.
  * @param records A list of BackStackRecords that are to be executed
  * @param isRecordPop The direction that these records are being run.
  * @param startIndex The index of the first record in <code>records</code> to be executed
  * @param endIndex One more than the final record index in <code>records</code> to executed.
  */
 private void executeOpsTogether(ArrayList<BackStackRecord> records,
        ArrayList<Boolean> isRecordPop, int startIndex, int endIndex) {
     //BackStackRecord是否允许重排序
     final boolean allowReordering = records.get(startIndex).mReorderingAllowed;
     boolean addToBackStack = false;
     if (mTmpAddedFragments == null) {
         mTmpAddedFragments = new ArrayList<>();
     } else {
         mTmpAddedFragments.clear();
     }
     mTmpAddedFragments.addAll(mAdded);
     //这个oldPrimaryNav是干啥的？
     Fragment oldPrimaryNav = getPrimaryNavigationFragment();
     for (int recordNum = startIndex; recordNum < endIndex; recordNum++) {
         final BackStackRecord record = records.get(recordNum);
         final boolean isPop = isRecordPop.get(recordNum);
         if (!isPop) {//注释1处，添加操作
             oldPrimaryNav = record.expandOps(mTmpAddedFragments, oldPrimaryNav);
         } else {//注释2处，pop操作
             oldPrimaryNav = record.trackAddedFragmentsInPop(mTmpAddedFragments, oldPrimaryNav);
         }
         addToBackStack = addToBackStack || record.mAddToBackStack;
     }
     //清空mTmpAddedFragments
     mTmpAddedFragments.clear();

     //...
     //注释3处
     executeOps(records, isRecordPop, startIndex, endIndex);
     //...
}

```

FragmentManagerImpl 的 executeOpsTogether 方法的注释1处，调用 BackStackRecord 的 expandOps 方法。

```java
/**
 * 将所有元操作扩展为更原始的等效操作。
 *
 * <p>移除所有的OP_REPLACE操作将其替换为等价的添加和移除操作。
 *
 * @param added Initialized to the fragments that are in the mManager.mAdded, this
 *              will be modified to contain the fragments that will be in mAdded
 *              after the execution ({@link #executeOps()}.
 * @param oldPrimaryNav The tracked primary navigation fragment as of the beginning of
 *                      this set of ops
 * @return the new oldPrimaryNav fragment after this record's ops would be run
 */
@SuppressWarnings("ReferenceEquality")
Fragment expandOps(ArrayList<Fragment> added, Fragment oldPrimaryNav) {
    for (int opNum = 0; opNum < mOps.size(); opNum++) {
        final Op op = mOps.get(opNum);
        switch (op.cmd) {
            case OP_ADD:
            case OP_ATTACH:
                added.add(op.fragment);
                break;
            case OP_REMOVE:
            case OP_DETACH: {
                added.remove(op.fragment);
                if (op.fragment == oldPrimaryNav) {
                    mOps.add(opNum, new Op(OP_UNSET_PRIMARY_NAV, op.fragment));
                    opNum++;
                    oldPrimaryNav = null;
                }
            }
            break;
            //注释1处，替换OP_REPLACE操作
            case OP_REPLACE: {
                final Fragment f = op.fragment;
                final int containerId = f.mContainerId;
                boolean alreadyAdded = false;
                for (int i = added.size() - 1; i >= 0; i--) {
                    final Fragment old = added.get(i);
                    //老的fragment的mContainerId需要和replace的containerId相等
                    if (old.mContainerId == containerId) {
                        if (old == f) {
                            //注释2处，fragment已经添加了。
                            alreadyAdded = true;
                        } else {
                            // This is duplicated from above since we only make
                            // a single pass for expanding ops. Unset any outgoing primary nav.
                            if (old == oldPrimaryNav) {
                                mOps.add(opNum, new Op(OP_UNSET_PRIMARY_NAV, old));
                                opNum++;
                                oldPrimaryNav = null;
                            }
                            //注释3处，移除老fragment的操作
                            final Op removeOp = new Op(OP_REMOVE, old);
                            removeOp.enterAnim = op.enterAnim;
                            removeOp.popEnterAnim = op.popEnterAnim;
                            removeOp.exitAnim = op.exitAnim;
                            removeOp.popExitAnim = op.popExitAnim;
                            mOps.add(opNum, removeOp);
                            added.remove(old);
                            opNum++;
                        }
                    }
                }
                //如果要替换到containerId的fragment已经添加了，就移除掉该Op
                if (alreadyAdded) {
                    mOps.remove(opNum);
                    opNum--;
                } else {
                    //注释4处，添加一个OP_ADD操作，并将fragment添加到已添加的fragment列表 added 中
                    op.cmd = OP_ADD;
                    added.add(f);
                }
            }
            break;
            case OP_SET_PRIMARY_NAV: {
                // It's ok if this is null, that means we will restore to no active
                // primary navigation fragment on a pop.
                mOps.add(opNum, new Op(OP_UNSET_PRIMARY_NAV, oldPrimaryNav));
                opNum++;
                // Will be set by the OP_SET_PRIMARY_NAV we inserted before when run
                oldPrimaryNav = op.fragment;
            }
            break;
        }
    }
    return oldPrimaryNav;
}
```

注释1处，替换OP_REPLACE操作。
注释2处，fragment已经添加了。
注释3处，如果存在和新添加的fragment的mContainerId一样的老的fragment，添加一个移除老fragment的`Op`。
注释4处，添加一个OP_ADD操作，并将fragment添加到已添加的fragment列表`added`中。下一次循环的时候，`added`列表中就多了一个Fragment。

对于一个replace操作，如果存在老的fragment和要添加的fragment的mContainerId一样，那么就要添加一个移除操作将老的fragment移除掉然后再添加新的fragment。


FragmentManagerImpl的executeOpsTogether方法的注释2处，调用 BackStackRecord 的 trackAddedFragmentsInPop 方法。`mTmpAddedFragments`移除掉在一个pop操作中添加或移除的fragment。


FragmentManagerImpl 的 executeOpsTogether 方法的注释3处，调用 BackStackRecord 的 executeOps 方法。


BackStackRecord 的 executeOps 方法。for循环执行此事务中的所有操作。
```java
void executeOps() {
    final int numOps = mOps.size();
    //for循环执行所有的Op
    for (int opNum = 0; opNum < numOps; opNum++) {
        final Op op = mOps.get(opNum);
        final Fragment f = op.fragment;
        if (f != null) {
            f.setNextTransition(mTransition, mTransitionStyle);
        }
        switch (op.cmd) {
            case OP_ADD:
                f.setNextAnim(op.enterAnim);
                //注释1处，调用FragmentManagerImpl的addFragment方法。
                mManager.addFragment(f, false);
                break;
            case OP_REMOVE:
                f.setNextAnim(op.exitAnim);
                mManager.removeFragment(f);
                break;
            case OP_HIDE:
                f.setNextAnim(op.exitAnim);
                mManager.hideFragment(f);
                break;
            case OP_SHOW:
                f.setNextAnim(op.enterAnim);
                mManager.showFragment(f);
                break;
            case OP_DETACH:
                f.setNextAnim(op.exitAnim);
                mManager.detachFragment(f);
                break;
            case OP_ATTACH:
                f.setNextAnim(op.enterAnim);
                mManager.attachFragment(f);
                break;
            //...
            default:
                throw new IllegalArgumentException("Unknown cmd: " + op.cmd);
        }
        if (!mReorderingAllowed && op.cmd != OP_ADD && f != null) {
            //如果不逊于重排序并且不是添加操作，立即将fragment转换到合适的生命周期状态。
            mManager.moveFragmentToExpectedState(f);
        }
    }
    if (!mReorderingAllowed) {
        // Added fragments are added at the end to comply with prior behavior.
        //注释2处，将添加的fragment移动到合适的状态。
        mManager.moveToState(mManager.mCurState, true);
    }
}
```

在方法中，我看看到最终都是根据Op的cmd调用FragmentManagerImpl的对应的`xxxFragment`方法，像`addFragment`，`removeFragment`，`hideFragment`,`showFragment`等等。我们看看注释1处，调用FragmentManagerImpl的addFragment方法，传入的moveToStateNow参数为false。


```java
public void addFragment(Fragment fragment, boolean moveToStateNow) {
    //将fragment添加到FragmentManagerImpl的处于活动状态的fragment列表mActive中
    makeActive(fragment);
    if (!fragment.mDetached) {
        if (mAdded.contains(fragment)) {
            throw new IllegalStateException("Fragment already added: " + fragment);
        }
        synchronized (mAdded) {
            //注释1处，将fragment添加到mAdded列表
            mAdded.add(fragment);
        }
        fragment.mAdded = true;
        fragment.mRemoving = false;
        if (fragment.mView == null) {
            fragment.mHiddenChanged = false;
        }
        if (fragment.mHasMenu && fragment.mMenuVisible) {
            mNeedMenuInvalidate = true;
        }
        if (moveToStateNow) {//这里为false，不执行
            moveToState(fragment);
        }
    }
}
```
FragmentManagerImpl的addFragment方法注释1处，将fragment添加到mAdded列表。

回到BackStackRecord 的 executeOps 方法的注释2处，调用FragmentManagerImpl的 moveToState 方法。将添加的fragment移动到合适的状态。

```java
 void moveToState(int newState, boolean always) {
     if (mHost == null && newState != Fragment.INITIALIZING) {
         throw new IllegalStateException("No activity");
     }
     //如果状态相等，直接返回
     if (!always && newState == mCurState) {
         return;
     }

     mCurState = newState;

     if (mActive != null) {
         boolean loadersRunning = false;

         // Must add them in the proper order. mActive fragments may be out of order
         //存在已添加的fragment，
         final int numAdded = mAdded.size();
         for (int i = 0; i < numAdded; i++) {
             Fragment f = mAdded.get(i);
             //注释1处，从mAdded中取出fragment，移动到合适的状态
             moveFragmentToExpectedState(f);
             if (f.mLoaderManager != null) {
                 loadersRunning |= f.mLoaderManager.hasRunningLoaders();
             }
         }

        // Now iterate through all active fragments. These will include those that are removed
        // and detached.
         final int numActive = mActive.size();
         for (int i = 0; i < numActive; i++) {
             Fragment f = mActive.valueAt(i);
             //注释2处，如果fragment正在被activity移除，或者fragment不再是出于活动的状态，也要将fragment移动到合适的状态
             if (f != null && (f.mRemoving || f.mDetached) && !f.mIsNewlyAdded) {
                 moveFragmentToExpectedState(f);
                 if (f.mLoaderManager != null) {
                     loadersRunning |= f.mLoaderManager.hasRunningLoaders();
                 }
             }
         }
         //...
     }
}

```

FragmentManagerImpl的 moveFragmentToExpectedState 方法。
```java
 void moveFragmentToExpectedState(Fragment f) {
     if (f == null) {
         return;
     }
     //注释1处，为nextState赋值为mCurState
     int nextState = mCurState;
     if (f.mRemoving) {
         if (f.isInBackStack()) {
             nextState = Math.min(nextState, Fragment.CREATED);
         } else {
             nextState = Math.min(nextState, Fragment.INITIALIZING);
         }
     }
     //注释2处
     moveToState(f, nextState, f.getNextTransition(), f.getNextTransitionStyle(), false);

     if (f.mView != null) {
         // Move the view if it is out of order
         Fragment underFragment = findFragmentUnder(f);
            if (underFragment != null) {
            final View underView = underFragment.mView;
            // make sure this fragment is in the right order.
            final ViewGroup container = f.mContainer;
            int underIndex = container.indexOfChild(underView);
            int viewIndex = container.indexOfChild(f.mView);
            if (viewIndex < underIndex) {
                container.removeViewAt(viewIndex);
                container.addView(f.mView, underIndex);
            }
        }
        //...
     }
     //注释3处
     if (f.mHiddenChanged) {
         completeShowHideFragment(f);
     }
}

```

注释1处，为nextState赋值为mCurState，即Fragment要转换到的生命周期状态。说道生命周期状态，我们看一下`FragmentActivity`是如何调度Fragment的生命周期的。

```java
public class FragmentActivity extends BaseFragmentActivityApi16 implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        ActivityCompat.RequestPermissionsRequestCodeValidator {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //注释1处，这里先注意，后面会说道
        mFragments.attachHost(null /*parent*/);
        //...
        //注释2处，
        mFragments.dispatchCreate();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!mCreated) {
            mCreated = true;
            mFragments.dispatchActivityCreated();
        }
        
        mFragments.noteStateNotSaved();
        mFragments.execPendingActions();
        
        mFragments.doLoaderStart();
        
        // NOTE: HC onStart goes here.
        //注释3处
        mFragments.dispatchStart();
    }
 
     @Override
    protected void onPostResume() {
        super.onPostResume();
        mHandler.removeMessages(MSG_RESUME_PENDING);
        onResumeFragments();
        mFragments.execPendingActions();
    }

    protected void onResumeFragments() {
        //注释4处，调度Fragment
        mFragments.dispatchResume();
    }

    //...
}
```

FragmentActivity会在不同的生命周期的时候调用`FragmentController`的`dispatchXxx`方法来管理Fragment的生命周期状态。我们看看注释2处的`dispatchCreate`方法。

`FragmentController`的`dispatchCreate`方法。

```java
public void dispatchCreate() {
   mHost.mFragmentManager.dispatchCreate();
}
```

FragmentManagerIMpl的dispatchCreate方法。

```java
public void dispatchCreate() {
    mStateSaved = false;
    dispatchStateChange(Fragment.CREATED);
}
```

```java
private void dispatchStateChange(int nextState) {
   try {
       mExecutingActions = true;
       moveToState(nextState, false);
   } finally {
       mExecutingActions = false;
   }
   execPendingActions();
}
```

总之：Fragment的声明周期是受Activity调度的，Activity的生命周期发生了变化，Fragment的生命周期也要响应改变。


我们回到FragmentManagerImpl的 moveFragmentToExpectedState 方法的注释2处调用FragmentManagerImpl的 moveToState 方法。重点方法。这个方法内部就是fragment从当前状态移动到合适的生命周期状态。


```java
void moveToState(Fragment f, int newState, int transit, int transitionStyle, boolean keepActive) {
    //...
    if (f.mState <= newState) {//fragment的当前状态小于最终状态
        //...
        switch (f.mState) {
            case Fragment.INITIALIZING://注释1处，fragment当前还没有created
                if (newState > Fragment.INITIALIZING) {
                    if (DEBUG) Log.v(TAG, "moveto CREATED: " + f);
                    if (f.mSavedFragmentState != null) {
                        //如果fragment意外被销毁了，这里获取保存的Fragment的信息。不是主流程，逻辑忽略
                        //...
                    }

                    f.mHost = mHost;
                    f.mParentFragment = mParent;
                    f.mFragmentManager = mParent != null
                            ? mParent.mChildFragmentManager : mHost.getFragmentManagerImpl();

                    // If we have a target fragment, push it along to at least CREATED
                    // so that this one can rely on it as an initialized dependency.
                    if (f.mTarget != null) {
                        if (mActive.get(f.mTarget.mIndex) != f.mTarget) {
                            throw new IllegalStateException("Fragment " + f
                                    + " declared target fragment " + f.mTarget
                                    + " that does not belong to this FragmentManager!");
                        }
                        if (f.mTarget.mState < Fragment.CREATED) {
                            moveToState(f.mTarget, Fragment.CREATED, 0, 0, true);
                        }
                    }

                    dispatchOnFragmentPreAttached(f, mHost.getContext(), false);
                    f.mCalled = false;
                    //注释1处，调用fragment的onAttach方法。
                    f.onAttach(mHost.getContext());
                    if (!f.mCalled) {
                        throw new SuperNotCalledException("Fragment " + f
                                + " did not call through to super.onAttach()");
                    }
                    if (f.mParentFragment == null) {
                        mHost.onAttachFragment(f);
                    } else {
                        f.mParentFragment.onAttachFragment(f);
                    }
                    dispatchOnFragmentAttached(f, mHost.getContext(), false);

                    if (!f.mIsCreated) {//没有performCreate之前为false
                        dispatchOnFragmentPreCreated(f, f.mSavedFragmentState, false);
                        //注释2处，fragment内部调用onCreate方法。方法内部会将mIsCreated置为true
                        f.performCreate(f.mSavedFragmentState);
                        dispatchOnFragmentCreated(f, f.mSavedFragmentState, false);
                    } else {
                        f.restoreChildFragmentState(f.mSavedFragmentState);
                        f.mState = Fragment.CREATED;
                    }
                    f.mRetaining = false;
                }
            case Fragment.CREATED://fragment当前created完毕
                // This is outside the if statement below on purpose; we want this to run
                // even if we do a moveToState from CREATED => *, CREATED => CREATED, and
                // * => CREATED as part of the case fallthrough above.
                ensureInflatedFragmentView(f);

                if (newState > Fragment.CREATED) {
                    if (DEBUG) Log.v(TAG, "moveto ACTIVITY_CREATED: " + f);
                    //如果fragment不是写在布局文件xml里面的
                    if (!f.mFromLayout) {
                        ViewGroup container = null;
                        if (f.mContainerId != 0) {
                            if (f.mContainerId == View.NO_ID) {
                                throwException(new IllegalArgumentException(
                                        "Cannot create fragment "
                                                + f
                                                + " for a container view with no id"));
                            }
                            //注释3处，这里的mContainer是何时赋值的呢？
                            container = (ViewGroup) mContainer.onFindViewById(f.mContainerId);
                            if (container == null && !f.mRestored) {
                                String resName;
                                try {
                                    resName = f.getResources().getResourceName(f.mContainerId);
                                } catch (NotFoundException e) {
                                    resName = "unknown";
                                }
                                throwException(new IllegalArgumentException(
                                        "No view found for id 0x"
                                        + Integer.toHexString(f.mContainerId) + " ("
                                        + resName
                                        + ") for fragment " + f));
                            }
                        }
                        f.mContainer = container;
                        //注释4处，fragment内部调用onCreateView方法
                        f.mView = f.performCreateView(f.performGetLayoutInflater(
                                f.mSavedFragmentState), container, f.mSavedFragmentState);
                        if (f.mView != null) {
                            f.mInnerView = f.mView;
                            f.mView.setSaveFromParentEnabled(false);
                            if (container != null) {
                                container.addView(f.mView);
                            }
                            if (f.mHidden) {
                                f.mView.setVisibility(View.GONE);
                            }
                            //注释5处，fragment调用onViewCreated方法
                            f.onViewCreated(f.mView, f.mSavedFragmentState);
                            dispatchOnFragmentViewCreated(f, f.mView, f.mSavedFragmentState,
                                    false);
                            // Only animate the view if it is visible. This is done after
                            // dispatchOnFragmentViewCreated in case visibility is changed
                            f.mIsNewlyAdded = (f.mView.getVisibility() == View.VISIBLE)
                                    && f.mContainer != null;
                        } else {
                            f.mInnerView = null;
                        }
                    }
                    //注释6处，fragment内部调用 onActivityCreated 方法
                    f.performActivityCreated(f.mSavedFragmentState);
                    dispatchOnFragmentActivityCreated(f, f.mSavedFragmentState, false);
                    if (f.mView != null) {
                        f.restoreViewState(f.mSavedFragmentState);
                    }
                    f.mSavedFragmentState = null;
                }
            case Fragment.ACTIVITY_CREATED:
                if (newState > Fragment.ACTIVITY_CREATED) {
                    f.mState = Fragment.STOPPED;
            }
            case Fragment.STOPPED:
                if (newState > Fragment.STOPPED) {
                    if (DEBUG) Log.v(TAG, "moveto STARTED: " + f);
                    //注释7处，fragment内部调用 onStart 方法
                    f.performStart();
                    dispatchOnFragmentStarted(f, false);
                }
            case Fragment.STARTED:
                if (newState > Fragment.STARTED) {
                    if (DEBUG) Log.v(TAG, "moveto RESUMED: " + f);
                    //注释8处，fragment内部调用 onResume 方法
                    f.performResume();
                    dispatchOnFragmentResumed(f, false);
                    f.mSavedFragmentState = null;
                    f.mSavedViewState = null;
                }
        }
    } else if (f.mState > newState) {
        switch (f.mState) {
            case Fragment.RESUMED:
                if (newState < Fragment.RESUMED) {
                    if (DEBUG) Log.v(TAG, "movefrom RESUMED: " + f);
                    //注释9处，fragment内部调用 onPause 方法
                    f.performPause();
                    dispatchOnFragmentPaused(f, false);
                }
            case Fragment.STARTED:
                if (newState < Fragment.STARTED) {
                    if (DEBUG) Log.v(TAG, "movefrom STARTED: " + f);
                    //注释10处，fragment内部调用 onStop 方法
                    f.performStop();
                    dispatchOnFragmentStopped(f, false);
                }
            case Fragment.STOPPED:
                if (newState < Fragment.STOPPED) {
                    if (DEBUG) Log.v(TAG, "movefrom STOPPED: " + f);
                    f.performReallyStop();
                }
            case Fragment.ACTIVITY_CREATED:
                if (newState < Fragment.ACTIVITY_CREATED) {
                    if (DEBUG) Log.v(TAG, "movefrom ACTIVITY_CREATED: " + f);
                    if (f.mView != null) {
                        // Need to save the current view state if not
                        // done already.
                        if (mHost.onShouldSaveFragmentState(f) && f.mSavedViewState == null) {
                            saveFragmentViewState(f);
                        }
                    }
                    //注释11处，fragment内部调用 onDestroyView 方法
                    f.performDestroyView();
                    dispatchOnFragmentViewDestroyed(f, false);
                    if (f.mView != null && f.mContainer != null) {
                        // Stop any current animations:
                        f.mView.clearAnimation();
                        f.mContainer.endViewTransition(f.mView);
                        AnimationOrAnimator anim = null;
                        if (mCurState > Fragment.INITIALIZING && !mDestroyed
                                && f.mView.getVisibility() == View.VISIBLE
                                && f.mPostponedAlpha >= 0) {
                            anim = loadAnimation(f, transit, false,
                                    transitionStyle);
                        }
                        f.mPostponedAlpha = 0;
                        if (anim != null) {
                            animateRemoveFragment(f, anim, newState);
                        }
                        f.mContainer.removeView(f.mView);
                    }
                    f.mContainer = null;
                    f.mView = null;
                    f.mInnerView = null;
                    f.mInLayout = false;
                }
            case Fragment.CREATED:
                if (newState < Fragment.CREATED) {
                    if (mDestroyed) {
                        // The fragment's containing activity is
                        // being destroyed, but this fragment is
                        // currently animating away.  Stop the
                        // animation right now -- it is not needed,
                        // and we can't wait any more on destroying
                        // the fragment.
                        if (f.getAnimatingAway() != null) {
                            View v = f.getAnimatingAway();
                            f.setAnimatingAway(null);
                            v.clearAnimation();
                        } else if (f.getAnimator() != null) {
                            Animator animator = f.getAnimator();
                            f.setAnimator(null);
                            animator.cancel();
                        }
                    }
                    if (f.getAnimatingAway() != null || f.getAnimator() != null) {
                        // We are waiting for the fragment's view to finish
                        // animating away.  Just make a note of the state
                        // the fragment now should move to once the animation
                        // is done.
                        f.setStateAfterAnimating(newState);
                        newState = Fragment.CREATED;
                    } else {
                        if (DEBUG) Log.v(TAG, "movefrom CREATED: " + f);
                        if (!f.mRetaining) {
                            //注释12处，fragment内部调用 onDestroy 方法
                            f.performDestroy();
                            dispatchOnFragmentDestroyed(f, false);
                        } else {
                            f.mState = Fragment.INITIALIZING;
                        }
                        //注释13处，fragment内部调用 onDetach 方法
                        f.performDetach();
                        dispatchOnFragmentDetached(f, false);
                        if (!keepActive) {
                            if (!f.mRetaining) {
                                makeInactive(f);
                            } else {
                                f.mHost = null;
                                f.mParentFragment = null;
                                f.mFragmentManager = null;
                            }
                        }
                    }
                }
        }
    }

    if (f.mState != newState) {
        Log.w(TAG, "moveToState: Fragment state for " + f + " not updated inline; "
                   + "expected state " + newState + " found " + f.mState);
        f.mState = newState;
    }
}
```


注释1处，调用fragment的onAttach方法。
注释2处，fragment内部调用onCreate方法。方法内部会将mIsCreated置为true

注释3处，这里的mContainer是何时赋值的呢？这个开始看源码时候的一个疑惑。

```java
public class FragmentActivity extends BaseFragmentActivityApi16 implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        ActivityCompat.RequestPermissionsRequestCodeValidator {

    final FragmentController mFragments = FragmentController.createController(new HostCallbacks());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mFragments.attachHost(null /*parent*/);
    }
    
    //...


}
```

在FragmentActivity的onCreate方法中，调用了FragmentController的attachHost方法进行了赋值。

```java
/**
 * Attaches the host to the FragmentManager for this controller. The host must be
 * attached before the FragmentManager can be used to manage Fragments.
 */
public void attachHost(Fragment parent) {
    mHost.mFragmentManager.attachController(
         mHost, mHost /*container*/, parent);
}
```
FragmentManagerImpl的 attachController 方法。
```java
public void attachController(FragmentHostCallback host,
            FragmentContainer container, Fragment parent) {
    if (mHost != null) throw new IllegalStateException("Already attached");
    mHost = host;
    //为mContainer赋值
    mContainer = container;
    mParent = parent;
}
```

注释4处，fragment内部调用onCreateView方法。
注释5处，fragment调用onViewCreated方法。
注释6处，fragment内部调用 onActivityCreated 方法。
注释7处，fragment内部调用 onStart 方法。
注释8处，fragment内部调用 onResume 方法。
注释9处，fragment内部调用 onPause 方法。
注释10处，fragment内部调用 onStop 方法。
注释11处，fragment内部调用 onDestroyView 方法。
注释12处，fragment内部调用 onDestroy 方法。
注释13处，fragment内部调用 onDetach 方法。

好：本篇到此结束，比较潦草，但是大致原理懂了，后续会逐渐完善。