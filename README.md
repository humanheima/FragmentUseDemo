# simpleuse 包下面是关于Fragment的基础知识
# FragmentUseDemo 单独使用fragment,结合ViewPager 懒加载
1. Fragment 的 setUserVisibleHint方法 只有该 Fragment 在 ViewPager 里才会被调用
2. Fragment 的  setUserVisibleHint 的执行顺序又是在 onCreateView 之前


