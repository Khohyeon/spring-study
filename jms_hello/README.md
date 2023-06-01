## JMS로 메시징
이 가이드는 JMS 브로커를 사용하여 메시지를 게시하고 구독하는 과정을 안내합니다.

### 무엇을 만들 것인가
JmsTemplateSpring을 사용하여 단일 메시지를 게시하고 @JmsListener 관리 빈의 주석이 달린 메서드 로 구독하는 애플리케이션을 빌드합니다 .

### 필요한 것
* 약 15분
* 선호하는 텍스트 편집기 또는 IDE
* 자바 17 이상
* Gradle 7.5+ 또는 Maven 3.5+
* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.
처음부터 시작 하려면 [scratch] 로 이동합니다 .
기본 사항을 건너뛰 려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-messaging-jms.git
* cd 로gs-messaging-jms/initia
* Spring Initializr로 시작하기 로 이동하십시오 .
작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-messaging-jms/complete.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.
프로젝트를 수동으로 초기화하려면:
1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.
2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.
3. 종속성을 클릭 하고 Apache ActiveMQ 5용 Spring을 선택합니다 .
4. 생성 을 클릭합니다 .
5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 메시지 수신자 만들기
Spring은 모든 POJO(Plain Old Java Object)에 메시지를 게시하는 수단을 제공합니다.
이 안내서는 JMS 메시지 브로커를 통해 메시지를 보내는 방법을 설명합니다.
시작하려면 이메일 메시지의 세부 정보를 구현하는 간단한 POJO를 만듭니다. 
우리는 이메일 메시지를 보내지 않습니다. 메시지로 보낼 내용에 대한 세부 정보를 한 곳에서 다른 곳으로 보냅니다.
(src/main/java/hello/Email.java)

```java
package com.example.jms_hello;

public class Email {

  private String to;
  private String body;

  public Email() {
  }

  public Email(String to, String body) {
    this.to = to;
    this.body = body;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Override
  public String toString() {
    return String.format("Email{to=%s, body=%s}", getTo(), getBody());
  }

}
```

이 POJO 는 추정되는 getter 및 setter 세트와 함께 두 개의 필드( to 및 body )를 포함하는 매우 간단합니다.
여기에서 메시지 수신자를 정의할 수 있습니다.
(src/main/java/hello/Receiver.java)

```java
package com.example.jms_hello;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

	@JmsListener(destination = "mailbox", containerFactory = "myFactory")
	public void receiveMessage(Email email) {
		System.out.println("Received <" + email + ">");
	}

}
```

### Spring으로 JMS 메시지 송수신
다음으로 발신자와 수신자를 연결합니다.

(src/main/java/hello/Application.java)
```java
package com.example.jms_hello;

import jakarta.jms.ConnectionFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@SpringBootApplication
@EnableJms
public class JmsHelloApplication {

    @Bean
    public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        // You could still override some of Boot's default if necessary.
        return factory;
    }

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    public static void main(String[] args) {
        // Launch the application
        ConfigurableApplicationContext context = SpringApplication.run(JmsHelloApplication.class, args);

        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

        // Send a message with a POJO - the template reuse the message converter
        System.out.println("Sending an email message.");
        jmsTemplate.convertAndSend("mailbox", new Email("info@example.com", "Hello"));
    }

}
```

@SpringBootApplication다음을 모두 추가하는 편의 주석입니다.

* @Configuration: 애플리케이션 컨텍스트에 대한 빈 정의의 소스로 클래스에 태그를 지정합니다.
* @EnableAutoConfiguration: 클래스 경로 설정, 기타 빈 및 다양한 속성 설정을 기반으로 빈 추가를 시작하도록 Spring Boot에 지시합니다. 
예를 들어 spring-webmvc가 클래스 경로에 있는 경우 이 주석은 애플리케이션을 웹 애플리케이션으로 플래그 지정하고 DispatcherServlet 설정과 같은 주요 동작을 활성화합니다.
* @ComponentScanhello: 컨트롤러를 찾을 수 있도록 패키지 에서 다른 구성 요소, 구성 및 서비스를 찾도록 Spring에 지시합니다 .

@EnableJms는 @JmsListener로 주석이 달린 메서드의 검색을 트리거하여 커버 아래에 메시지 수신기 컨테이너를 만듭니다.


명확성을 위해 수신기의 JmsListener 주석에서 참조되는 myFactory 빈도 정의했습니다. 
Spring Boot에서 제공하는 DefaultJmsListenerContainerFactoryConfigurer 인프라를 사용하기 때문에 해당 JmsMessageListenerContainer는 Boot에서 기본적으로 생성하는 것과 동일합니다.
기본 MessageConverter는 기본 유형(예: 문자열, 맵, 직렬화 가능)만 변환할 수 있으며 이메일은 일부러 직렬화할 수 없습니다. 
우리는 Jackson을 사용하고 콘텐츠를 텍스트 형식(즉, TextMessage)의 JSON으로 직렬화하려고 합니다. 
Spring Boot는 MessageConverter의 존재를 감지하고 이를 기본 JmsTemplate과 DefaultJmsListenerContainerFactoryConfigurer에 의해 생성된 모든 JmsListenerContainerFactory에 연결합니다.

JmsTemplate은 메시지를 JMS 목적지로 간단하게 보낼 수 있게 해줍니다. 기본 실행기 방법에서 작업을 시작한 후 jmsTemplate을 사용하여 이메일 POJO를 보낼 수 있습니다. 사용자 지정 MessageConverter가 자동으로 연결되었기 때문에 JSON 문서는 TextMessage에서만 생성됩니다.

정의된 것으로 보이지 않는 두 개의 빈은 JmsTemplate과 ConnectionFactory입니다. 이들은 Spring Boot에 의해 자동으로 생성됩니다. 이 경우 ActiveMQ 브로커는 임베디드로 실행됩니다.

기본적으로 Spring Boot는 pubSubDomain을 false로 설정하여 대기열로 전송하도록 구성된 JmsTemplate을 생성합니다. 
JmsMessageListenerContainer도 같은 방식으로 구성됩니다. 
재정의하려면 Boot의 속성 설정을 통해 spring.jms.isPubSubDomain=true를 설정합니다(application.properties 내에서 또는 환경 변수 설정을 통해). 
그런 다음 수신 컨테이너의 설정이 동일한지 확인하십시오.

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/jms_hello-0.1.0.jar
```
실행되면 모든 로깅에 묻혀 다음 메시지가 표시됩니다.
```
Sending an email message.
Received <Email{to=info@example.com, body=Hello}>
```