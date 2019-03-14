# TaskReminder
防健忘小东西

## 基本容器
  
  用“基本容器”来指代那些我觉得是这个程序中最最基本需要的东西。包括
  * SingleTask. 一个数据类。就是TaskReminder里的 Task ，下文也简称 Task
  * MainActivity. 显示已有的Task
  * TaskAdding. 这个名字不是很好。其实他是另外一个activity,用于增加新的Task
  * InfoPanel. 一个显示数据的面板。在修改 Task 内容的时候弹出。总觉得上面的 TaskAdding 其实也应该这样做
  * LocalReceiver. 本地广播接收器

## 界面组织

  整个程序的组织结构基本上是围绕界面来进行的。于是就写这个界面的构成吧。
  
  ### MainActivity

---
## 总结
算起来做这个的总时间只有6天，每天3~4小时的样子,这么看的话要是每天从早肝到晚，可能一两天就能搞定了【秃头警告

已经尽可能将各个部分分开，一个类做一件事，最后在activity/layout里执行，不过还是有不满意的地方

--比如始终会有A的功能需要在B中完成

比如说：

---
最后分出了3个功能类型的类【不知道这么说准不准确】

```
  class SQLOperator;
  class BroadcastOperator;
  class AnimationOperator;
```

SQLOperator 用于数据库的增删改查事务, 包含以下静态方法
```
  // 查找符合某些要求的Task项
  //用法：list = SQLOperator.executeQuery("select * from TaskInfo where finished = ?", str);
  //咦怎么用起来这么复杂。。。不好意思之后一定把这个改了
  public static List<SingleTask> executeQuery(String query, String[] args);
  
  // 插入新Task, 参数名和参数值分别放在 paras 和 values 里
  public static void insertTask(String[] paras, String[] values);
  
  // 删除某个task
  public static void deleteDataById(int id);
  
  // 修改某个task， 参数和参数名放在 ContentValues里
  public static void updateDataById(int id, ContentValues contentValues);
```

BroadcastOperator 用于本地广播有关的事物，包含以下静态参数和方法
```
  private static LocalBroadcastManager localBroadcastManager;
  private static LocalReceiver localReceiver;
  private static final String renewData = "com.hu.tr_v1.RENEW_DATA";
  
  // 每当有新消息时，调用这个函数以更新界面上显示的task项
  public static void broadcastRenewing();
```

AniamtionOperatior 用于执行动画，包含以下静态参数和方法
```
  private static InfoPanel infoPanel;
  
  // InfoPanel 的进入动画
  public static void infoPanelIn(SingleTask task);
  
  // InfoPanel 的退出动画
  public static void infoPanelOut();
```

问题【需改进】
1. 类内的相似功能函数不够规范。函数之间的返回值与调用参数都有不同，这样调用的时候会让人摸不着头脑。写这些代码的时候由于是分开写的，所以还真没多大感觉，
现在看起来就是乱七八糟了
2. AnimationOperator 里的 infoPanelIn() 函数。传入参数SingleTask其实只是为了最后能为infoPanel进行初始化。这个小功能点应该从这个函数中分离才对。
3. 是不是真的有必要？这个要之后好好想一下。 **因为从内存的方面来看，这样似乎会占用很多的资源，还从不释放**这句话未经考据
4. 续3.比如 LocalReceiver 应该是包含多个 **本地广播接收器** 的类，而不是将一个接收器就作为一个类
5. 当然上面得到的这些需改进的地方，可能这个想法就是错的。多敲代码就知道了。
还有。为啥 Android studio 的 version control 老是不能直接连上git。待我搞搞
