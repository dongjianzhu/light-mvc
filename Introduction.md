## 项目来由（brief history) ##
我们多年来一直在从事J2EE项目的开发实施，类型从管理系统到生产系统和电子商务网站等等，规模也有小有大，发现大部分项目并不需要太多复杂的技术，也不需要用到太多的框架；

基于对项目实施和开发框架的理解，同时借鉴了一些开源框架（rails、play framework、ASP.NET MVC）的思想，我们创建了这个项目，旨在构建一个简单、统一、规范以及可灵活扩展的基础框架，用于支撑项目的开发。

如果您对此项目有兴趣，请花些时间了解一下，也可参与到我们中来一起[交流、讨论](http://discussions.zoho.com/lightmvc)；您的一点建议或者意见，都有可能会对这个项目产生很大的帮助。


## 设计思想（thinking of design） ##
  * 基于约定：使用自然并且大多数人都容易理解的约定而不是配置来编写代码
  * 插件体系：核心功能完全基于插件来实现，同时也允许应用扩展自己的插件
  * 面向服务：所有Action方法都可以通过插件自动支持AJAX,SOAP等远程请求

## 关于框架（about light-mvc） ##
light-mvc是一个Model-View-Controller框架，但和struts这些经典传统的MVC框架对比，存在一些区别：
```
 Controller : light的controller是一个普通Java对象(POJO）.
 Action     : light的action是指controller中的一个静态或非静态方法（对参数和返回值没有要求）.
 View       : light的view是指把action返回的数据渲染输出到客户端（browser、soap client..）的结果信息（html、json、xml..）.
 Model      : light的model并不存在，框架只关注action的执行，并不关注model是否存在
```

_更多内容请看[主要功能](Features.md)和[快速开始](GettingStarted.md)._

## 如何参与（howto contribute） ##
您可以使用以下任何一种方式参与此项目：
  * [提出建议](http://discussions.zoho.com/lightmvc)
  * [反馈问题](http://code.google.com/p/light-mvc/issues/list)
  * [编写文档](http://code.google.com/p/light-mvc/wiki)
  * [贡献代码](http://code.google.com/p/light-mvc/source/checkout)

_详细内容请浏览wiki文档[如何参与](HowtoContribute.md)._