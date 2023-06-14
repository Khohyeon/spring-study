# spring-study 

1. [Rest](restservice/README.md)<br>
- 이 가이드는 Spring으로 "Hello, World" RESTful 웹 서비스를 만드는 과정을 안내합니다.<br>
- @RestController의 Get요청을 통해 인사말을 반환하는 RESTful 웹 서비스를 만드는 가이드 입니다.

2. [Scheduling](schedulingtasks/README.md)<br>
- 이 가이드는 Spring을 사용하여 작업을 예약하는 단계를 안내합니다.<br>
- Spring의 주석을 이용하여 원하는 시간마다 현재 시간을 출력하는 예제를 만드는 가이드 입니다.

3. [Restful](consumingrest/README.md)<br>
- 이 가이드는 RESTful 웹 서비스를 사용하는 애플리케이션을 만드는 과정을 안내합니다.<br>
- Spring의 RestTemplate을 사용하여 http://localhost:8080/api/random 에서 임의의 Spring Boot 인용문을 검색하는 예제를 만드는 가이드 입니다.
- Restful 이란? <br>
-> 웹 서비스 아키텍처의 설계 원칙 중 하나로, 자원을 고유한 URI로 표현하고 HTTP 메서드를 사용하여 자원에 대한 작업을 수행하는 것을 의미합니다.

4. [Gradle](hello/README.md)<br>
- 이 가이드는 Gradle을 사용하여 Spring Boot 애플리케이션을 빌드하는 과정을 안내합니다.<br>
- Spring Initializr를 사용하여 Gradle을 사용하여 Spring Boot 애플리케이션을 빌드하는 예제를 만드는 가이드 입니다.

5. [Maven](hello_maven/README.md)<br>
- 이 가이드는 Maven을 사용하여 Spring Boot 애플리케이션을 빌드하는 과정을 안내합니다.<br>
- Spring Initializr를 사용하여 Maven을 사용하여 Spring Boot 애플리케이션을 빌드하는 예제를 만드는 가이드 입니다.

6. [JDBC](relationaldataaccess/README.md)<br>
- 이 가이드는 Spring을 사용하여 관계형 데이터베이스에 액세스하는 과정을 안내합니다.<br>
- Spring의 JdbcTemplate을 사용하여 데이터베이스에 저장된 고객 데이터를 검색하는 예제를 만드는 가이드 입니다.

7. [FileUpload](uploadingfiles/README.md)<br>
- 이 가이드는 Spring을 사용하여 파일 업로드를 처리하는 과정을 안내합니다.<br>
- 파일 업로드를 허용하는 Spring Boot 웹 애플리케이션을 생성합니다. 또한 테스트 파일을 업로드하기 위한 간단한 HTML 인터페이스를 구축하는 가이드입니다.

8. [LDAP](authenticatingldap/README.md)<br>
- 이 가이드는 Spring을 사용하여 LDAP 서버에 인증하는 과정을 안내합니다.<br>
- Spring Security를 사용하여 LDAP 서버에 인증하는 예제를 만드는 가이드 입니다. <br>
- LDAP란? <br>
-> 경량 디렉터리 액세스 프로토콜로 네트워크에서 디렉터리 서비스에 접근하기 위해 사용되는 프로토콜이다.


9. [Redis](messagingredis/README.md)<br>
- 이 가이드는 Spring Data Redis를 사용하여 Redis로 보낸 메시지를 게시하고 구독하는 과정을 안내합니다.<br>
- Spring Data Redis를 사용하여 메시지를 보내고 받는 예제를 만드는 가이드 입니다.
- Resdis란? <br>
-> 메모리 기반의 Key-Value 구조 데이터 관리 시스템으로, 오픈 소스이다.

10. [Rabbitmq](messagingrabbitmq/README.md)<br>
- 이 가이드는 메시지를 게시하고 구독하는 RabbitMQ AMQP 서버를 설정하고 해당 RabbitMQ 서버와 상호 작용할 Spring Boot 애플리케이션을 만드는 과정을 안내합니다.
- Spring AMQP의 RabbitTemplate을 사용하여 메시지를 게시하고 MessageListenerAdapter를 사용하여 POJO에서 메시지를 구독하는 애플리케이션을 빌드합니다.
- RabbitMQ란? <br>
-> AMQP를 구현한 오픈소스 메시지 브로커입니다.
- AMQP란? <br>
  -> 메시지 지향 미들웨어를 위한 개방형 표준 프로토콜로, 메시지 기반 애플리케이션을 개발하기 위한 라이브러리입니다.

11. [Neo4j](accessingdataneo4j/README.md)<br>
- 이 가이드는 Neo4j 에서 데이터를 저장하고 검색하는 애플리케이션을 빌드하기 위해 Spring Data Neo4j를 사용하는 프로세스를 안내합니다 .
- Spring Data Neo4j를 사용하여 그래프 데이터베이스에 데이터를 저장하고 검색하는 예제를 만드는 가이드 입니다.
- Neo4j란? <br>
-> 그래프 데이터베이스로, 노드와 노드를 연결하는 관계로 데이터를 표현한다. 

12. [Valid](validatingforminput/README.md)<br>
- 이 가이드는 Spring MVC를 사용하여 폼 입력을 유효성 검사하는 과정을 안내합니다.<br>
- Spring의 Bean Validation을 사용하여 폼 입력을 유효성 검사하는 예제를 만드는 가이드 입니다.
- Bean Validation이란? <br>
-> Java EE 6의 일부로 도입된 Bean Validation은 Java 객체의 속성을 유효성 검사하기 위한 표준이다.

13. [Actuator](actuatorservice/README.md)<br>
- 이 가이드는 Spring Boot Actuator를 사용하여 Spring Boot 애플리케이션을 모니터링하는 과정을 안내합니다.<br>
- Spring Boot Actuator를 사용하여 애플리케이션의 상태를 모니터링하는 예제를 만드는 가이드 입니다.
- Actuator란? <br>
-> Actuator는 Spring Boot 애플리케이션의 모니터링 및 관리를 위한 기능을 제공하는 라이브러리이다.

14. [JMS](jms_hello/README.md)<br>
- 이 가이드는 Spring을 사용하여 JMS 메시지를 보내고 받는 과정을 안내합니다.<br>
- Spring의 JmsTemplate을 사용하여 JMS 메시지를 보내고 받는 예제를 만드는 가이드 입니다.
- JMS란? <br>
-> JMS(Java Message Service)는 자바 애플리케이션 간에 메시지를 교환하기 위한 API이다.

15. [Batch](batchprocessing/README.md)<br>
- 이 가이드는 Spring Batch를 사용하여 배치 프로세스를 실행하는 과정을 안내합니다.<br>
- Spring Batch를 사용하여 배치 프로세스를 실행하는 예제를 만드는 가이드 입니다.
- Spring Batch란? <br>
-> Spring Batch는 대용량의 데이터를 처리하는 데 필요한 기능을 제공하는 Spring 프로젝트이다.

16. [Security](security-web/README.md)<br>
- 이 가이드는 Spring Security를 사용하여 웹 애플리케이션을 보호하는 과정을 안내합니다.<br>
- 고정된 사용자 목록이 지원하는 로그인 양식으로 페이지를 보호하는 Spring MVC 애플리케이션을 빌드합니다.
- Spring Security란? <br>
-> Spring Security는 Spring 기반의 애플리케이션의 보안(인증과 권한, 인가)을 담당하는 스프링 하위 프레임워크이다. 

17. [Hateoas](rest-hateoas/README.md)<br>
- 이 가이드는 Spring을 사용하여 "Hello, World" 하이퍼미디어 기반 REST 웹 서비스를 생성하는 프로세스를 안내합니다.
- 하이퍼미디어는 REST의 중요한 측면입니다. 이를 통해 클라이언트와 서버를 크게 분리하고 독립적으로 발전할 수 있는 서비스를 구축할 수 있습니다. REST 리소스에 대해 반환된 표현에는 데이터뿐만 아니라 관련 리소스에 대한 링크도 포함됩니다. 따라서 표현의 디자인은 전체 서비스의 디자인에 매우 중요합니다.
- HATEOAS란? <br>
-> HATEOAS(Hypermedia as the Engine of Application State)는 REST의 제약 조건 중 하나로, 하이퍼미디어를 통해 애플리케이션의 상태를 전이시키는 것을 의미한다. 

18. [Gemfire](accessing-data-gemfire/README.md)<br>
- 이 가이드는 Spring Data GemFire를 사용하여 GemFire 데이터를 저장하고 검색하는 애플리케이션을 빌드하는 프로세스를 안내합니다.
- Spring Data GemFire를 사용하여 GemFire 데이터를 저장하고 검색하는 예제를 만드는 가이드 입니다.
- GemFire란? <br>
-> 대규모 데이터 처리에 특화된 인메모리 데이터 그리드 플랫폼입니다. 데이터를 빠르게 저장하고 처리할 수 있으며, 분산 환경에서 안정성과 확장성을 제공합니다.

19. [Integration](integration/README.md)<br>
- 이 가이드는 Spring 통합을 사용하여 RSS Feed(Spring 블로그)에서 데이터를 검색하고 데이터를 조작한 다음 파일에 쓰는 간단한 애플리케이션을 만드는 과정을 안내합니다.
- 기존 XML 구성을 사용하여 Spring 통합으로 흐름을 정의하는 예제를 만드는 가이드 입니다.
- Spring Integration이란? <br>
-> Spring Integration은 Spring 기반의 애플리케이션에서 메시지를 처리하는 데 필요한 기능을 제공하는 Spring 프로젝트이다.

20. [Caching GemFire](caching-gemfire/README.md)<br>
- 이 가이드는 Apache Geode의 데이터 관리 시스템을 사용하여 애플리케이션 코드의 특정 호출을 캐시하는 과정을 안내합니다.
- Spring Boot를 사용하여 Apache Geode의 데이터 관리 시스템을 사용하여 애플리케이션 코드의 특정 호출을 캐시하는 예제를 만드는 가이드 입니다.
- Apache Geode란? <br>
- Apache Geode는 대규모 데이터 처리에 특화된 인메모리 데이터 그리드 플랫폼입니다. 데이터를 빠르게 저장하고 처리할 수 있으며, 분산 환경에서 안정성과 확장성을 제공합니다.


21. [Transaction](managing-transaction/README.md)<br>
- 이 가이드는 비간섭 트랜잭션으로 데이터베이스 작업을 래핑하는 프로세스를 안내합니다.
- 특수 JDBC 코드를 작성할 필요 없이 데이터베이스 작업을 트랜잭션으로 만드는 간단한 JDBC 응용 프로그램을 만드는 가이드 입니다.
- Spring Transaction이란? <br>
-> Spring Transaction은 Spring 기반의 애플리케이션에서 트랜잭션을 처리하는 데 필요한 기능을 제공하는 Spring 프로젝트이다. 

22. [JPA](accessing-data-jpa/README.md)<br>
- 이 가이드는 Spring Data JPA를 사용하여 관계형 데이터베이스에서 데이터를 저장하고 검색하는 애플리케이션을 구축하는 과정을 안내합니다.
- Customer메모리 기반 데이터베이스에 POJO(Plain Old Java Objects)를 저장하는 애플리케이션을 만드는 가이드 입니다.
- Spring Data JPA란? <br>
-> Spring Data JPA는 Spring 기반의 애플리케이션에서 JPA(Java Persistence API)를 사용하여 데이터를 저장하고 검색하는 데 필요한 기능을 제공하는 Spring 프로젝트이다.

23. [Mongodb](accessing-data-mongodb/README.md)<br>
- 이 가이드는 문서 기반 데이터베이스인 MongoDB 에서 데이터를 저장하고 검색하는 애플리케이션을 빌드하기 위해 Spring Data MongoDB를 사용하는 프로세스를 안내합니다 .
- Spring Data MongoDB를 사용하여 MongoDB 데이터베이스에 Customer POJO(Plain Old Java Objects)를 저장하고 검색하는 예제를 만드는 가이드 입니다.
- Spring Data MongoDB란? <br>
-> Spring Data MongoDB는 Spring 기반의 애플리케이션에서 MongoDB를 사용하여 데이터를 저장하고 검색하는 데 필요한 기능을 제공하는 Spring 프로젝트이다. 

24. [Spring MVC](serving-web-content/README.md)<br>
- 이 가이드는 Spring으로 "Hello, World" 웹 사이트를 만드는 과정을 안내합니다.
- Spring MVC를 사용하여 정적 HTML 파일을 제공하는 간단한 웹 응용 프로그램을 만드는 가이드 입니다.

25. [Jar-to-War](convert-jar-to-war/README.md)<br>
- 이 가이드는 Spring Boot를 사용하여 JAR 파일을 WAR 파일로 변환하는 과정을 안내합니다.

26. [Async Method](async-method/READMD.md)<br>
- 이 가이드는 GitHub에 대한 비동기 쿼리를 생성하는 과정을 안내합니다. 초점은 서비스를 확장할 때 자주 사용되는 기능인 비동기 부분에 있습니다.
- 비동기 메서드를 사용하여 비동기 쿼리를 생성하는 예제를 만드는 가이드 입니다.

27. [Handling Submit](handling-form-submission/README.md)<br>
- 이 가이드는 Spring을 사용하여 웹 양식을 만들고 제출하는 과정을 안내합니다.
- 브라우저에서 ID 및 콘텐츠 양식 필드를 채워 양식을 제출하는 간단한 웹 응용 프로그램을 만드는 가이드 입니다.

28. [Spring-boot-groovy](spring-boot-groovy/README.md)<br>
- 이 가이드는 Spring Boot가 애플리케이션 개발을 가속화하고 예제를 제공하는데 도움이 되는 것을 안내합니다.
- Spring Boot와 Groovy를 사용하여 간단한 웹 응용 프로그램을 만드는 가이드 입니다.
- Groovy란? <br>
-> JVM 에서 동작하는 동적 프로그래밍 언어로, 자바와 호환되며 보다 간결하고 생산적인 코드 작성을 가능하게 합니다.

29. [WebSocket](messaging-stomp-websocket/README.md)<br>
- 이 가이드는 Spring과 STOMP 메시징을 사용하여 브라우저와 서버 간에 대화형 웹 애플리케이션을 구축하는 과정을 안내합니다.
- STOMP 메시징을 사용하여 브라우저와 서버 간에 대화형 웹 애플리케이션을 만드는 가이드 입니다.
- STOMP란? <br>
->  하위 수준 WebSocket 위에서 작동하는 메시징 프로토콜로, 브라우저와 서버 간에 대화형 통신을 가능하게 합니다.

30. [STS](sts/README.md) <br>
- 이 가이드는 시작하기 가이드 중 하나를 빌드하기 위해 STS(Spring Tool Suite)를 사용하는 과정을 안내합니다.
- Spring Tool Suite란? <br>
-> Spring Tool Suite는 Spring 기반의 애플리케이션을 개발하기 위한 IDE(통합 개발 환경)입니다. 

31. [AngularJS](consuming-rest-angularjs/README.md) <br>
- 이 가이드는 Spring MVC 기반 RESTful 웹 서비스를 사용하는 간단한 AngularJS 클라이언트를 작성하는 과정을 안내합니다 .
- AngularJS를 사용하여 RESTful 웹 서비스를 호출하는 예제를 만드는 가이드 입니다.
- AngularJS란? <br>
-> AngularJS는 HTML을 사용하여 웹 애플리케이션을 구축하기 위한 오픈 소스 프론트엔드 웹 애플리케이션 프레임워크입니다.

32. [Jquery](consuming-rest-jquery/README.md) <br>
- 이 가이드는 Spring MVC 기반 RESTful 웹 서비스를 사용하는 간단한 jQuery 클라이언트를 작성하는 과정을 안내합니다.
- jQuery를 사용하여 RESTful 웹 서비스를 호출하는 예제를 만드는 가이드 입니다.
- jQuery란? <br>
-> jQuery는 HTML 문서 탐색, 이벤트 처리, 애니메이션 및 Ajax 상호 작용을 위한 간결하고 재사용 가능한 JavaScript 코드를 작성하기 위한 빠르고 간편한 방법을 제공하는 JavaScript 라이브러리입니다.

33. [Cross Origin](rest-service-cors/README.md) <br>
- 이 가이드는 Spring을 사용하여 "Hello, World" RESTful 웹 서비스를 생성하고, CORS(Cross-Origin Resource Sharing)에 대한 헤더를 응답에 포함하는 방법을 안내합니다.
- CORS란? <br>
-> CORS는 웹 애플리케이션이 다른 도메인에서 리소스를 요청할 수 있도록 허용하는 메커니즘입니다.

34. [Consuming SOAP](consuming-web-service-maven/README.md) <br>
- 이 가이드는 Spring에서 SOAP 기반 웹 서비스를 사용하는 과정을 안내합니다.
- Spring에서 SOAP 기반 웹 서비스를 호출하는 예제를 만드는 가이드 입니다.
- SOAP란? <br>
-> SOAP는 XML 기반 메시지 교환 프로토콜로, 네트워크 상에서 구조화된 정보를 교환하기 위한 통신 규약입니다. 

35. [JPA Rest](accessing-data-jpa/README.md) <br>
- 이 가이드는 Spring Data JPA를 사용하여 관계형 데이터베이스에서 데이터를 저장하고 검색하는 애플리케이션을 구축하는 과정을 안내합니다.
- Spring Data JPA를 사용하여 관계형 데이터베이스에서 데이터를 저장하고 검색하는 예제를 만드는 가이드 입니다.
- JPA란? <br>
-> JPA는 Java Persistence API의 약자로, 자바 ORM 기술에 대한 API 표준 명세입니다. 

36. [Neo4j Rest](accessing-neo4j-data-rest/README.md) <br>
- 이 가이드는 하이퍼미디어 기반 RESTful 프런트 엔드를 통해 그래프 기반 데이터(Neo4j)에 액세스하는 애플리케이션을 만드는 과정을 안내합니다 .
- 하이퍼미디어 기반 RESTful 프런트 엔드를 통해 그래프 기반 데이터(Neo4j)에 액세스하는 예제를 만드는 가이드 입니다.
- Neo4j를 설치하여 서버의 properties 와 연결을 시킨 후 curl 명령어를 이용하여 응답을 받아봤습니다.

37. [MongoDB Rest](accessing-mongodb-data-rest/README.md) <br>
- 이 가이드는 하이퍼미디어 기반 RESTful 프런트 엔드를 통해 문서 기반 데이터에 액세스하는 애플리케이션을 만드는 과정을 안내합니다 .
- 하이퍼미디어 기반 RESTful 프런트 엔드를 통해 문서 기반 데이터에 액세스하는 예제를 만드는 가이드 입니다.
- MongoDB를 설치하여 서버의 properties 와 연결을 시킨 후 curl 명령어를 이용하여 응답을 받아봤습니다.
- MongoDB란? <br>
- MongoDB는 문서 지향적 데이터베이스로, 데이터를 JSON과 같은 동적 스키마 형식으로 저장합니다.

38. [Gemfire Rest](accessing-gemfire-data-rest/README.md) <br>
- 이 가이드는 하이퍼미디어 기반 REST-ful 프런트엔드를 통해 Apache Geode에 저장된 데이터에 액세스하는 애플리케이션을 만드는 과정을 안내합니다.
- 하이퍼미디어 기반 REST-ful 프런트엔드를 통해 Apache Geode에 저장된 데이터에 액세스하는 예제를 만드는 가이드 입니다.

39. [Producing SOAP](producing-web-service-maven/README.md) <br>
- 이 가이드는 Spring을 사용하여 SOAP 기반 웹 서비스 서버를 만드는 과정을 안내합니다.
- Spring을 사용하여 SOAP 기반 웹 서비스 서버를 만드는 예제를 만드는 가이드 입니다.

40. [Caching Data](caching/README.md) <br>
- 이 가이드는 Spring 관리 빈에서 캐싱을 활성화하는 과정을 안내합니다.
- Spring 관리 빈에서 캐싱을 활성화하는 예제를 만드는 가이드 입니다.
- 캐싱이란? <br>
- 캐싱은 데이터나 값을 미리 복사해 놓는 임시 장소를 가리킵니다.
- 캐시는 데이터를 빠르게 가져오기 위해 데이터를 저장해 놓는 장소입니다.

41. STS에서 Cloud Foundry에 배포

42. IntelliJ IDEA로 시작하기 가이드 작업

43. [Crud-Vaadin](crud-vaadin/README.md) <br>
- 이 가이드는 Spring Data JPA 기반 백엔드에서 Vaadin 기반 UI를 사용하는 애플리케이션을 구축하는 과정을 안내합니다 .
- 간단한 JPA 리포지토리를 위한 Vaadin UI를 빌드하여 완전한 CRUD(만들기, 읽기, 업데이트 및 삭제) 기능이 있는 애플리케이션과 사용자 지정 리포지토리 방법을 사용하는 필터링 예제를 만드는 가이드 입니다.
- Vaadin이란? <br>
-> Vaadin은 Java 개발자가 웹 애플리케이션을 만들 수 있도록 도와주는 오픈 소스 웹 프레임워크입니다.

44.

45.

46.

47.

48.

49.

50.

51.

52.

53.

54.

55.

56.

57.

58.

59.

60.

61.

62.

63.



























