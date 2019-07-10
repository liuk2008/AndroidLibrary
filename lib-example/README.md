# AndroidLibrary #

## lib-example ##

**Android**

    * 1、封装网络框架：AsyncTask + CallBack
    * 2、增加MVP项目框架
    * 3、增加网络检测功能，配置动态UI提示

**Java**

	* 分析Java中线程、反射、注解、集合、引用类型等原理

**RxJava2**

	* 1、事件关系
		 1、上游可以发送无限个onNext, 下游也可以接收无限个onNext
	     2、当上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送, 而下游收到onComplete事件之后将不再继续接收事件
	     3、当上游发送了一个onError后, 上游onError之后的事件将继续发送, 而下游收到onError事件之后将不再继续接收事件
	     4、上游可以不发送onComplete或onError
	     5、最为关键的是onComplete和onError必须唯一并且互斥，不能先发一个onComplete， 然后再发一个onError，反之亦然
	     注意:
	         1、调用dispose()并不会导致上游不再继续发送事件, 上游会继续发送剩余的事件，可以通过 CompositeDisposable 统一管理订阅关系
	         2、上下游默认是在同一个线程工作，在主线程
	         3、Observable (被观察者)只有在被Observer (观察者)订阅后才能执行其内部的相关逻辑
	* 2、线程调度
	     1、只有第一个subscribeOn()起作用，其次 subscribeOn() 控制从流程开始的第一个操作，直到遇到第一个observeOn()
	     2、observeOn()可以使用多次，每个observeOn()将导致一次线程切换，指定的是observeOn() 之后的操作所在的线程
	     3、不论是 subscribeOn() 还是 observeOn()，每次线程切换如果不受到下一个observeOn()的干预，线程将不再改变，不会自动切换到其他线程。
	     4、在不指定线程的情况下，RxJava遵循线程不变原则，即：在哪个线程调用subscribe()，就在哪个线程生产事件；在哪个线程生产事件，就在哪个线程消费事件。
	 	 5、线程类型：
	     * Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作
	     * Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作
	     * Schedulers.newThread() 代表一个常规的新线程
	     * AndroidSchedulers.mainThread() 代表Android的主线程
    * 3、注意事项
         1、线程调度问题
         2、大数据量事件发送时产生的背压问题，及处理背压的策略