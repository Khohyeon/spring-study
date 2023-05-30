## Redis를 사용한 메시징
이 가이드는 Spring Data Redis를 사용하여 Redis로 보낸 메시지를 게시하고 구독하는 과정을 안내합니다.

### 무엇을 만들 것인가
StringRedisTemplate를 사용하여 문자열 메시지를 게시하고 POJO가 메시지를 구독하도록 하는 애플리케이션을 빌드합니다 MessageListenerAdapter.

```
Spring Data Redis를 메시지 게시 수단으로 사용하는 것이 이상하게 들릴 수 있지만 Redis는 NoSQL 데이터 저장소뿐만 아니라 메시징 시스템도 제공합니다.
```

### 필요한 것
* 약 15분

* 선호하는 텍스트 편집기 또는 IDE

* 자바 17 이상

* Gradle 7.5+ 또는 Maven 3.5+

* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작 하려면 Redis 서버 세우기 로 이동하십시오 .

기본 사항을 건너뛰 려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-messaging-redis.git
* cd 로gs-messaging-redis/initial
* Spring Initializr로 시작하기 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-messaging-redis/complete.

### Redis 서버 세우기
메시징 응용 프로그램을 구축하기 전에 메시지 수신 및 전송을 처리할 서버를 설정해야 합니다.

Redis는 메시징 시스템과 함께 제공되는 오픈 소스, BSD 라이센스, 키-값 데이터 저장소입니다. 서버는 https://redis.io/download 에서 무료로 사용할 수 있습니다 . 수동으로 다운로드하거나 Mac을 사용하는 경우 Homebrew와 함께 터미널 창에서 다음 명령을 실행하여 다운로드할 수 있습니다.
```
brew install redis
```

Redis의 압축을 풀면 다음 명령을 실행하여 기본 설정으로 시작할 수 있습니다.

```
redis-server
```

다음과 유사한 메시지가 표시되어야 합니다.
```
[35142] 01 May 14:36:28.939 # Warning: no config file specified, using the default config. In order to specify a config file use redis-server /path/to/redis.conf
[35142] 01 May 14:36:28.940 * Max number of open files set to 10032
                _._
              _.-``__ ''-._
        _.-``    `.  `_.  ''-._           Redis 2.6.12 (00000000/0) 64 bit
    .-`` .-```.  ```\/    _.,_ ''-._
  (    '      ,       .-`  | `,    )     Running in stand alone mode
  |`-._`-...-` __...-.``-._|'` _.-'|     Port: 6379
  |    `-._   `._    /     _.-'    |     PID: 35142
    `-._    `-._  `-./  _.-'    _.-'
  |`-._`-._    `-.__.-'    _.-'_.-'|
  |    `-._`-._        _.-'_.-'    |           https://redis.io
    `-._    `-._`-.__.-'_.-'    _.-'
  |`-._`-._    `-.__.-'    _.-'_.-'|
  |    `-._`-._        _.-'_.-'    |
    `-._    `-._`-.__.-'_.-'    _.-'
        `-._    `-.__.-'    _.-'
            `-._        _.-'
                `-.__.-'

[35142] 01 May 14:36:28.941 # Server started, Redis version 2.6.12
[35142] 01 May 14:36:28.941 * The server is now ready to accept connections on port 6379
```

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭 하고 Spring Data Redis를 선택합니다 .

4. 생성 을 클릭합니다 .

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다

### Redis 메시지 수신기 생성
모든 메시징 기반 응용 프로그램에는 메시지 게시자와 메시징 수신자가 있습니다. 메시지 수신자를 생성하려면 다음 예제(에서 src/main/java/com/example/messagingredis/Receiver.java)와 같이 메시지에 응답하는 메서드를 사용하여 수신자를 구현합니다.

```java
package com.example.messagingredis;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    private AtomicInteger counter = new AtomicInteger();

    public void receiveMessage(String message) {
        LOGGER.info("Received <" + message + ">");
        counter.incrementAndGet();
    }

    public int getCount() {
        return counter.get();
    }
}
```

Receiver 메시지 수신 방법을 정의하는 POJO입니다. 를 메시지 리스너로 등록하면 Receiver 메시지 처리 메서드의 이름을 원하는 대로 지정할 수 있습니다.

- 데모 목적으로 수신자는 수신된 메시지를 세고 있습니다. 이렇게 하면 메시지를 받았을 때 신호를 보낼 수 있습니다.
 
### 리스너 등록 및 메시지 보내기
  Spring Data Redis는 Redis로 메시지를 보내고 받는 데 필요한 모든 구성 요소를 제공합니다. 특히 다음을 구성해야 합니다.

* 연결 공장

* 메시지 리스너 컨테이너

* Redis 템플릿

ReceiverRedis 템플릿을 사용하여 메시지를 보내고 메시지를 수신할 수 있도록 메시지 수신기 컨테이너에 를 등록합니다 . 연결 팩토리는 템플릿과 메시지 리스너 컨테이너를 모두 구동하여 Redis 서버에 연결할 수 있도록 합니다.

이 예제에서는 Jedis Redis 라이브러리를 기반으로 하는 RedisConnectionFactory인스턴스인 Spring Boot의 default 를 사용합니다 . 연결 팩토리는 다음 예제(에서 )와 같이 메시지 리스너 컨테이너와 Redis 템플릿 모두에 삽입됩니다.JedisConnectionFactorysrc/main/java/com/example/messagingredis/MessagingRedisApplication.java

```java
package com.example.messagingredis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@SpringBootApplication
public class MessagingRedisApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessagingRedisApplication.class);

	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("chat"));

		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	@Bean
	Receiver receiver() {
		return new Receiver();
	}

	@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}

	public static void main(String[] args) throws InterruptedException {

		ApplicationContext ctx = SpringApplication.run(MessagingRedisApplication.class, args);

		StringRedisTemplate template = ctx.getBean(StringRedisTemplate.class);
		Receiver receiver = ctx.getBean(Receiver.class);

		while (receiver.getCount() == 0) {

			LOGGER.info("Sending message...");
			template.convertAndSend("chat", "Hello from Redis!");
			Thread.sleep(500L);
		}

		System.exit(0);
	}
}
```

메소드 에 정의된 Bean은 listenerAdapter에 정의된 메시지 리스너 컨테이너에 메시지 리스너로 등록되고 주제 container에 대한 메시지를 수신합니다. 클래스는 POJO 이므로 Receiver 인터페이스를 구현하는 메시지 리스너 어댑터에 래핑해야 합니다 MessageListener( 에서 필요함 addMessageListener()). 메시지 수신기 어댑터는 메시지가 도착할 때 receiveMessage()메서드를 호출하도록 구성됩니다.

연결 팩토리와 메시지 리스너 컨테이너 빈만 있으면 메시지를 수신할 수 있습니다. 메시지를 보내려면 Redis 템플릿도 필요합니다. 여기서는 키와 값이 모두 인스턴스인 Redis의 일반적인 사용에 초점을 맞춘 StringRedisTemplate 구현인 으로 구성된 빈입니다.

이 main()메서드는 Spring 애플리케이션 컨텍스트를 생성하여 모든 것을 시작합니다. 그런 다음 애플리케이션 컨텍스트는 메시지 리스너 컨테이너를 시작하고 메시지 리스너 컨테이너 Bean은 메시지 수신을 시작합니다. 그런 다음 메서드 는 애플리케이션 컨텍스트에서 빈을 main()검색 하고 이를 사용하여 주제 에 대한 메시지를 보냅니다 . 마지막으로 Spring 애플리케이션 컨텍스트를 닫고 애플리케이션이 종료됩니다.

### Gradle을 사용하는 경우 
./gradlew bootRun ./gradlew build또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```gradle
java -jar build/libs/messaging-redis-0.0.1-SNAPSHOT.jar
```

다음과 유사한 출력이 표시되어야 합니다.
```
"C:\Users\HoHyeon Kim\.jdks\corretto-17.0.7\bin\java.exe" -XX:TieredStopAtLevel=1 -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true "-Dmanagement.endpoints.jmx.exposure.include=*" "-javaagent:C:\Users\HoHyeon Kim\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\231.9011.34\lib\idea_rt.jar=50885:C:\Users\HoHyeon Kim\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\231.9011.34\bin" -Dfile.encoding=UTF-8 -classpath "C:\Users\HoHyeon Kim\IdeaProjects\spring-guide\spring-study\messagingredis\build\classes\java\main;C:\Users\HoHyeon Kim\IdeaProjects\spring-guide\spring-study\messagingredis\build\resources\main;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-starter-data-redis\3.1.0\33a768aacf7326522831421b3a3eab2b4c6cdac2\spring-boot-starter-data-redis-3.1.0.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-starter\3.1.0\2960a1f899f4ee3eb815dc85986b0428c1a5289f\spring-boot-starter-3.1.0.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework.data\spring-data-redis\3.1.0\b06a2831edf101b7c0c585b78678672ad8f95385\spring-data-redis-3.1.0.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\io.lettuce\lettuce-core\6.2.4.RELEASE\81b026e4bc3ff16f591ce543fb95d22722e14de4\lettuce-core-6.2.4.RELEASE.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-autoconfigure\3.1.0\b06d1f0b08f6f8a2636e364c8941b2dabc4f0b77\spring-boot-autoconfigure-3.1.0.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot\3.1.0\efa941e9a2162a3dd8c5e4679f46a24af9e5769f\spring-boot-3.1.0.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework.boot\spring-boot-starter-logging\3.1.0\4784b6e2adfe32720a4e2c009a62650835bba391\spring-boot-starter-logging-3.1.0.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\jakarta.annotation\jakarta.annotation-api\2.1.1\48b9bda22b091b1f48b13af03fe36db3be6e1ae3\jakarta.annotation-api-2.1.1.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework\spring-core\6.0.9\284ed111fa0b49b29f6fea6ac0afa402b809e427\spring-core-6.0.9.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.yaml\snakeyaml\1.33\2cd0a87ff7df953f810c344bdf2fe3340b954c69\snakeyaml-1.33.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework.data\spring-data-keyvalue\3.1.0\11a757221dc383fca073861ebc7d486b7c9bff53\spring-data-keyvalue-3.1.0.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework\spring-tx\6.0.9\89818f4cc656107709d3db6b238ed9b776d3dbb4\spring-tx-6.0.9.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework\spring-oxm\6.0.9\f9ea1892fcb5a59ae0e1c2de6faca412ca8f1b9a\spring-oxm-6.0.9.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework\spring-context-support\6.0.9\e80a601e0f5a7f024d712e9506f0289133250592\spring-context-support-6.0.9.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework\spring-aop\6.0.9\8c1025bf9c1dc66f5268639866b5a45ed9bc62ef\spring-aop-6.0.9.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.slf4j\slf4j-api\2.0.7\41eb7184ea9d556f23e18b5cb99cad1f8581fc00\slf4j-api-2.0.7.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\io.netty\netty-handler\4.1.92.Final\d8e961d89a966c0cdea88105bbb2353408a41d12\netty-handler-4.1.92.Final.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\io.netty\netty-transport\4.1.92.Final\e0cf483b7c04af7207c02a6ff0c861592b09c97a\netty-transport-4.1.92.Final.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\io.netty\netty-common\4.1.92.Final\66133f9ad31816a227acf3030632903cb9e4c5a2\netty-common-4.1.92.Final.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\io.projectreactor\reactor-core\3.5.6\27fdc551537b349389176a23a192f11a7a3d7de\reactor-core-3.5.6.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework\spring-context\6.0.9\be88c57829b9ec038774b47c241ac45673352a55\spring-context-6.0.9.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\ch.qos.logback\logback-classic\1.4.7\307944865579a6d490e6a4cbb5082dc8f36536ca\logback-classic-1.4.7.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.apache.logging.log4j\log4j-to-slf4j\2.20.0\d37f81f8978e2672bc32c82712ab4b3f66624adc\log4j-to-slf4j-2.20.0.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.slf4j\jul-to-slf4j\2.0.7\a48f44aeaa8a5ddc347007298a28173ac1fbbd8b\jul-to-slf4j-2.0.7.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework\spring-jcl\6.0.9\88d9ddfc6bbbf4047c2a8de8de94a425b06f636a\spring-jcl-6.0.9.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework.data\spring-data-commons\3.1.0\d0af470d565f2de864faeeb45181d2305510df3a\spring-data-commons-3.1.0.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework\spring-beans\6.0.9\745619eee32c8ead88a21c97748d2416f1db8dd9\spring-beans-6.0.9.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\io.netty\netty-transport-native-unix-common\4.1.92.Final\22364cca56b752abb49fb7972f0f6299b8d6be98\netty-transport-native-unix-common-4.1.92.Final.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\io.netty\netty-codec\4.1.92.Final\9496b3d59290edcc6480cbe064f33560873f7484\netty-codec-4.1.92.Final.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\io.netty\netty-resolver\4.1.92.Final\fe96e210a4e139cb043d26702792b6098b93b13f\netty-resolver-4.1.92.Final.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\io.netty\netty-buffer\4.1.92.Final\5ff1ba7ec68c20e635fc0f2b792c38c951f688d6\netty-buffer-4.1.92.Final.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.reactivestreams\reactive-streams\1.0.4\3864a1320d97d7b045f729a326e1e077661f31b7\reactive-streams-1.0.4.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.springframework\spring-expression\6.0.9\f50a1df7ed038ee7ca85528aff652cef4ff4883b\spring-expression-6.0.9.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\ch.qos.logback\logback-core\1.4.7\a2948dae4013d0e9486141b4d638d8951becb767\logback-core-1.4.7.jar;C:\Users\HoHyeon Kim\.gradle\caches\modules-2\files-2.1\org.apache.logging.log4j\log4j-api\2.20.0\1fe6082e660daf07c689a89c94dc0f49c26b44bb\log4j-api-2.20.0.jar" com.example.messagingredis.MessagingRedisApplication

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.1.0)

2023-05-30T19:43:02.050+09:00  INFO 26308 --- [           main] c.e.m.MessagingRedisApplication          : Starting MessagingRedisApplication using Java 17.0.7 with PID 26308 (C:\Users\HoHyeon Kim\IdeaProjects\spring-guide\spring-study\messagingredis\build\classes\java\main started by HoHyeon Kim in C:\Users\HoHyeon Kim\IdeaProjects\spring-guide\spring-study)
2023-05-30T19:43:02.052+09:00  INFO 26308 --- [           main] c.e.m.MessagingRedisApplication          : No active profile set, falling back to 1 default profile: "default"
2023-05-30T19:43:02.296+09:00  INFO 26308 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
2023-05-30T19:43:02.297+09:00  INFO 26308 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
2023-05-30T19:43:02.317+09:00  INFO 26308 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 8 ms. Found 0 Redis repository interfaces.
2023-05-30T19:43:03.065+09:00  INFO 26308 --- [           main] c.e.m.MessagingRedisApplication          : Started MessagingRedisApplication in 1.285 seconds (process running for 2.086)
2023-05-30T19:43:03.068+09:00  INFO 26308 --- [           main] c.e.m.MessagingRedisApplication          : Sending message...
2023-05-30T19:43:03.079+09:00  INFO 26308 --- [    container-1] com.example.messagingredis.Receiver      : Received <Hello from Redis!>

Process finished with exit code 0
```
