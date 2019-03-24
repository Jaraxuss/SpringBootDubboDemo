# SpringBoot + Dubbo 最小化工程

工程分为三部分：

1. 公共类库
2. 服务提供方
3. 服务消费方



## 公共类库

这里包含了公共的 service 接口，公共的 model 等。

该工程只包含实例接口文件 `TestService`，其中只包含 `sayHello` 接口，供服务方实现，消费方调用。目录结构大致如下：

```
.
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── dubbostartup
    │   │           └── common
    │   │               ├── bean
    │   │               └── service
    │   │                   └── TestService.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── com
                └── dubbostartup
                    └── common
```

TestService.java

```java
package com.dubbostartup.common.service;

public interface TestService {

    String sayHello(String name);

}
```

pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.dubbostartup</groupId>
    <artifactId>common</artifactId>
    <version>1.0-SNAPSHOT</version>
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>4.3.13.RELEASE</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>

</project>
```

使用 `mvn clean package -Dmaven.test.skip=true` 将其打成 jar 包，并安装至本地仓库。



## 服务提供方

服务提供方实现了 TestService，供消费方调用。

#### 目录结构

```
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── dubbostartup
    │   │           └── provider
    │   │               ├── DemoApplication.java
    │   │               └── TestServiceImpl.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── com
                └── dubbostartup
                    └── provider
```

DemoApplication.java

```java
package com.dubbostartup.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

@EnableDubbo
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
```

TestServiceImpl.java

```java
package com.dubbostartup.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.dubbostartup.common.service.TestService;

@Service(version = "${demo.service.version}")
public class TestServiceImpl implements TestService {

    @Override
    public String sayHello(String name) {

        System.out.println("---------------------------");

        return "Hi, " + name + "!";
    }
}
```

application.properties

```properties
spring.application.name = dubbo-provider
server.port = 8080

# 当前服务的名称
dubbo.application.name = springboot-demo-service-provider

demo.service.version = 1.0.0

# 指定通信规则（通信协议:通信端口）
dubbo.protocol.name = dubbo
dubbo.protocol.port = 20880

# 注册中心的地址 这里注册中心用的是zookeeper
dubbo.registry.address = zookeeper://localhost:2181

dubbo.provider.timeout = 50000
```

pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.3.RELEASE</version>
        <relativePath/>
        <!-- lookup parent from repository -->
    </parent>
    <groupId>com.dubbostartup</groupId>
    <artifactId>provider</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.6.4</version>
        </dependency>

        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.8</version>
        </dependency>

        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
            <version>2.12.0</version>
        </dependency>

        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.33.Final</version>
        </dependency>

        <dependency>
            <groupId>com.dubbostartup</groupId>
            <artifactId>common</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```



## 服务消费方

消费方对 common jar 包中的接口，使用 `@Reference` 注解引用服务，从而进行 RPC 调用。

#### 目录结构

```
.
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── dubbostartup
    │   │           └── consumer
    │   │               ├── DemoApplication.java
    │   │               └── controller
    │   │                   └── TestController.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── com
                └── dubbostartup
                    └── consumer
```

DemoApplication.java

```java
package com.dubbostartup.consumer;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

TestServiceImpl.java

```java
package com.dubbostartup.consumer.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.alibaba.dubbo.config.annotation.Reference;
import com.dubbostartup.common.service.TestService;

@RestController
public class TestController {

    @Reference(version = "${demo.service.version}")
    private TestService testService;

    @RequestMapping("/sayHello/{name}")
    public String sayHello(@PathVariable("name") String name) {
        return testService.sayHello(name);
    }
}
```

application.properties

```properties
spring.application.name = dubbo-consumer
server.port = 8888

# 当前服务的名称
dubbo.application.name = springboot-demo-service-consumer

demo.service.version = 1.0.0

# 注册中心的地址 这里注册中心用的是 zookeeper
dubbo.registry.address = zookeeper://localhost:2181

dubbo.consumer.timeout = 50000
```

pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.3.RELEASE</version>
        <relativePath/>
        <!-- lookup parent from repository -->
    </parent>
    <groupId>com.dubbostartup</groupId>
    <artifactId>consumer</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
        <zookeeper.version>3.4.13</zookeeper.version>
        <dubbo.starter.version>0.2.0</dubbo.starter.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.6.4</version>
        </dependency>

        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.33.Final</version>
        </dependency>

        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.8</version>
        </dependency>

        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
            <version>2.12.0</version>
        </dependency>

        <dependency>
            <groupId>com.dubbostartup</groupId>
            <artifactId>common</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```



## 运行

1. 启动 provider (一定要在 consumer 之前启动)

   在 provider 目录下执行 `mvn spring-boot:run`

   看到如下日志表示 provider 服务启动成功：

   ```verilog
   2019-03-20 22:13:42.699  INFO 43567 --- [           main] org.apache.zookeeper.ZooKeeper           : Initiating client connection, connectString=localhost:2181 sessionTimeout=60000 watcher=org.apache.curator.ConnectionState@32f69400
   2019-03-20 22:13:42.716  INFO 43567 --- [89.222.14:2181)] org.apache.zookeeper.ClientCnxn          : Opening socket connection to server localhost/localhost:2181. Will not attempt to authenticate using SASL (unknown error)
   2019-03-20 22:13:42.752  INFO 43567 --- [89.222.14:2181)] org.apache.zookeeper.ClientCnxn          : Socket connection established to localhost/localhost:2181, initiating session
   2019-03-20 22:13:42.793  INFO 43567 --- [89.222.14:2181)] org.apache.zookeeper.ClientCnxn          : Session establishment complete on server localhost/localhost:2181, sessionid = 0x1008a76d8620019, negotiated timeout = 40000
   2019-03-20 22:13:42.799  INFO 43567 --- [ain-EventThread] o.a.c.f.state.ConnectionStateManager     : State change: CONNECTED
   2019-03-20 22:13:42.987  INFO 43567 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
   2019-03-20 22:13:42.991  INFO 43567 --- [           main] c.dubbostartup.provider.DemoApplication  : Started DemoApplication in 3.527 seconds (JVM running for 6.515)
   ```

   在 zookeeper 中看到类似如下日志表示 provider 与服务注册中心连接成功：

   ```
   2019-03-20 14:13:42,596 [myid:] - INFO  [NIOServerCxn.Factory:0.0.0.0/0.0.0.0:2181:NIOServerCnxnFactory@215] - Accepted socket connection from /117.89.210.169:35413
   2019-03-20 14:13:42,601 [myid:] - INFO  [NIOServerCxn.Factory:0.0.0.0/0.0.0.0:2181:ZooKeeperServer@949] - Client attempting to establish new session at /117.89.210.169:35413
   2019-03-20 14:13:42,607 [myid:] - INFO  [SyncThread:0:ZooKeeperServer@694] - Established session 0x1008a76d8620019 with negotiated timeout 40000 for client /117.89.210.169:35413
   ```

2. 启动 consumer

   在 consumer 目录下执行 `mvn spring-boot:run`

   看到如下日志表示 consumer 服务启动成功：

   ```
   2019-03-20 22:13:50.045  INFO 43581 --- [           main] org.apache.zookeeper.ZooKeeper           : Initiating client connection, connectString=localhost:2181 sessionTimeout=60000 watcher=org.apache.curator.ConnectionState@655487cc
   2019-03-20 22:13:50.060  INFO 43581 --- [89.222.14:2181)] org.apache.zookeeper.ClientCnxn          : Opening socket connection to server localhost/localhost:2181. Will not attempt to authenticate using SASL (unknown error)
   2019-03-20 22:13:50.112  INFO 43581 --- [89.222.14:2181)] org.apache.zookeeper.ClientCnxn          : Socket connection established to localhost/localhost:2181, initiating session
   2019-03-20 22:13:50.162  INFO 43581 --- [89.222.14:2181)] org.apache.zookeeper.ClientCnxn          : Session establishment complete on server localhost/localhost:2181, sessionid = 0x1008a76d862001a, negotiated timeout = 40000
   2019-03-20 22:13:50.167  INFO 43581 --- [ain-EventThread] o.a.c.f.state.ConnectionStateManager     : State change: CONNECTED
   2019-03-20 22:13:50.640  INFO 43581 --- [           main] c.a.d.c.s.b.f.a.ReferenceBeanBuilder     : <dubbo:reference object="com.alibaba.dubbo.common.bytecode.proxy0@6b0a2687" singleton="true" interface="com.dubbostartup.common.service.TestService" uniqueServiceName="com.dubbostartup.common.service.TestService:1.0.0" generic="false" version="1.0.0" id="com.dubbostartup.common.service.TestService" /> has been built.
   2019-03-20 22:13:50.968  INFO 43581 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
   2019-03-20 22:13:51.209  INFO 43581 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8888 (http) with context path ''
   2019-03-20 22:13:51.213  INFO 43581 --- [           main] c.dubbostartup.consumer.DemoApplication  : Started DemoApplication in 3.337 seconds (JVM running for 6.161)
   ```

   在 zookeeper 中看到类似如下日志表示 consumer 与服务注册中心连接成功：

   ```
   2019-03-20 14:13:49,955 [myid:] - INFO  [NIOServerCxn.Factory:0.0.0.0/0.0.0.0:2181:NIOServerCnxnFactory@215] - Accepted socket connection from /117.89.210.169:35432
   2019-03-20 14:13:49,960 [myid:] - INFO  [NIOServerCxn.Factory:0.0.0.0/0.0.0.0:2181:ZooKeeperServer@949] - Client attempting to establish new session at /117.89.210.169:35432
   2019-03-20 14:13:49,970 [myid:] - INFO  [SyncThread:0:ZooKeeperServer@694] - Established session 0x1008a76d862001a with negotiated timeout 40000 for client /117.89.210.169:35432
   ```

3. 调用 consumer 接口

   ```shell
   curl 'http://localhost:8888/sayHello/Velen'
   ```

   看到终端返回 `Hi, Velen!` 表示调用成功，SpringBoot + Dubbo 最小化工程搭建完毕。

   

