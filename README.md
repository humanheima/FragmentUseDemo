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

