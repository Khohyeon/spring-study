## jQuery로 RESTful 웹 서비스 사용
이 가이드는 Spring MVC 기반 RESTful 웹 서비스를 사용하는 간단한 jQuery 클라이언트를 작성하는 과정을 안내합니다.

### 무엇을 만들 것인가
Spring 기반 RESTful 웹 서비스를 사용하는 jQuery 클라이언트를 빌드합니다. 특히 클라이언트는 CORS로 RESTful 웹 서비스 구축에서 생성된 서비스를 사용합니다.

jQuery 클라이언트는 브라우저에서 index.html 파일을 열어 액세스하고 다음 위치에서 요청을 수락하는 서비스를 사용합니다.

```
http://rest-service.guides.spring.io/greeting
```

서비스는 인사말의 JSON 표현으로 응답합니다.
```json
{"id":1,"content":"Hello!"}
```
jQuery 클라이언트는 ID와 콘텐츠를 DOM으로 렌더링합니다.

### 필요한 것
* 약 15분

* 좋아하는 텍스트 편집기

* 최신 웹 브라우저

* 인터넷 연결

### jQuery 컨트롤러 만들기
먼저 REST 서비스를 사용할 jQuery 컨트롤러 모듈을 만듭니다.

hello.js
```js
$(document).ready(function() {
    $.ajax({
        url: "http://rest-service.guides.spring.io/greeting"
    }).then(function(data) {
       $('.greeting-id').append(data.id);
       $('.greeting-content').append(data.content);
    });
});
```

이 컨트롤러 모듈은 간단한 JavaScript 함수로 표현됩니다. jQuery의 $.ajax() 메서드를 사용하여 http://rest-service.guides.spring.io/greeting에서 REST 서비스를 사용합니다. 성공하면 받은 JSON을 데이터에 할당하여 인사말 모델 개체로 만듭니다. 그러면 id와 콘텐츠가 각각 greeting-id와 greeting-content DOM 요소에 추가됩니다.

jQuery 약속 .then()의 사용에 유의하십시오. 이렇게 하면 $.ajax() 메서드가 완료되면 jQuery가 익명 함수를 실행하여 완료된 AJAX 요청의 데이터 결과를 전달합니다.

### 애플리케이션 페이지 만들기
이제 jQuery 컨트롤러가 있으므로 클라이언트를 사용자의 웹 브라우저에 로드할 HTML 페이지를 만듭니다.

index.html
```html
<!DOCTYPE html>
<html>
    <head>
        <title>Hello jQuery</title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
        <script src="hello.js"></script>
    </head>

    <body>
        <div>
            <p class="greeting-id">The ID is </p>
            <p class="greeting-content">The content is </p>
        </div>
    </body>
</html>
```

<head> 섹션 내의 다음 두 스크립트 태그에 유의하십시오.
```
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="hello.js"></script>
```

첫 번째 스크립트 태그는 콘텐츠 전송 네트워크(CDN)에서 축소된 jQuery 라이브러리(jquery.min.js)를 로드하므로 jQuery를 다운로드하여 프로젝트에 배치할 필요가 없습니다. 또한 애플리케이션의 경로에서 컨트롤러 코드(hello.js)를 로드합니다.

또한 <p> 태그에는 클래스 속성이 포함되어 있습니다.

```
<p class="greeting-id">The ID is </p>
<p class="greeting-content">The content is </p>
```

이러한 클래스 특성은 jQuery가 HTML 요소를 참조하고 REST 서비스에서 받은 JSON의 id 및 콘텐츠 속성 값으로 텍스트를 업데이트하는 데 도움이 됩니다.

### 클라이언트 실행
클라이언트를 실행하려면 웹 서버에서 브라우저로 클라이언트를 제공해야 합니다.
Spring Boot CLI(Command Line Interface)에는 웹 콘텐츠 제공에 대한 간단한 접근 방식을 제공하는 임베디드 Tomcat 서버가 포함되어 있습니다.
CLI 설치 및 사용에 대한 자세한 내용은 Spring Boot로 애플리케이션 빌드를 참조하세요.

app.groovy
```groovy
@Controller class JsApp { }
```
이제 Spring Boot CLI를 사용하여 앱을 실행할 수 있습니다.
```
spring run app.groovy
```
하지만 spring에서는 이제 run 명령을 사용하지 않습니다.

