## RESTful 웹 서비스 사용
이 가이드는 RESTful 웹 서비스를 사용하는 애플리케이션을 만드는 과정을 안내합니다.

### 무엇을 만들 것인가
Spring을 사용하여 http://localhost:8080/api/randomRestTemplate 에서 임의의 Spring Boot 인용문을 검색하는 애플리케이션을 빌드합니다 .

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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-consuming-rest.git

* cd 로gs-consuming-rest/initial

* REST 리소스 가져오기 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-consuming-rest/complete.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭 하고 Spring Web 을 선택하십시오 .

4. 생성 을 클릭합니다 .

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### REST 리소스 가져오기
프로젝트 설정이 완료되면 RESTful 서비스를 사용하는 간단한 애플리케이션을 만들 수 있습니다.

그렇게 하기 전에 REST 자원의 소스가 필요합니다. https://github.com/spring-guides/quoters 에서 그러한 서비스의 예를 제공했습니다 . 별도의 터미널에서 해당 애플리케이션을 실행하고 http://localhost:8080/api/random 에서 결과에 액세스할 수 있습니다 . 해당 주소는 Spring Boot에 대한 인용문을 무작위로 가져와서 JSON 문서로 반환합니다. 다른 유효한 주소로는 http://localhost:8080/api/ (모든 인용문의 경우) 및 http://localhost:8080/api/1 (첫 번째 인용문의 경우), http://localhost:8080/api/2가 있습니다. (두 번째 인용의 경우) 등(현재 최대 10개).

웹 브라우저나 curl을 통해 해당 URL을 요청하면 다음과 같은 JSON 문서를 받게 됩니다.


```json
{
   type: "success",
   value: {
      id: 10,
      quote: "Really loving Spring Boot, makes stand alone Spring apps easy."
   }
}
```

REST 웹 서비스를 사용하는 더 유용한 방법은 프로그래밍 방식입니다. 이 작업을 돕기 위해 Spring은 이라는 편리한 템플릿 클래스를 제공합니다 RestTemplate. RestTemplate대부분의 RESTful 서비스와의 상호 작용을 한 줄 주문으로 만듭니다. 그리고 해당 데이터를 사용자 지정 도메인 유형에 바인딩할 수도 있습니다.

먼저 필요한 데이터를 포함할 도메인 클래스를 만들어야 합니다. 다음 목록은 Quote도메인 클래스로 사용할 수 있는 레코드 클래스를 보여줍니다.

src/main/java/com/example/consumingrest/Quote.java

```java
package com.example.consumingrest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Quote(
        String type,
        Value value
) { }
```
@JsonIgnoreProperties이 간단한 Java 레코드 클래스 에는 Jackson JSON 처리 라이브러리에서 주석이 추가되어 이 유형에 바인딩되지 않은 모든 속성을 무시해야 함을 나타냅니다.

데이터를 사용자 정의 유형에 직접 바인딩하려면 변수 이름이 API에서 반환된 JSON 문서의 키와 정확히 동일하도록 지정해야 합니다. JSON 문서의 변수 이름과 키가 일치하지 않는 경우 @JsonProperty주석을 사용하여 JSON 문서의 정확한 키를 지정할 수 있습니다. (이 예에서는 각 변수 이름을 JSON 키와 일치시키므로 여기에 해당 주석이 필요하지 않습니다.)

또한 내부 인용 자체를 포함하려면 추가 클래스가 필요합니다. 레코드 Value클래스는 이러한 요구 사항을 충족하며 다음 목록( src/main/java/com/example/consumingrest/Value.java)에 표시됩니다.

```java
package com.example.consumingrest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Value(
        Long id,
        String quote
) { }
```

### 신청 완료
Initalizr는 메서드를 사용하여 클래스를 만듭니다 main(). 다음 목록은 Initializr가 생성하는 클래스를 보여줍니다(에서 src/main/java/com/example/consumingrest/ConsumingRestApplication.java):

```java
package com.example.consumingrest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class ConsumingRestApplication {

	// 출력을 로그로 보내는 로거를 생성
	private static final Logger log = LoggerFactory.getLogger(ConsumingRestApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsumingRestApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			Quote quote = restTemplate.getForObject(
					"http://localhost:8080/api/random", Quote.class);
			log.info(quote.toString());
		};
	}
}

```

ConsumingRestApplication이제 RESTful 소스의 인용문을 표시하도록 클래스 에 몇 가지 다른 항목을 추가해야 합니다 . 다음을 추가해야 합니다.

* 출력을 로그(이 예에서는 콘솔)로 보내는 로거.

* A RestTemplate, 들어오는 데이터를 처리하기 위해 Jackson JSON 처리 라이브러리를 사용합니다.

* CommandLineRunner시작 시 를 실행 RestTemplate하고 결과적으로 견적을 가져오는 A.

### 애플리케이션 실행

#### Gradle 사용하는 경우

./gradlew bootRun. ./gradlew build또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .

```java
java -jar build/libs/consuming-rest-0.0.1-SNAPSHOT.jar
```