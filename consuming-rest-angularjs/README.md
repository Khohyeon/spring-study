## AngularJS로 RESTful 웹 서비스 사용
이 가이드는 Spring MVC 기반 RESTful 웹 서비스를 사용하는 간단한 AngularJS 클라이언트를 작성하는 과정을 안내합니다 .

### 무엇을 만들 것인가
Spring 기반 RESTful 웹 서비스를 사용하는 AngularJS 클라이언트를 빌드합니다. 특히 클라이언트는 CORS를 사용하여 RESTful 웹 서비스 빌드 에서 생성된 서비스를 사용합니다 .</a>
<a href ="https://spring.io/guides/gs/rest-service-cors/"> cors 가이드 바로가기 </a>

브라우저에서 파일을 열어 AngularJS 클라이언트에 액세스하고 밑의 위치에서 요청을 수락하는 서비스를 사용합니다.

```
http://rest-service.guides.spring.io/greeting
```

서비스는 인사말의 JSON 표현으로 응답합니다.
```json
{"id":1,"content":"Hello, World!"}
```

AngularJS 클라이언트는 ID와 콘텐츠를 DOM으로 렌더링합니다.

DOM은 Document Object Model(문서 객체 모델)의 약어로, 웹 페이지의 구조를 표현하는 객체 기반의 프로그래밍 인터페이스입니다. DOM은 HTML, XML 또는 XHTML 문서의 요소들을 계층적으로 구조화하여 표현하며, 이를 프로그래밍적으로 조작할 수 있도록 합니다.

```
rest-service.guides.spring.io의 서비스는 CORS 가이드의 코드를 약간 수정하여 실행하고 있습니다. 앱이 도메인 없이 @CrossOrigin을 사용하기 때문에 /greeting 엔드포인트에 대한 공개 액세스가 있습니다.
```

### 필요한 것
* 약 15분

* 좋아하는 텍스트 편집기

* 최신 웹 브라우저

* 인터넷 연결

### AngularJS 컨트롤러 만들기
먼저 REST 서비스를 사용할 AngularJS 컨트롤러 모듈을 만듭니다.

hello.js
```js
angular.module('demo', [])
.controller('Hello', function($scope, $http) {
    $http.get('http://rest-service.guides.spring.io/greeting').
        then(function(response) {
            $scope.greeting = response.data;
        });
});
```

이 컨트롤러 모듈은 AngularJS의 $scope 및 $http 구성 요소가 지정된 간단한 JavaScript 함수로 표현됩니다. $http 구성 요소를 사용하여 "/greeting"에서 REST 서비스를 사용합니다.

성공하면 서비스에서 반환된 JSON을 $scope.greeting에 할당하여 "greeting"이라는 모델 개체를 효과적으로 설정합니다. 해당 모델 개체를 설정함으로써 AngularJS는 이를 애플리케이션 페이지의 DOM에 바인딩하여 사용자가 볼 수 있도록 렌더링할 수 있습니다.

### 애플리케이션 페이지 만들기
이제 AngularJS 컨트롤러가 있으므로 컨트롤러를 사용자의 웹 브라우저에 로드할 HTML 페이지를 만듭니다.

index.html
```html
<!doctype html>
<html ng-app="demo">
	<head>
		<title>Hello AngularJS</title>
		<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.3/angular.min.js"></script>
    	<script src="hello.js"></script>
	</head>

	<body>
		<div ng-controller="Hello">
			<p>The ID is {{greeting.id}}</p>
			<p>The content is {{greeting.content}}</p>
		</div>
	</body>
</html>
```


head 섹션에 있는 다음 두 스크립트 태그에 유의하십시오.
```html
<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.3/angular.min.js"></script>
<script src="hello.js"></script>
```

첫 번째 스크립트 태그는 콘텐츠 전송 네트워크(CDN)에서 축소된 AngularJS 라이브러리(angular.min.js)를 로드하므로 AngularJS를 다운로드하여 프로젝트에 배치할 필요가 없습니다. 또한 애플리케이션의 경로에서 컨트롤러 코드(hello.js)를 로드합니다.

AngularJS 라이브러리는 표준 HTML 태그와 함께 사용할 여러 사용자 정의 속성을 활성화합니다. index.html에는 두 가지 속성이 있습니다.

- <html> 태그에는 이 페이지가 AngularJS 애플리케이션임을 나타내는 ng-app 속성이 있습니다.

- <div> 태그에는 컨트롤러 모듈인 Hello를 참조하도록 설정된 ng-controller 속성이 있습니다.

또한 자리 표시자를 사용하는 두 개의 <p> 태그(이중 중괄호로 식별됨)에 유의하십시오.
```
<p>The ID is {{greeting.id}}</p>
<p>The content is {{greeting.content}}</p>
```

자리 표시자는 REST 서비스를 성공적으로 사용할 때 설정될 인사말 모델 개체의 id 및 콘텐츠 속성을 참조합니다.

### 클라이언트 실행
클라이언트를 실행하려면 웹 서버에서 브라우저로 클라이언트를 제공해야 합니다. 
Spring Boot CLI(Command Line Interface)에는 웹 콘텐츠 제공에 대한 간단한 접근 방식을 제공하는 임베디드 Tomcat 서버가 포함되어 있습니다.
CLI 설치 및 사용에 대한 자세한 내용은 Spring Boot로 애플리케이션 빌드를 참조하세요.

Spring Boot의 임베디드 Tomcat 서버에서 정적 콘텐츠를 제공하려면 Spring Boot가 Tomcat을 시작할 수 있도록 최소한의 웹 애플리케이션 코드를 생성해야 합니다. 다음 app.groovy 스크립트는 Tomcat을 실행하려는 것을 Spring Boot에 알리기에 충분합니다.

app.groovy
```groovy
@Controller class JsApp { }
```
이제 Spring Boot CLI를 사용하여 앱을 실행할 수 있습니다.
```
spring run app.groovy
```
하지만 spring에서는 이제 run 명령을 사용하지 않습니다.

