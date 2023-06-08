## SOAP 웹 서비스 생성
이 가이드는 Spring을 사용하여 SOAP 기반 웹 서비스 서버를 만드는 과정을 안내합니다.

### 무엇을 만들 것인가
WSDL 기반 SOAP 웹 서비스를 사용하여 다양한 유럽 국가의 데이터를 노출하는 서버를 구축합니다.

```
예제를 단순화하기 위해 영국, 스페인 및 폴란드에 대해 하드코딩된 데이터를 사용합니다.
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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-soap-service.git

* cd 로gs-soap-service/initial

* Spring-WS 종속성 추가 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-soap-service/complete.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭 하고 Spring Web 및 Spring Web Services를 선택합니다 .

4. 생성 을 클릭합니다 .

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

```
pom.xml및 파일 모두 build.gradle다음 단계에서 추가할 추가 빌드 정보가 필요합니다.
```

### Spring-WS 종속성 추가
프로젝트는 빌드 파일의 종속성으로 spring-ws-core 및 wsdl4j를 포함해야 합니다.

다음 예는 Maven을 사용하는 경우 pom.xml 파일에 대해 수행해야 하는 변경 사항을 보여줍니다.

pom.xml

```xml
<dependency>
	<groupId>wsdl4j</groupId>
	<artifactId>wsdl4j</artifactId>
</dependency>
```
```gradle
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-starter-web-services'
	implementation 'wsdl4j:wsdl4j'
	jaxb("org.glassfish.jaxb:jaxb-xjc")
	testImplementation('org.springframework.boot:spring-boot-starter-test')
}
```

