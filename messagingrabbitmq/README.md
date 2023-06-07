## RabbitMQ를 사용한 메시징
이 가이드는 메시지를 게시하고 구독하는 RabbitMQ AMQP 서버를 설정하고 해당 RabbitMQ 서버와 상호 작용할 Spring Boot 애플리케이션을 만드는 과정을 안내합니다.

### 무엇을 만들 것인가
Spring AMQP의 RabbitTemplate을 사용하여 메시지를 게시하고 MessageListenerAdapter를 사용하여 POJO에서 메시지를 구독하는 애플리케이션을 빌드합니다.

### 필요한 것
* 약 15분

* 선호하는 텍스트 편집기 또는 IDE

* 자바 17 이상

* Gradle 7.5+ 또는 Maven 3.5+

* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작 하려면 RabbitMQ 브로커 설정 으로 이동하십시오 .

기본 사항을 건너뛰 려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-messaging-rabbitmq.git

* cd 로gs-messaging-rabbitmq/initial

* Spring Initializr로 시작하기 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-messaging-rabbitmq/complete.

### RabbitMQ 브로커 설정
메시징 응용 프로그램을 구축하기 전에 메시지 수신 및 전송을 처리할 서버를 설정해야 합니다.

RabbitMQ는 AMQP 서버입니다. 서버는 https://www.rabbitmq.com/download.html 에서 무료로 사용할 수 있습니다 . 수동으로 다운로드하거나 Homebrew와 함께 Mac을 사용하는 경우 터미널 창에서 다음 명령을 실행하여 다운로드할 수 있습니다.

```shell
brew install rabbitmq
```

터미널 창에서 다음 명령을 실행하여 서버의 압축을 풀고 기본 설정으로 시작합니다.

```shell
rabbitmq-server
```

다음과 유사한 출력이 표시되어야 합니다.
```
            RabbitMQ 3.1.3. Copyright (C) 2007-2013 VMware, Inc.
##  ##      Licensed under the MPL.  See https://www.rabbitmq.com/
##  ##
##########  Logs: /usr/local/var/log/rabbitmq/rabbit@localhost.log
######  ##        /usr/local/var/log/rabbitmq/rabbit@localhost-sasl.log
##########
            Starting broker... completed with 6 plugins.
```

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭 하고 Spring for RabbitMQ 를 선택하십시오 .

4. 생성 을 클릭합니다 .

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### RabbitMQ 메시지 수신자 만들기
메시징 기반 애플리케이션을 사용하면 게시된 메시지에 응답하는 수신기를 만들어야 합니다. 다음 목록(에서 src/main/java/com.example.messagingrabbitmq/Receiver.java)은 이를 수행하는 방법을 보여줍니다.

```java
package com.example.messagingrabbitmq;

import java.util.concurrent.CountDownLatch;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

  private CountDownLatch latch = new CountDownLatch(1);

  public void receiveMessage(String message) {
    System.out.println("Received <" + message + ">");
    latch.countDown();
  }

  public CountDownLatch getLatch() {
    return latch;
  }

}
```

Receiver 메시지 수신 방법을 정의하는 POJO입니다 . 메시지를 수신하도록 등록할 때 원하는 이름을 지정할 수 있습니다.

### 리스너 등록 및 메시지 보내기
Spring AMQP는 RabbitTemplateRabbitMQ로 메시지를 보내고 받는 데 필요한 모든 것을 제공합니다. 그러나 다음을 수행해야 합니다.

* 메시지 수신기 컨테이너를 구성합니다.
* 큐, 교환 및 이들 사이의 바인딩을 선언하십시오.
* 수신기를 테스트하기 위해 일부 메시지를 보내도록 구성 요소를 구성합니다.

```
Spring Boot는 연결 팩토리와 RabbitTemplate을 자동으로 생성하여 작성해야 하는 코드의 양을 줄입니다.
```

메시지를 보내는 데 사용 하고 메시지 수신을 위해 메시지 수신기 컨테이너에 RabbitTemplate를 등록합니다 . Receiver연결 팩토리는 둘 다 구동하여 RabbitMQ 서버에 연결할 수 있습니다. 다음 목록(에서 src/main/java/com.example.messagingrabbitmq/MessagingRabbitApplication.java)은 애플리케이션 클래스를 만드는 방법을 보여줍니다.

```java
package com.example.messagingrabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MessagingRabbitmqApplication {

  static final String topicExchangeName = "spring-boot-exchange";

  static final String queueName = "spring-boot";

  @Bean
  Queue queue() {
    return new Queue(queueName, false);
  }

  @Bean
  TopicExchange exchange() {
    return new TopicExchange(topicExchangeName);
  }

  @Bean
  Binding binding(Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
  }

  @Bean
  SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
      MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(queueName);
    container.setMessageListener(listenerAdapter);
    return container;
  }

  @Bean
  MessageListenerAdapter listenerAdapter(Receiver receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  public static void main(String[] args) throws InterruptedException {
    SpringApplication.run(MessagingRabbitmqApplication.class, args).close();
  }

}
```
