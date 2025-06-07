# jimmer-ddd

jimmer-ddd不仅是对jimmer的扩展，更是对ddd艺术的扩展和诠释

## 炸裂功能：
1. jimmer仅支持贫血模型，jimmer-ddd支持了充血模型

2. jimmer-ddd实现了从仓储中加载聚合根时，通过预分析字节码，仅查询需要的加载的字段

3. jimmer-ddd实现了仅update更新过的字段，即仅更新需要更新的字段

4. jimmer-ddd性能极强，显著强于hibernate ，一是jimmer-ddd做到了在操作处理orm对象的情况下，查询和修改都和原生sql一致，二是关键对象也能做到预分析，仅查询需要的属性，达到性能比肩原生sql的炸裂效果。

5. jimmer-ddd实现了任意字段懒加载，并且仅需一个注解搞定，显著强于hibernate

6. jimmer-ddd能解决任意结构的变更追踪和懒加载，而jimmer本身并没有实现该功能

7. jimmer-ddd能够自动保存聚合根，减少了大量save的代码，平齐hibernate

8. jimmer-ddd能够延迟发送事件，即在保存聚合根以后自动或手动发送事件，更加符合领域事件的常用情况，显著增强了spring事件的能力

9. jimmer-ddd还能够将数据库多表映射到单聚合根上(不是通过关联关系)，极大的增强了映射能力

10. jimmer-ddd是编译期依赖，可以兼容任何版本的jimmer，甚至魔改和自编译版本的jimmer

### 下面是jimmer框架在ddd中的优势：
jimmer-dto减少ddd中大量的类转化代码

## 谁要是还说ddd影响性能，你就把jimmer-ddd拍他脸上让他说话！！

## 如何使用？

方式1：  [jimmer-ddd-sample](https://github.com/morecup/jimmer-ddd-sample) 项目中有详细的例子，以及对ddd六边形架构的具体实现

方式2： 下载源码，常用的方法每一个都有详尽的说明

## 引入和后续更新
### 引入
引入jimmer-ddd，需要引入jimmer的依赖，并且引入jimmer-ddd的依赖
#### java
```xml
<dependency>
    <groupId>io.github.morecup.jimmer-ddd</groupId>
    <artifactId>jimmer-ddd-java-spring-boot-starter</artifactId>
    <version>0.2.2</version>
</dependency>
```
gradle
```groovy
implementation("io.github.morecup.jimmer-ddd:jimmer-ddd-java-spring-boot-starter:0.2.2")
```
#### kotlin
```xml
<dependency>
    <groupId>io.github.morecup.jimmer-ddd</groupId>
    <artifactId>jimmer-ddd-kotlin-spring-boot-starter</artifactId>
    <version>0.2.2</version>
</dependency>
```
gradle
```groovy
implementation("io.github.morecup.jimmer-ddd:jimmer-ddd-kotlin-spring-boot-starter:0.2.2")
```
### 后续更新
最新版本可以去 https://central.sonatype.com/artifact/io.github.morecup.jimmer-ddd/jimmer-ddd-kotlin-spring-boot-starter 查看

## todo
1. 关联对象保存时，自动不保存没有修改的关联对象（可能涉及到定制化jimmer）

## 交流
QQ群：1041852155
欢迎加入QQ群交流，一起学习和进步
