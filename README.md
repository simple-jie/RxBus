# RxBus for Android
## A event bus depend on RxJava RxAndroid

  There have many RxBus on github, but not i wanted. So I decide to build this.
I don't like to define a class for every EVENT， also I like handle a pile of events in one method like handleMessage of Handler

Thanks for [AndroidKnife/RxBus](https://github.com/AndroidKnife/RxBus)

>All the event target by a int value

Maybe I was wrong. Welcome send suggest to me about anything.

####Dependency
```java
    compile "io.reactivex:rxjava:1.1.5"
    compile "io.reactivex:rxandroid:1.2.0"
```

>register and unregister

```java
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.getDefault().register(this);
    }
    
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unRegister(this);// must unregister manually when never use it anymore
    }
```

> define handle method use @Subscribe annotation

* eventId : the events this method will handle
* thread : which thread these events will be post to

```java
    @Subscribe(eventId = 7, thread = PostingThread.COMPUTATION)
    public void onEvent(int id, CustomEvent arg) {
        // CustomEvent is a specific Event class
    }
    
    or
    
    @Subscribe(eventId = {7, 8}, thread = PostingThread.COMPUTATION)
    public void onEvent(int id, CustomEvent arg) {
        // CustomEvent is a specific Event class
    }
    
    or
    
    @Subscribe(eventId = {6, 7, 8, 9, 10, 11, 12}, thread = PostingThread.MAIN)
    public void onEvent(int id, Object[] args) {
        if (id < 7)
            super.onEvent(id, args);
    }
```

> about extends

```java
 class A {
    @Subscribe(eventId = {1, 2, 3, 4, 5, 6, 7}, thread = PostingThread.IO)
    public void onEvent(int id, Object[] args) {
       
    }
 }
 
 class B extends A {
    @Subscribe(eventId = {6, 7, 8, 9, 10, 11, 12}, thread = PostingThread.MAIN)
    public void onEvent(int id, Object[] args) {
       
    }
 }
```

if a instance of class B register to bus    
it will receive all the event from 1-12    
1, 2, 3, 4, 5 will be post to IO thread        
6 and 7 will be post to MAIN thread (_same as child define_)
8, 9, 10, 11, 12 will be post to MAIN thread      


###dependency snippet: Gradle

```java
compile 'com.simplejie.toolkit.rxbus:rxbus:1.0.1'
```

###dependency snippet: Maven

```java
<dependency>
  <groupId>com.simplejie.toolkit.rxbus</groupId>
  <artifactId>rxbus</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```




