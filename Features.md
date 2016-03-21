## Introduction ##
  * `[*]` ：表示已经计划但未实现
  * `[o]` ：表示已经在框架中实现
  * `[?]` ：表示在讨论中尚未计划
  * `[x]` ：表示经过讨论后被放弃

## Feature List ##

### `[o]` url to action dynamic routing : ###

> 把一个请求路径映射为一个action，如:
```
   http://{server}/{context}/product/list 映射为 product.list : product is controller , list is action
```

> 约定：
```
 1. action name的格式是controller.action，其中controller可以使用'.'分割，如product.category.list
 2. 缺省的controller是home，如果没有指定controller，那么默认是home.{action}
 3. 在url中，controller中的'.'可以用'/'字符代替，表示相同的含义
```

> 规则：
```
 1. /                      ： home.index
 2. /{action}              ： home.{action}
 3. /{controller}/{action} ： {controller}.{action}
```

> 示例：
```
 /welcome               ：home.welcome
 /product/list          ：product.list
 /product/category/list ：product.category.list
 /product.category/list ：product.category.list
```

### `[o]` action method dynamic resolving : ###

> 根据action名称如product.list解析出对应的Java Class和方法以及方法的参数.

> 约定：
```
 1. controller class 是一个public的java class，有一个public且无参的constructor.
 2. action method 是一个public（包括静态与非静态）java method.
 3. controller class都在 app.controllers package 下
```

> 示例：
```
 package controllers;
 public class ProductController {
    public void delete(int id) {
       ...
    }
 }
```
> > action name 'product.delete '将解析出：
```
 controller class : app.controllers.ProductController 或者 app.controllers.Product
 action method    : delete
 action arguments : [{name : 'id' , type: Integer.type}]
```

### `[*]` action arguments dynamic binding : ###


> 从请求参数中根据参数名称取出对应的值并转换为参数的类型

  * 支持8种基本类型及对应的包装类型的自动绑定
  * 支持常用类型`（String,Date,BigDecimal,BigInteger）`的自动绑定
  * 支持上述类型对应数组类型的自动绑定
  * 支持Bean对象的自动绑定

> 约定：
```
 1.数组传递参数有两种方式, 第一种：id=1&id=2  第二种：id=1,2,3,4,5
 2.参数名称区分大小写
```