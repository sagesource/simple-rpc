## Simple-RPC
    Simple-RPC是基于 Thrift 实现的 Java RPC 框架。
    在个人平时学习的过程总，希望加深对 Thrift 和 RPC 的理解，故开发了一个简单的 RPC 框架。
    Simple-RPC对 Thrift 客户端的改造主要如下：
        1. 服务发现基于 Zookeeper 自动发现，由于 Zookeeper 原生客户端使用较为复杂，因此使用curator-recipes实现
        2. 客户端使用连接池对服务调用进行管理，提升性能。使用 apache commons pool2 进行连接池开发，降低代码复杂度
        3. LoadBalance：当 Zookeeper 节点的 Watcher 监听到节点变更后，通知客户端重新刷新服务器节点信息。
            而负载均衡有很多算法，这里使用了简单的加权轮询的算法
        4. 客户端与 Spring 解耦，通过 simple-rpc-spring 工程，提供 Spring 支持
        5. 客户端自定义支持 Filter，可以在调用远程方法前和方法调用后执行自定义过滤器逻辑
        6. 支持静态路由，指定服务和目标机器信息
    Simple-RPC对 Thrift 服务端的改造主要如下：
        1. 服务自动注册在 Zookeeper，节点格式：/default/服务名称/服务版本号/IP:PORT 服务方应用名称、权重等信息储存在节点数据中
        2. 提供服务创建工厂，服务启动在后台线程中
        3. 服务端与 Spring 解耦，可以通过引入 simple-rpc-spring 工程，提供 Spring 支持
    其他特性：
        1. 重写TProtocolDecorator，自定义消息接收和传递的过程
        2. 添加 Trace 追踪支持：
            通过重写 TMultiplexedProtocol 自定义消息接收和传输的过程，从消息中获取每次请求的 trace 信息。 
            同时，Trace 信息通过 ThreadLocal 写入到当前线程上下文，可以支持服务调用链追踪
            通过SLF4J 的 MDC，存储 traceId 对象，完成日志的 trace 追踪
        3. 上下文 Context：
            存放一次调用的上下文信息，包括调用方信息，接口服务信息，目标服务方信息等。
            使用者可以通过 Filter 获取到 Context，根据信息实现自定义逻辑
        4. Filter 过滤器：
            a. 可以自定义客户端的方法调用前后的过滤器，上线文 Context 对象中保存了整个调用的信息，可以获取并处理
            b. Trace追踪的支持通过 Filter 来实现。
               可以在 ClientStubBeforeFilter ClientStubPostFilter 中实现 trace 信息的上报，并生成 Trace 调用链。
            c. Filter 配置在 properties 中，灵活修改 Filter 的执行流程
        5. 多路复用支持
            服务方通过 MultiplexedServiceProviderFactory 实现同一个端口绑定多个服务。这是推荐的做法，如果每一个服务都绑定一个端口，
            会造成服务方端口无限申请，难以管理并且无法复用。Thrift 的 TMultiplexedProtocol 为我们方便的提供了多路复用的支持。
---
## 模块介绍
### simple-rpc-basic
    存放核心的基本类，工具类等信息
    - entity 自定义实体信息
    - exception 自定义异常
    - utils 工具类 
### simple-rpc-client
    RPC 框架客户端实现，负责创建调用方客户端生成，过滤器等
    - filter 客户端过滤器
    - pool 客户端连接池工厂
    - proxy 服务调用动态代理实现
### simple-rpc-core
    RPC 框架核心模块
    - loadbalance 负载均衡实现
    - protocol 通信协议重写
    - trace 追踪 trace 支持
    - zookeeper zk服务注册与发现 
### simple-rpc-provider
    RPC 框架服务端实现，负责服务端的启动和服务注册等
    - SimpleServiceProviderFactory 简单实现的服务初始化与注册工厂
    - thread 服务端线程
### simple-rpc-spring
    - client 调用方 Spring 支持
### simple-rpc-config
    - 配置管理
### simple-rpc-test
    单元测试

## 使用说明
### 0. 通用配置
    # ZK 连接地址
    export SIMEPLE_RPC_ZK=127.0.0.1:2181
    # 客户端静态路由配置
    export SIMPLERPC_STATIC_ROUTER=serviceName.version=ip:port,....
    # 客户端静态路由 jvm 参数
    -Dsimplerpc.static.router=serviceName.version=ip:port,...
    
### 1. 调用方
    调用方将 API 接口包引入到工程中即可
#### 客户端非 Spring 模式
```java
    package org.sagesource.test.client;

    import org.sagesource.simplerpc.client.proxy.ServiceClientProxy;
    import org.sagesource.simplerpc.basic.entity.ProtocolPoolConfig;
    import org.sagesource.test.api.HelloWorldService;
    
    public class HelloClientDemo {
    	public static void main(String[] args) throws Exception {
            // 创建客户端 Zookeeper 连接池配置
            ProtocolPoolConfig protocolPoolConfig = new ProtocolPoolConfig();
            // zookeeper 连接是否为长连接，如果为 false，每次连接返回到连接池中，都会 close 掉
            protocolPoolConfig.setKeepAlive(true);
            // 连接池连接超时时间
            protocolPoolConfig.setTimeout(300000);
            // 参考 Apache Commons Pool2的 GenericObjectPoolConfig 配置即可
            protocolPoolConfig.setMinIdle(1);
            protocolPoolConfig.setMaxIdle(8);
            protocolPoolConfig.setMaxTotal(8);
            // 创建客户端，使用 Iface 接口模式
            HelloWorldService.Iface client = ServiceClientProxy.createClient(HelloWorldService.Iface.class, "1.0.0", protocolPoolConfig);
            client.sayHello("薛琪");
            // 关闭 ZK 连接，可以在做在关闭工程的钩子上，不需要每次调用后都关闭
            ZookeeperClientFactory.close();
    	}
    }
```
#### 客户端 Spring 模式
```xml
    <!-- 注册 HelloWorldService 服务客户端 -->
        <bean id="helloWorldService" class="org.sagesource.simplerpc.spring.client.ServiceClientFactoryBean" destroy-method="close">
            <property name="interfaceType" value="org.sagesource.test.api.HelloWorldService.Iface"/>
            <property name="serviceVersion" value="1.0.0"/>
            <property name="protocolPoolConfig">
                <bean class="org.sagesource.simplerpc.basic.entity.ProtocolPoolConfig">
                    <property name="maxIdle" value="50"/>
                    <property name="minIdle" value="1"/>
                    <property name="maxTotal" value="50"/>
                </bean>
            </property>
        </bean>
```
```java
    package org.sagesource.test.client;
    
    import org.apache.thrift.TException;
    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.sagesource.test.api.HelloWorldService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.test.context.ContextConfiguration;
    import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    public class SpringClientDemo {
        @Autowired
        @Qualifier("helloWorldService")
        private HelloWorldService.Iface helloWorldService;
	    
        @Test
        public void test2() throws TException {
            LOGGER.info(helloWorldService.sayHello("sage"));
        }
    }
```

### 2. 服务提供方
    服务方定义 API 接口，并实现
#### 服务端非 Spring 模式
```java
    package org.sagesource.test.provider;
    
    import org.junit.Test;
    import org.sagesource.simplerpc.core.zookeeper.ZookeeperClientFactory;
    import org.sagesource.simplerpc.provider.MultiplexedServiceProviderFactory;
    import org.sagesource.simplerpc.provider.dto.ServiceRegisterDto;
    import org.sagesource.test.api.impl.HelloWorldServiceImpl;
    import org.sagesource.test.api.impl.JobServiceImpl;
    import org.springframework.context.support.ClassPathXmlApplicationContext;
    
    import java.util.Arrays;
    import java.util.List;
    public class MultiplexedServiceProviderTest { 
        @Test
        public void test() throws Exception {
            ServiceRegisterDto       serviceRegisterDto  = new ServiceRegisterDto(new HelloWorldServiceImpl(), "1.0.0");
            ServiceRegisterDto       serviceRegisterDto1 = new ServiceRegisterDto(new JobServiceImpl(), "1.0.0");
            List<ServiceRegisterDto> list                = Arrays.asList(serviceRegisterDto, serviceRegisterDto1);
            
            MultiplexedServiceProviderFactory multiplexedServiceProviderFactory = new MultiplexedServiceProviderFactory(8090, list);
            multiplexedServiceProviderFactory.createServiceProvider();
            Thread.sleep(10000);
        }
        
        @After
        public void after() {
        	// 关闭 ZK 连接，可以在做在关闭工程的钩子上，不需要每次调用后都关闭
            MultiplexedServiceProviderFactory.close();
            ZookeeperClientFactory.close();
        }
    }
```
#### 服务端 Spring 模式
```xml
    <!-- 创建服务实现类 Spring Bean 使用 @Service注解服务端的实现类即可-->
    <context:annotation-config/>
    <context:component-scan base-package="org.sagesource.test.api.impl"/>

    <!-- 多路复用 Service Provider -->
    <bean id="serviceProvider" class="org.sagesource.simplerpc.spring.provider.MultiplexedServiceProviderFactoryBean"
          destroy-method="close">
        <property name="port" value="8999"/>
        <property name="configList">
            <list>
                <bean class="org.sagesource.simplerpc.spring.provider.dto.MultiplexedServiceConfig">
                    <property name="weight" value="1"/>
                    <property name="serviceRef" ref="helloWorldService"/>
                    <property name="version" value="1.0.0"/>
                </bean>
                <bean class="org.sagesource.simplerpc.spring.provider.dto.MultiplexedServiceConfig">
                    <property name="weight" value="1"/>
                    <property name="serviceRef" ref="jobService"/>
                    <property name="version" value="1.0.0"/>
                </bean>
            </list>
        </property>
    </bean>
```
```java
    package org.sagesource.test.provider;
    
    import org.springframework.context.support.ClassPathXmlApplicationContext;
    
    public class SpringSimpleProviderTest {
        public static void main(String[] args) {
            // 启动 Spring 工程，通过客户端调用即可
            new ClassPathXmlApplicationContext("classpath*:spring-provider.xml");
        }
    }
```

### 错误码定义


## TODO LIST
1. 规范化日志输出
2. 优雅停机
3. 监控 熔断机制
4. 异常错误码梳理
5. 异步调用
6. Codegen 代码生成工具
7. 本地 mock
8. 超时时间管理
    尽量将客户端的超时配置在服务端，这样比较可控
9. 集成开发框架
    基于Spring-Boot作为集成开发框架
10. 负载均衡 内存计数器