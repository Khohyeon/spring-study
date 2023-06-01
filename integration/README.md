## 데이터 통합
이 가이드는 Spring 통합을 사용하여 RSS Feed(Spring 블로그)에서 데이터를 검색하고 데이터를 조작한 다음 파일에 쓰는 간단한 애플리케이션을 만드는 과정을 안내합니다. 이 가이드는 전통적인 Spring Integration XML 구성을 사용합니다. 다른 가이드는 Lambda 표현식을 사용하거나 사용하지 않고 Java 구성 및 DSL을 사용하는 방법을 보여줍니다. 

## 무엇을 만들 것인가
기존 XML 구성을 사용하여 Spring 통합으로 흐름을 생성합니다. 

### 필요한 것
* 약 15분
* 선호하는 텍스트 편집기 또는 IDE
* 자바 17 이상
* Gradle 7.5+ 또는 Maven 3.5+
* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작 하려면 Spring Initializr로 시작하기 로 이동하십시오 .

기본 사항을 건너뛰 려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-integration.git

* cd 로gs-integration/initial

* 통합 흐름 정의 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-integration/complete.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭 하고 Spring 통합을 선택합니다 .

4. 생성 을 클릭합니다 .

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 빌드 파일에 추가
이 예에서는 두 가지 종속성을 추가해야 합니다.

- spring-integration-feed

- spring-integration-file

### 완성된 build.gradle
```
plugins {
	id 'org.springframework.boot' version '3.1.0'
	id 'io.spring.dependency-management' version '1.1.0'
	id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-integration'
	implementation 'org.springframework.integration:spring-integration-feed'
	implementation 'org.springframework.integration:spring-integration-file'
	testImplementation('org.springframework.boot:spring-boot-starter-test')
	testImplementation 'org.springframework.integration:spring-integration-test'
}

test {
	useJUnitPlatform()
}
```

### 통합 흐름 정의

이 가이드의 샘플 애플리케이션에서는 다음과 같은 Spring 통합 흐름을 정의합니다.

* spring.io의 RSS 피드에서 블로그 게시물을 읽습니다.

* 게시물 제목과 게시물의 URL로 구성된 쉽게 읽을 수 있는 문자열로 변환합니다.

* 해당 문자열을 파일 끝에 추가합니다(/tmp/si/SpringBlog).

통합 흐름을 정의하기 위해 Spring Integration의 XML 네임스페이스에서 소수의 요소로 Spring XML 구성을 생성할 수 있습니다. 특히 원하는 통합 흐름을 위해 이러한 Spring 통합 네임스페이스(코어, 피드 및 파일)의 요소를 사용하여 작업합니다. (마지막 두 개를 얻기 위해 Spring Initializr에서 제공하는 빌드 파일을 수정해야 했습니다.)

다음 XML 구성 파일(src/main/resources/integration/integration.xml에 있음)은 통합 흐름을 정의합니다.

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:int="http://www.springframework.org/schema/integration"
	xmlns:file="http://www.springframework.org/schema/integration/file"
	xmlns:feed="http://www.springframework.org/schema/integration/feed"
	xsi:schemaLocation="http://www.springframework.org/schema/integration/feed https://www.springframework.org/schema/integration/feed/spring-integration-feed.xsd
		http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/integration/file https://www.springframework.org/schema/integration/file/spring-integration-file.xsd
		http://www.springframework.org/schema/integration https://www.springframework.org/schema/integration/spring-integration.xsd">

    <feed:inbound-channel-adapter id="news" url="https://spring.io/blog.atom" auto-startup="${auto.startup:true}">
        <int:poller fixed-rate="5000"/>
    </feed:inbound-channel-adapter>

    <int:transformer
            input-channel="news"
            expression="payload.title + ' @ ' + payload.link + '#{systemProperties['line.separator']}'"
            output-channel="file"/>

    <file:outbound-channel-adapter id="file"
            mode="APPEND"
            charset="UTF-8"
            directory="/tmp/si"
            filename-generator-expression="'${feed.file.name:SpringBlog}'"/>

</beans>
```
``
### 세 가지 통합 요소가 사용됩니다.
- <feed:inbound-channel-adapter>: 설문당 하나씩 게시물을 검색하는 인바운드 어댑터입니다. 여기에 구성된 대로 5초마다 폴링합니다. 게시물은 뉴스(어댑터의 ID에 해당)라는 채널에 배치됩니다.

- <int:transformer>: 뉴스 채널의 항목(com.rometools.rome.feed.synd.SyndEntry)을 변환하여 항목의 제목(payload.title)과 링크(payload.link)를 추출하고 읽을 수 있는 문자열( 줄 바꿈 추가). 그런 다음 String은 file이라는 출력 채널로 전송됩니다.

- <file:outbound-channel-adapter>: 해당 채널(이름이 지정된 파일)의 콘텐츠를 파일에 쓰는 아웃바운드 채널 어댑터입니다. 특히 여기에 구성된 대로 파일 채널의 모든 항목을 /tmp/si/SpringBlog의 파일에 추가합니다.


다음 이미지는 이 간단한 흐름을 보여줍니다.
![blogToFile.png](..%2F..%2F..%2Fgs-integration%2Fimages%2FblogToFile.png)

지금은 자동 시작 속성을 무시하십시오. 나중에 테스트에 대해 논의할 때 다시 살펴보겠습니다.
지금은 기본적으로 true이며 애플리케이션이 시작될 때 게시물을 가져오는 것을 의미합니다.
또한 filename-generator-expression의 속성 자리 표시자에 유의하십시오.
기본값은 SpringBlog이지만 속성으로 재정의할 수 있음을 의미합니다.

응용 프로그램을 실행 가능하게 만들기
더 큰 애플리케이션(아마도 웹 애플리케이션) 내에서 Spring Integration 흐름을 구성하는 것이 일반적이지만 더 간단한 독립형 애플리케이션에서 정의할 수 없는 이유는 없습니다. 
이것이 다음에 수행할 작업입니다. 통합 흐름을 시작하고 통합 흐름을 지원하기 위해 소수의 빈을 선언하는 기본 클래스를 만듭니다. 또한 애플리케이션을 독립형 실행 JAR 파일로 빌드합니다. 
Spring Boot의 @SpringBootApplication 주석을 사용하여 애플리케이션 컨텍스트를 생성합니다. 이 가이드는 통합 흐름에 XML 네임스페이스를 사용하므로 애플리케이션 컨텍스트에 로드하려면 @ImportResource 주석을 사용해야 합니다. 
다음 목록(src/main/java/com/example/integration/IntegrationApplication.java에서)은 애플리케이션 파일을 보여줍니다.

```java
package com.example.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("/integration/integration.xml")
public class IntegrationApplication {
  public static void main(String[] args) throws Exception {
    ConfigurableApplicationContext ctx = new SpringApplication(IntegrationApplication.class).run(args);
    System.out.println("Hit Enter to terminate");
    System.in.read();
    ctx.close();
  }

}
```

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/integration-0.0.1-SNAPSHOT.jar
```

### 애플리케이션 실행
이제 다음 명령을 실행하여 jar에서 애플리케이션을 실행할 수 있습니다.
```
java -jar build/libs/{project_id}-0.1.0.jar

... app starts up ...
```

애플리케이션이 시작되면 RSS 피드에 연결하고 블로그 게시물 가져오기를 시작합니다. 애플리케이션은 사용자가 정의한 통합 흐름을 통해 해당 게시물을 처리하고 궁극적으로 게시물 정보를 /tmp/si/SpringBlog의 파일에 추가합니다.

애플리케이션이 한동안 실행된 후 /tmp/si/SpringBlog에서 파일을 보고 소수의 게시물에서 데이터를 볼 수 있어야 합니다. UNIX 기반 운영 체제에서는 다음 명령을 실행하여 파일을 추적하여 작성된 결과를 확인할 수도 있습니다.

```
tail -f /tmp/si/SpringBlog
```

다음 샘플 출력과 같은 내용이 표시되어야 합니다(실제 출력값과 다를 수 있음).
```shell
Spring Integration Java DSL 1.0 GA Released @ https://spring.io/blog/2014/11/24/spring-integration-java-dsl-1-0-ga-released
This Week in Spring - November 25th, 2014 @ https://spring.io/blog/2014/11/25/this-week-in-spring-november-25th-2014
Spring Integration Java DSL: Line by line tutorial @ https://spring.io/blog/2014/11/25/spring-integration-java-dsl-line-by-line-tutorial
Spring for Apache Hadoop 2.1.0.M2 Released @ https://spring.io/blog/2014/11/14/spring-for-apache-hadoop-2-1-0-m2-released
```

### 테스트
전체 프로젝트를 검사하면 src/test/java/com/example/integration/FlowTests.java에서 테스트 사례를 볼 수 있습니다.

```java
package com.example.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import com.rometools.rome.feed.synd.SyndEntryImpl;

@SpringBootTest({ "auto.startup=false",   // we don't want to start the real feed
          "feed.file.name=Test" })   // use a different file
public class FlowTests {

  @Autowired
  private SourcePollingChannelAdapter newsAdapter;

  @Autowired
  private MessageChannel news;

  @Test
  public void test() throws Exception {
    assertThat(this.newsAdapter.isRunning()).isFalse();
    SyndEntryImpl syndEntry = new SyndEntryImpl();
    syndEntry.setTitle("Test Title");
    syndEntry.setLink("http://characters/frodo");
    File out = new File("/tmp/si/Test");
    out.delete();
    assertThat(out.exists()).isFalse();
    this.news.send(MessageBuilder.withPayload(syndEntry).build());
    assertThat(out.exists()).isTrue();
    BufferedReader br = new BufferedReader(new FileReader(out));
    String line = br.readLine();
    assertThat(line).isEqualTo("Test Title @ http://characters/frodo");
    br.close();
    out.delete();
  }

}
```


