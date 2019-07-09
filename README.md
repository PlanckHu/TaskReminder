# TaskReminder
防健忘小东西

这是一篇记录

既然错过了找实习，那么暑假就把这个东西好好完善吧
1. 将主界面做成能左右滑动的分类界面   |所有|未完成|已完成|
2. 名称排序，时间排序
3. 之前的这个似乎是一旋转就死，那么就解决这个问题顺便做个好看点的旋转变换效果吧，就是竖屏->横屏  横屏->竖屏
4. 如果上面的全部弄完，就加入任务下的子任务划分功能 & 甘特图功能吧

update 2019.6.18

---

遇到了一个问题，用了我没有想到过的方法来解决，记录一下【太强了orz...这大概就是从源头出发找解决方法吧

问题是这样的：在用 ViewPager 的时候，我将他设定为在第 N 个页面进行刷新后，页面刷新，但仍然保持在页面 N。我所使用的页面刷新方法是，数据更新一次，ViewPager 就进行一次初始化【我觉得这不是一个好的办法，之后继续想想别的解决方案】。可是在ViewPager的默认设置中，一旦初始化，页面会自动跳转到第0个界面（也就是adapter的初始位置默认值为0）,如果在初始化后马上跳转到想要的页面（比如1），会造成页面闪烁。于是可以对adapter初始化后用反射机制修改初始值
```java
private static void initViewPager(int position){
        final ViewPager viewPager = main.findViewById(R.id.view_pager);
        viewPager.setAdapter(initViewPageAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //...
        });

        // 下面这段是用反射的方法将adapter的默认初始值改变为position
        // 太强了orz...
        try {
            Class c = Class.forName("android.support.v4.view.ViewPager");
            Field field = c.getDeclaredField("mCurItem");
            field.setAccessible(true);
            field.setInt(viewPager,position);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        viewPager.getAdapter().notifyDataSetChanged();
        setDataChanged(false);
    }
```

update 2019.6.30

---

之前由于只有3个页面，于是当数据改变时，第二个页面永远不会刷新，
* 在第 1 个页面时，缓存第1，2个页面
* 在第 2 个页面时，缓存第1，2，3个页面
* 在第 3 个页面时，缓存第2，3个页面

就很尴尬

终于找到了正确的页面刷新方法orz

用checkbox来举例子：

首先设置一个数组用于记录数据改变后，页面是否也刷新过了
> private static boolean[] pageRenewed = new boolean[3];

1. 一旦点击了页面中某一项的checkbox，改变了他的值，会触发以下动作
```java
    // DataCache中记录下该条目的改变，将 pageRenewed 里的每一项设为false，然后开始刷新页面
    public void setTaskFinished(int id, boolean finished) {
        DataCache dataCache = DataCache.getInstance();
        dataCache.reviseFinished(id, finished);
        for (int i = 0; i < 3; i++) {
            setPageRenewed(false, i);
        }
        ViewPager viewPager = main.findViewById(R.id.view_pager);
        viewPager.getAdapter().notifyDataSetChanged();
    }
```
2. 重写ViewPagerAdapter的instantiateItem(@NonNull ViewGroup container, int position)。给页面加入tag，方便之后我们知道该页面是在哪个位置上的。且由于该函数在每次页面初始化时调用，所以可以在该函数中设置对应的 pageRenewed值为true
```java
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = datas.get(position);
        view.setTag(position);
        ViewPagerOperator.initRecyclerView(view, position);
        ViewPagerOperator.setPageRenewed(true, position);
        Log.d(TAG, "viewpage " + position);
        container.addView(view);
        return view;
    }
```
3. 接着重写ViewPagerAdapter的 getItemPosition(Object object)，使其根据 pageRenewed 来返回 POSITION_NONE/POSITION_UNCHANGED, **返回 POSITION_NONE 表示页面需要刷新，返回 POSITION_UNCHANGED 表示页面不需要刷新**
```java
    @Override
    public int getItemPosition(@NonNull Object object) {
        int tag = (int) ((View) object).getTag();
        if (ViewPagerOperator.getPageRenewed(tag))
            return POSITION_UNCHANGED;
        else
            return POSITION_NONE;
    }
```

于是就都串起来了，在改变了checkbox值后，刷新了当前页面（其实应该也刷新了缓存页面）。然后在进入无缓存页面时，也由于本来的初始化刷新了数据。也不用每次进入页面都完全初始化一次搞得巨卡了

可喜可贺可喜可贺

update 2019.7.4

哦忘了写

这中间还加入了个DataCache类，用于数据缓存，避免频繁地直接读取数据库

目前DataCache的功能有：
1. 读取数据库，建立缓存数据条目【原始数据】
2. 建立数据修改记录 
3. （被）读取数据，所能读取到的数据是在原始数据的基础上依据数据修改记录进行了修改后得到的【缓存数据】
4. 依据同步指令，依据数据修改记录修改数据库中的原始数据

不过由于数据量其实并不大，所以应该也节约不了多少时间。就是突然想到可以这么搞手痒搞了下orz也不难就是了

依然update 2019.7.4

---

突然想到一个很蠢的问题

我需要点开条目，然后在下面展开具体内容

咦这样的话不是连查看和修改都能整合在上面了吗，不需要一个新的activity来编辑项目

好蠢哈哈哈哈哈没有好好设计的后果

今天先把可以增删改查的改结构之后的v1.0放上来好了

update 2019.7.9
