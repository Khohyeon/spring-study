## Spring MVC로 웹 콘텐츠 제공
이 가이드는 Spring으로 "Hello, World" 웹 사이트를 만드는 과정을 안내합니다.

### 무엇을 만들 것인가
정적 홈 페이지가 있고 http://localhost:8080/greeting에서 HTTP GET 요청도 수락하는 애플리케이션을 빌드합니다.

HTML을 표시하는 웹 페이지로 응답합니다. HTML 본문에는 "Hello, World!"라는 인사말이 포함됩니다.

쿼리 문자열에서 선택적 이름 매개 변수를 사용하여 인사말을 사용자 지정할 수 있습니다. URL은 http://localhost:8080/greeting?name=User일 수 있습니다.

name 매개 변수 값은 World의 기본값을 재정의하고 "Hello, User!"로 변경되는 콘텐츠에 의해 응답에 반영됩니다.

### 필요한 것
* 약 15분
* 선호하는 텍스트 편집기 또는 IDE
* 자바 17 이상
* Gradle 7.5+ 또는 Maven 3.5+
* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작하려면 Spring Initializr로 시작하기로 이동하세요.

기본 사항을 건너뛰려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다. git clone https://github.com/spring-guides/gs-serving-web-content.git

* gs-serving-web-content/initial로 cd

* 웹 컨트롤러 만들기로 이동합니다.

완료하면 gs-serving-web-content/complete의 코드와 비교하여 결과를 확인할 수 있습니다.

### 
스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io로 이동합니다. 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭하고 Spring Web, Thymeleaf 및 Spring Boot DevTools를 선택합니다.

4. 생성을 클릭합니다.

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 웹 컨트롤러 만들기
- 웹 사이트 구축에 대한 Spring의 접근 방식에서 HTTP 요청은 컨트롤러에 의해 처리됩니다. 
- @Controller 주석으로 컨트롤러를 쉽게 식별할 수 있습니다. 다음 예제에서 GreetingController는 보기의 이름(이 경우 인사말)을 반환하여 /greeting에 대한 GET 요청을 처리합니다. 보기는 HTML 콘텐츠 렌더링을 담당합니다. 
다음 목록(src/main/java/com/example/servingwebcontent/GreetingController.java)은 컨트롤러를 보여줍니다.
```java
@Controller
public class GreetingController {

	@GetMapping("/greeting")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}

}
```
이 컨트롤러는 간결하고 단순하지만 많은 기능이 있습니다. 우리는 그것을 단계별로 분해합니다.

@GetMapping 주석은 /greeting에 대한 HTTP GET 요청이 greeting() 메서드에 매핑되도록 합니다.

@RequestParam은 쿼리 문자열 매개변수 name의 값을 greeting() 메서드의 name 매개변수에 바인딩합니다. 
이 쿼리 문자열 매개변수는 필수가 아닙니다. 요청에 없는 경우 World의 defaultValue가 사용됩니다. 
name 매개 변수의 값은 모델 개체에 추가되어 궁극적으로 보기 템플릿에 액세스할 수 있습니다.

메서드 본문의 구현은 HTML의 서버측 렌더링을 수행하기 위해 보기 기술(이 경우 Thymeleaf)에 의존합니다. 
Thymeleaf는 greeting.html 템플릿을 구문 분석하고 th:text 표현식을 평가하여 컨트롤러에 설정된 ${name} 매개변수의 값을 렌더링합니다. 
다음 목록(src/main/resources/templates/greeting.html에서)은 greeting.html 템플릿:
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head> 
    <title>Getting Started: Serving Web Content</title> 
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
    <p th:text="'Hello, ' + ${name} + '!'" />
</body>
</html>
```
클래스 경로에 Thymeleaf가 있는지 확인하십시오(아티팩트 좌표: org.springframework.boot:spring-boot-starter-thymeleaf). Github의 "초기" 및 "완료" 샘플에 이미 있습니다.

### 스프링 부트 개발 도구
웹 애플리케이션 개발의 일반적인 기능은 변경 사항을 코딩하고, 애플리케이션을 다시 시작하고, 변경 사항을 보기 위해 브라우저를 새로 고치는 것입니다. 이 전체 프로세스는 많은 시간을 소비할 수 있습니다. 
이 갱신 주기를 가속화하기 위해 Spring Boot는 spring-boot-devtools 라는 편리한 모듈을 제공합니다. <br>
스프링 부트 개발 도구:
* 핫 스와핑을 활성화합니다.

* 캐싱을 비활성화하도록 템플릿 엔진을 전환합니다.

* LiveReload를 활성화하여 브라우저를 자동으로 새로고침합니다.

* 프로덕션 대신 개발을 기반으로 하는 기타 합리적인 기본값입니다.

### 애플리케이션 실행
Spring Initializr는 당신을 위한 애플리케이션 클래스를 생성합니다.
이 경우 Spring Initializr에서 제공하는 클래스를 더 이상 수정할 필요가 없습니다. 
다음 목록(src/main/java/com/example/servingwebcontent/ServingWebContentApplication.java)은 애플리케이션 클래스를 보여줍니다.

```java
@SpringBootApplication
public class ServingWebContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServingWebContentApplication.class, args);
    }

}
```
#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/serving-web-content-0.0.1-SNAPSHOT.jar
```
### 애플리케이션 테스트
이제 웹 사이트가 실행 중이므로 http://localhost:8080/greeting을 방문하면 "Hello, World!"가 표시됩니다.

http://localhost:8080/greeting?name=User를 방문하여 이름 조회 문자열 매개변수를 제공하십시오. 메시지가 "Hello, World!"에서 어떻게 변경되는지 확인합니다. "안녕하세요, 사용자!"로:

이 변경은 GreetingController의 @RequestParam 배열이 예상대로 작동함을 보여줍니다. name 매개 변수에는 World라는 기본값이 지정되었지만 쿼리 문자열을 통해 명시적으로 재정의할 수 있습니다.

홈페이지 추가
HTML, JavaScript 및 CSS를 포함한 정적 리소스는 소스 코드의 올바른 위치에 드롭하여 Spring Boot 애플리케이션에서 제공할 수 있습니다.
기본적으로 Spring Boot는 /static(또는 /public)에 있는 클래스 경로의 리소스에서 정적 콘텐츠를 제공합니다. 
index.html 리소스는 존재하는 경우 "`환영 페이지"serving-web-content/로 사용되기 때문에 특별합니다. 즉, 루트 리소스(즉, `http:// 로컬호스트:8080/). 따라서 다음 파일을 만들어야 합니다
(src/main/resources/static/index.html)
```html
<!DOCTYPE HTML>
<html>
<head> 
    <title>Getting Started: Serving Web Content</title> 
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
    <p>Get your greeting <a href="/greeting">here</a></p>
</body>
</html>
```
이제 http://localhost:8080에 방문하면 index.html이 표시됩니다.
