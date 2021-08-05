[TOC]

# 概念

设计模式，即Design Patterns，是指在软件设计中，被反复使用的一种代码设计经验。

# 为什么要使用设计模式？

使用设计模式的目的是为了可重用代码，提高代码的可扩展性和可维护性。

# 开闭原则

软件应该对扩展开放，而对修改关闭；

这里的意思是在增加新功能的时候，能不改代码就尽量不需要改，如果新增代码就能完成，那是最好的。

# 里式替换原则

就是说我们调用父类的一个方法可以成功，那么替换成子类调用也应该完全可以运行

# 创建型设计模式

创建型模型关注点是如何创建对象，其核心思想是要把对象的创建和使用相分离，这样使两者能够相对独立的变换。

创建型模式包括：

- 工厂方法：Factory Method
- 抽象工厂：Abstract Method
- 创造者：Builder
- 原型：Prototype
- 单例：Singleton

## 工厂方法

### 概念

定义一个创建对象的接口，让子类决定实例化哪个类。Factory Method使一个类的实例化延迟到其子类。

````mermaid
graph BT
productImpl[ProductImpl]-->product[Product]
factoryImpl[FactoryImpl]--->factory[Factory]
factoryImpl.->productImpl

````

### 工厂接口

```java
package com.maben.remote_debug.design_patterns.factory_method;

/**
 * 解析字符串到Number
 */
public interface NumberFactory {
    /**
     * 字符串转Number
     * @param s s
     * @return number
     */
    Number parse(String s);

    static NumberFactory impl = new NumberFactoryImpl();
    /**
     * 获取实现类实例
     * @return 实现类
     */
    static NumberFactory getFactory() {
        return impl;
    }
}
```

### 工厂实现类

````java
package com.maben.remote_debug.design_patterns.factory_method;

import java.math.BigDecimal;

public class NumberFactoryImpl implements NumberFactory {
    @Override
    public Number parse(String s) {
        return new BigDecimal(s);
    }
}
````

### 测试类

```java
package com.maben.remote_debug.design_patterns.factory_method;

public class MT001 {
    public static void main(String[] args) {
        final NumberFactory factory = NumberFactory.getFactory();
        System.out.println(factory.parse("1245"));
    }
}
```

### 总结

调用方可以完全忽略真正的工厂`NumberFactoryImpl`和实际的产品`BigDecimal`，这样做的好处是允许创建产品的代码独立地变换，而不会影响到调用方。

比方说：发消息场景。工厂类中有发消息接口，但是工厂实现类中需要传递发什么消息的参数，有短信、有APP信息等等，但是里面具体是哪个调用端不关心。

这种简化的使用静态方法创建产品的方式称为 <font color="red">静态工厂方法（Static Factory Method）</font>。静态工厂方法广泛地应用在Java标准库中。

例如：(MD5)

```java
MessageDigest md5 = MessageDigest.getInstance("MD5");
MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
```

## 抽象工厂

### 概念

提供一个创建一系列相关或相关依赖对象的接口，而无需指定他们具体的类。

这种模式有点类似于多个供应商负责提供一系列类型的产品。

````mermaid
graph LR
client....->factory
factory...->productA
factory...->productB

factory1------>factory
factory1.->productA1
factory1.->productB1

factory2------>factory
factory2.->productA2
factory2.->productB2
````

### 工厂接口

````java
package com.maben.remote_debug.design_patterns.abstract_factory.service;

public interface AbstractFactory {
    // 创建Html文档:
    HtmlDocument createHtml(String md);
    // 创建Word文档:
    WordDocument createWord(String md);
}
````

### 工厂接口实现类

#### FastFactory

````java
package com.maben.remote_debug.design_patterns.abstract_factory.fast_factory;


import com.maben.remote_debug.design_patterns.abstract_factory.service.AbstractFactory;
import com.maben.remote_debug.design_patterns.abstract_factory.service.HtmlDocument;
import com.maben.remote_debug.design_patterns.abstract_factory.service.WordDocument;

public class FastFactory implements AbstractFactory {

	@Override
	public HtmlDocument createHtml(String md) {
		return new FastHtmlDocument(md);
	}

	@Override
	public WordDocument createWord(String md) {
		return new FastWordDocument(md);
	}
}
````

#### GoodFactory

````java
package com.maben.remote_debug.design_patterns.abstract_factory.good_factory;

import com.maben.remote_debug.design_patterns.abstract_factory.service.AbstractFactory;
import com.maben.remote_debug.design_patterns.abstract_factory.service.HtmlDocument;
import com.maben.remote_debug.design_patterns.abstract_factory.service.WordDocument;

public class GoodFactory implements AbstractFactory {

	@Override
	public HtmlDocument createHtml(String md) {
		return new GoodHtmlDocument(md);
	}

	@Override
	public WordDocument createWord(String md) {
		return new GoodWordDocument(md);
	}
}

````

### 产品接口

#### HtmlDocument

````java
package com.maben.remote_debug.design_patterns.abstract_factory.service;

import java.io.IOException;
import java.nio.file.Path;

public interface HtmlDocument {
    String toHtml();
    void save(Path path) throws IOException;
}
````

#### WordDocument

````java
package com.maben.remote_debug.design_patterns.abstract_factory.service;

import java.io.IOException;
import java.nio.file.Path;

public interface WordDocument {
    void save(Path path) throws IOException;
}
````

### 产品接口实现类

#### FastHtmlDocument

````java
package com.maben.remote_debug.design_patterns.abstract_factory.fast_factory;

import com.maben.remote_debug.design_patterns.abstract_factory.service.HtmlDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class FastHtmlDocument implements HtmlDocument {

	private String md;

	public FastHtmlDocument(String md) {
		this.md = md;
	}

	@Override
	public String toHtml() {
		return "FastHtmlDocument -- save --- "+md;
	}

	@Override
	public void save(Path path) throws IOException {
		Files.write(path, toHtml().getBytes("UTF-8"));
	}
}
````

#### GoodHtmlDocument

````java
package com.maben.remote_debug.design_patterns.abstract_factory.good_factory;

import com.maben.remote_debug.design_patterns.abstract_factory.service.HtmlDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GoodHtmlDocument implements HtmlDocument {

	private String md;

	public GoodHtmlDocument(String md) {
		this.md = md;
	}

	@Override
	public String toHtml() {
		return "GoodHtmlDocument --- save --- "+md;
	}

	@Override
	public void save(Path path) throws IOException {
		Files.write(path, toHtml().getBytes("UTF-8"));
	}
}
````

#### FastWordDocument

````java
package com.maben.remote_debug.design_patterns.abstract_factory.fast_factory;

import com.maben.remote_debug.design_patterns.abstract_factory.service.WordDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class FastWordDocument implements WordDocument {

	private String md;

	public FastWordDocument(String md) {
		this.md = md;
	}

	@Override
	public void save(Path path) throws IOException {
		String content = "FastWordDocument -- save --- "+md;
		Files.write(path, content.getBytes("UTF-8"));
	}
}
````

#### GoodWordDocument

````java
package com.maben.remote_debug.design_patterns.abstract_factory.good_factory;

import com.maben.remote_debug.design_patterns.abstract_factory.service.WordDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GoodWordDocument implements WordDocument {

	private String md;

	public GoodWordDocument(String md) {
		this.md = md;
	}

	@Override
	public void save(Path path) throws IOException {
		String content = "GoodWordDocument --> save --- "+md;
		Files.write(path, content.getBytes("UTF-8"));
	}
}
````

### 测试类

````java
package com.maben.remote_debug.design_patterns.abstract_factory;

import com.maben.remote_debug.design_patterns.abstract_factory.fast_factory.FastFactory;
import com.maben.remote_debug.design_patterns.abstract_factory.good_factory.GoodFactory;
import com.maben.remote_debug.design_patterns.abstract_factory.service.AbstractFactory;
import com.maben.remote_debug.design_patterns.abstract_factory.service.HtmlDocument;
import com.maben.remote_debug.design_patterns.abstract_factory.service.WordDocument;

import java.io.IOException;
import java.nio.file.Paths;

public class MT001 {
    public static void main(String[] args) throws IOException {
        AbstractFactory fastFactory = new FastFactory();
        HtmlDocument fastHtml = fastFactory.createHtml("#Hello\nHello, world!");
        System.out.println(fastHtml.toHtml());
        fastHtml.save(Paths.get(".", "fast.html"));
        WordDocument fastWord = fastFactory.createWord("#Hello\nHello, world!");
        fastWord.save(Paths.get(".", "fast.doc"));

        AbstractFactory goodFactory = new GoodFactory();
        HtmlDocument goodHtml = goodFactory.createHtml("#Hello\nHello, world!");
        System.out.println(goodHtml.toHtml());
        goodHtml.save(Paths.get(".", "good.html"));
        WordDocument goodWord = goodFactory.createWord("#Hello\nHello, world!");
        goodWord.save(Paths.get(".", "good.doc"));
    }
}
````



