# InputMeasure

implementation --> [maven](https://github.com/mmm3w/maven)

### 输入法高度测量


```
//初始化，可不调用，不调用的话将不缓存键盘高度，相关数据自行处理
InputHeight.init(context)

//测量绑定，会通过生命周期自动解绑
InputHeight.bindMeasure(AppCompatActivity)

//监听事件，建议自行做全局分发
InputHeight.callback{ height->
    ...
}
```