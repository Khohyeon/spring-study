## 하이퍼미디어 기반 RESTful 웹 서비스 구축
이 가이드는 Spring을 사용하여 "Hello, World" 하이퍼미디어 기반 REST 웹 서비스를 생성하는 프로세스를 안내합니다.

하이퍼미디어는 REST의 중요한 측면입니다. 이를 통해 클라이언트와 서버를 크게 분리하고 독립적으로 발전할 수 있는 서비스를 구축할 수 있습니다. REST 리소스에 대해 반환된 표현에는 데이터뿐만 아니라 관련 리소스에 대한 링크도 포함됩니다. 따라서 표현의 디자인은 전체 서비스의 디자인에 매우 중요합니다.

### 무엇을 만들 것인가
Spring HATEOAS: Spring MVC 컨트롤러를 가리키는 링크를 생성하고, 리소스 표현을 구축하고, 지원되는 하이퍼미디어 형식(예: HAL ).

서비스는 에서 HTTP GET 요청을 수락합니다 http://localhost:8080/greeting.

리소스 자체를 가리키는 링크인 가장 간단한 하이퍼미디어 요소로 풍부한 인사말의 JSON 표현으로 응답합니다. 다음 목록은 출력을 보여줍니다.

```json
{
  "content":"Hello, World!",
  "_links":{
    "self":{
      "href":"http://localhost:8080/greeting?name=World"
    }
  }
}
```
name 응답은 이미 다음 목록과 같이 쿼리 문자열의 선택적 매개 변수를 사용하여 인사말을 사용자 지정할 수 있음을 나타냅니다 .

```
http://localhost:8080/greeting?name=User
```
매개 name 변수 값은 다음 목록과 같이 기본값 World를 재정의하고 응답에 반영됩니다.
```json
{
  "content":"Hello, User!",
  "_links":{
    "self":{
      "href":"http://localhost:8080/greeting?name=User"
    }
  }
}
```
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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-rest-hateoas.git
* cd 로gs-rest-hateoas/initial
* 리소스 표시 클래스 만들기 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-rest-hateoas/complete.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:
1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭 하고 Spring HATEOAS를 선택합니다 .

4. 생성 을 클릭합니다 .

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### JSON 라이브러리 추가
JSON을 사용하여 정보를 보내고 받기 때문에 JSON 라이브러리가 필요합니다. 이 가이드에서는 Jayway JsonPath 라이브러리를 사용합니다.
```
testCompile 'com.jayway.jsonpath:json-path'
```

다음 목록은 완료된 build.gradle파일을 보여줍니다.

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
	implementation 'org.springframework.boot:spring-boot-starter-hateoas'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
	useJUnitPlatform()
}
```
### Create a Resource Representation 
- 프로젝트 및 빌드 시스템을 설정했으므로 웹 서비스를 만들 수 있습니다.
- 서비스 상호 작용에 대해 생각하여 프로세스를 시작하십시오.
- 이 서비스는 GET 요청을 처리하기 위해 /greeting에 리소스를 노출하며 선택적으로 쿼리 문자열의 이름 매개변수를 사용합니다. GET 요청은 인사말을 나타내기 위해 본문에 JSON이 포함된 200 OK 응답을 반환해야 합니다.
- 그 외에도 리소스의 JSON 표현은 _links 속성의 하이퍼미디어 요소 목록으로 보강됩니다. 이것의 가장 기초적인 형태는 리소스 자체를 가리키는 링크입니다. 표현은 다음 목록과 유사해야 합니다.

```json
{
  "content":"Hello, World!",
  "_links":{
    "self":{
      "href":"http://localhost:8080/greeting?name=World"
    }
  }
}
```

- 내용은 인사말의 텍스트 표현입니다. _links 요소는 링크 목록을 포함합니다(이 경우 관계 유형이 rel이고 href 속성이 액세스된 리소스를 가리키는 정확히 하나임)
- 인사말 표현을 모델링하려면 자원 표현 클래스를 작성하십시오. _links 속성은 표현 모델의 기본 속성이므로 Spring HATEOAS는 Link 인스턴스를 추가하고 이전에 표시된 대로 렌더링되도록 하는 기본 클래스(RepresentationModel이라고 함)와 함께 제공됩니다.
- 다음 목록(src/main/java/com/example/resthateoas/Greeting.java에서)에 표시된 것처럼 RepresentationModel을 확장하고 컨텐트에 대한 필드와 접근자 및 생성자를 추가하는 일반 이전 Java 개체를 만듭니다.

```java
package com.example.resthateoas;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Greeting extends RepresentationModel<Greeting> {

	private final String content;

	@JsonCreator
	public Greeting(@JsonProperty("content") String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
```

- @JsonCreator: Jackson이 이 POJO의 인스턴스를 생성할 수 있는 방법을 알려줍니다.
- @JsonProperty: Jackson이 이 생성자 인수를 입력해야 하는 필드를 표시합니다.

```
이 가이드의 뒷부분에서 볼 수 있듯이 Spring은 Jackson JSON 라이브러리를 사용하여 Greeting 유형의 인스턴스를 JSON으로 자동 마샬링합니다.
마샬링(Marshalling)은 데이터를 일련의 바이트로 변환하는 프로세스입니다. 
```


다음으로 이러한 인사말을 제공할 리소스 컨트롤러를 만듭니다.

### REST 컨트롤러 생성
RESTful 웹 서비스 구축에 대한 Spring의 접근 방식에서 HTTP 요청은 컨트롤러에 의해 처리됩니다.
컴포넌트는 @Controller 및 @ResponseBody 주석을 결합한 @RestController 주석으로 식별됩니다.
다음 GreetingController(src/main/java/com/example/resthateoas/GreetingController.java에 있음)는 Greeting 클래스의 새 인스턴스를 반환하여 /greeting에 대한 GET 요청을 처리합니다.

```java
package com.example.resthateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class GreetingController {

	private static final String TEMPLATE = "Hello, %s!";

	@RequestMapping("/greeting")
	public HttpEntity<Greeting> greeting(
		@RequestParam(value = "name", defaultValue = "World") String name) {

		Greeting greeting = new Greeting(String.format(TEMPLATE, name));
		greeting.add(linkTo(methodOn(GreetingController.class).greeting(name)).withSelfRel());

		return new ResponseEntity<>(greeting, HttpStatus.OK);
	}
}
```
- 이 컨트롤러는 간결하고 단순하지만 많은 기능이 있습니다. 우리는 그것을 단계별로 분해합니다.
- @RequestMapping 주석은 /greeting에 대한 HTTP 요청이 greeting() 메서드에 매핑되도록 합니다.

```
위의 예는 @RequestMapping이 기본적으로 모든 HTTP 작업을 매핑하기 때문에 GET, PUT, POST 등을 지정하지 않습니다. 
이 매핑의 범위를 좁히려면 @GetMapping("/greeting")을 사용하십시오. 이 경우 org.springframework.web.bind.annotation.GetMapping;도 가져오려고 합니다.
```

- @RequestParam은 쿼리 문자열 매개변수 name의 값을 greeting() 메서드의 name 매개변수에 바인딩합니다.
이 쿼리 문자열 매개 변수는 defaultValue 특성을 사용하기 때문에 암시적으로 필요하지 않습니다.
요청에 없는 경우 World의 defaultValue가 사용됩니다.
- 클래스에 @RestController 주석이 있으므로 암시적 @ResponseBody 주석이 인사말 메서드에 추가됩니다.
이로 인해 Spring MVC는 반환된 HttpEntity와 해당 페이로드(Greeting)를 응답에 직접 렌더링합니다.
- 메서드 구현에서 가장 흥미로운 부분은 컨트롤러 메서드를 가리키는 링크를 만들고 이를 표현 모델에 추가하는 방법입니다. 
linkTo(…) 및 methodOn(…)은 컨트롤러에서 메서드 호출을 위조할 수 있는 ControllerLinkBuilder의 정적 메서드입니다. 
반환된 LinkBuilder는 메서드가 매핑된 URI를 정확하게 빌드하기 위해 컨트롤러 메서드의 매핑 주석을 검사했을 것입니다.


```

Spring HATEOAS는 다양한 X-FORWARDED- 헤더를 존중합니다. 프록시 뒤에 Spring HATEOAS 서비스를 배치하고 X-FORWARDED-HOST 헤더로 적절하게 구성하면 결과 링크가 적절하게 형식화됩니다
```

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/rest-hateoas-0.0.1-SNAPSHOT.jar
```

로깅 출력이 표시됩니다. 서비스가 몇 초 내에 시작되어 실행되어야 합니다.

#### 서비스 테스트
이제 서비스가 시작되었으므로 http://localhost:8080/greeting을 방문하면 다음 내용이 표시됩니다.

```json
{
  "content":"Hello, World!",
  "_links":{
    "self":{
      "href":"http://localhost:8080/greeting?name=World"
    }
  }
}
```
http://localhost:8080/greeting?name=User URL을 방문하여 이름 조회 문자열 매개변수를 제공하십시오. content 속성의 값이 Hello, World!에서 어떻게 변경되는지 확인하십시오. 안녕하세요, 사용자! 다음 목록과 같이 자체 링크의 href 속성에도 해당 변경 사항이 반영됩니다.
    
```json
{
"content":"Hello, User!",
"_links":{
    "self":{
    "href":"http://localhost:8080/greeting?name=User"
    }
}
}
```
이 변경은 GreetingController의 @RequestParam 배열이 예상대로 작동함을 보여줍니다. name 매개 변수에는 World의 기본값이 지정되었지만 항상 쿼리 문자열을 통해 명시적으로 재정의할 수 있습니다.