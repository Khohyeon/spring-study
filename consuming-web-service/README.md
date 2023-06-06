## SOAP 웹 서비스 사용
이 가이드는 Spring에서 SOAP 기반 웹 서비스를 사용하는 과정을 안내합니다.

SOAP란??? (Simple Object Access Protocol)
- 웹서비스에서 통신하기 위해 사용되는 프로토콜입니다.
- XML 기반의 메시지 교환 형식을 정의하며 원격 프로시저 호출을 통해 클라이언트와 서버 간에 상호작용할 수 있도록 합니다.
- 주로 웹 서비스와 같은 분산 환경에서 사용되며 , HTTP, HTTPS, SMTP 등을 통해 전송될 수 있습니다.

### 무엇을 만들 것인가
SOAP를 사용하여 원격 WSDL 기반 웹 서비스에서 국가 데이터를 가져오는 클라이언트를 빌드합니다. 이 가이드를 따라 국가 서비스에 대해 자세히 알아보고 직접 서비스를 실행할 수 있습니다.

이 서비스는 국가 데이터를 제공합니다. 이름을 기반으로 국가에 대한 데이터를 쿼리할 수 있습니다.

### 필요한 것
* 약 15분

* 선호하는 텍스트 편집기 또는 IDE

* 자바 1.8 이상

* Gradle 7.5+ 또는 Maven 3.5+

* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작하려면 Spring Initializr로 시작하기로 이동하세요.

기본 사항을 건너뛰려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다.

* cd를 gs-소비 웹 서비스/이니셜로 이동

* WSDL 기반 도메인 개체 생성으로 이동합니다.

완료하면 gs-fused-web-service/complete의 코드와 비교하여 결과를 확인할 수 있습니다.

``` 
SOAP 웹 서비스 제작을 읽으면 이 가이드에서 spring-boot-starter-ws를 사용하지 않는 이유가 궁금할 것입니다. 
해당 Spring Boot 스타터는 서버 측 웹 서비스 전용입니다. 그 스타터는 웹 호출을 하는 데 필요하지 않은 임베디드 Tomcat과 같은 것들을 보드에 가져옵니다.
```


대상 웹 서비스를 로컬로 실행
함께 제공되는 가이드의 단계를 따르거나 리포지토리를 복제하고 전체 디렉터리에서 서비스를 실행합니다(예: mvn spring-boot:run 사용). 브라우저에서 http://localhost:8080/ws/countries.wsdl을 방문하여 작동하는지 확인할 수 있습니다. 이렇게 하지 않으면 나중에 JAXB 도구에서 빌드할 때 혼란스러운 예외가 표시됩니다.

### 스프링 이니셜라이저로 시작하기
모든 Spring 애플리케이션의 경우 Spring Initializr로 시작해야 합니다. Initializr는 애플리케이션에 필요한 모든 종속성을 가져오는 빠른 방법을 제공하고 많은 설정을 수행합니다. 이 예제에는 Spring Web Services 종속성만 필요합니다.

프로젝트를 초기화하려면:

1. https://start.spring.io로 이동합니다. 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭하고 Spring 웹 서비스를 선택합니다.

4. 생성을 클릭합니다.

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### Gradle
종속성, 구성, bootJar 섹션 및 WSDL 생성 플러그인을 추가해야 합니다.

다음 목록은 Gradle에 추가해야 하는 종속 항목을 보여줍니다.
```
implementation ('org.springframework.boot:spring-boot-starter-web-services') {
	exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'
}
implementation 'org.springframework.ws:spring-ws-core'
// For Java 11:
implementation 'org.glassfish.jaxb:jaxb-runtime'
implementation(files(genJaxb.classesDir).builtBy(genJaxb))

jaxb "com.sun.xml.bind:jaxb-xjc:2.1.7"
```
Tomcat 제외에 유의하십시오. Tomcat이 이 빌드에서 실행되도록 허용된 경우 국가 데이터를 제공하는 Tomcat 인스턴스와 포트 충돌이 발생합니다.

다음 목록은 Gradle에 추가해야 하는 bootJar 섹션을 보여줍니다.
```
bootJar {
	baseName = 'gs-consuming-web-service'
	version =  '0.0.1'
}
```
WSDL 기반 도메인 개체 생성 섹션에서는 WSDL 생성 플러그인에 대해 설명합니다.

다음 목록은 최종 build.gradle 파일을 보여줍니다.
```
plugins {
	id 'org.springframework.boot' version '2.7.1'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

// tag::configurations[]
configurations {
	jaxb
}
// end::configurations[]

repositories {
	mavenCentral()
}

// tag::wsdl[]
task genJaxb {
	ext.sourcesDir = "${buildDir}/generated-sources/jaxb"
	ext.classesDir = "${buildDir}/classes/jaxb"
	ext.schema = "http://localhost:8080/ws/countries.wsdl"

	outputs.dir classesDir

	doLast() {
		project.ant {
			taskdef name: "xjc", classname: "com.sun.tools.xjc.XJCTask",
					classpath: configurations.jaxb.asPath
			mkdir(dir: sourcesDir)
			mkdir(dir: classesDir)

				xjc(destdir: sourcesDir, schema: schema,
						package: "com.example.consumingwebservice.wsdl") {
						arg(value: "-wsdl")
					produces(dir: sourcesDir, includes: "**/*.java")
				}

				javac(destdir: classesDir, source: 1.8, target: 1.8, debug: true,
						debugLevel: "lines,vars,source",
						classpath: configurations.jaxb.asPath) {
					src(path: sourcesDir)
					include(name: "**/*.java")
					include(name: "*.java")
					}

				copy(todir: classesDir) {
						fileset(dir: sourcesDir, erroronmissingdir: false) {
						exclude(name: "**/*.java")
				}
			}
		}
	}
}
// end::wsdl[]

dependencies {
// tag::dependency[]
	implementation ('org.springframework.boot:spring-boot-starter-web-services') {
		exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'
	}
	implementation 'org.springframework.ws:spring-ws-core'
	// For Java 11:
	implementation 'org.glassfish.jaxb:jaxb-runtime'
	implementation(files(genJaxb.classesDir).builtBy(genJaxb))

	jaxb "com.sun.xml.bind:jaxb-xjc:2.1.7"
// end::dependency[]
	testImplementation('org.springframework.boot:spring-boot-starter-test')
}

test {
	useJUnitPlatform()
}

// tag::bootjar[]
bootJar {
	baseName = 'gs-consuming-web-service'
	version =  '0.0.1'
}
// end::bootjar[]
```

URL에 있는 WSDL에 대한 클래스를 생성하여 해당 클래스를 com.example. 소모 웹 서비스.wsdl 패키지에 넣습니다.
```
task genJaxb {
  ext.sourcesDir = "${buildDir}/generated-sources/jaxb"
  ext.classesDir = "${buildDir}/classes/jaxb"
  ext.schema = "http://localhost:8080/ws/countries.wsdl"

  outputs.dir classesDir

  doLast() {
    project.ant {
      taskdef name: "xjc", classname: "com.sun.tools.xjc.XJCTask",
          classpath: configurations.jaxb.asPath
      mkdir(dir: sourcesDir)
      mkdir(dir: classesDir)

        xjc(destdir: sourcesDir, schema: schema,
            package: "com.example.consumingwebservice.wsdl") {
            arg(value: "-wsdl")
          produces(dir: sourcesDir, includes: "**/*.java")
        }

        javac(destdir: classesDir, source: 1.8, target: 1.8, debug: true,
            debugLevel: "lines,vars,source",
            classpath: configurations.jaxb.asPath) {
          src(path: sourcesDir)
          include(name: "**/*.java")
          include(name: "*.java")
          }

        copy(todir: classesDir) {
            fileset(dir: sourcesDir, erroronmissingdir: false) {
            exclude(name: "**/*.java")
        }
      }
    }
  }
}
```
Gradle에는 (아직) JAXB 플러그인이 없으므로 Ant 작업이 포함되어 있어 Maven보다 조금 더 복잡합니다. 해당 코드를 생성하려면 ./gradlew compileJava를 실행한 다음 제대로 작동하는지 확인하려면 build/generated-sources를 살펴보세요.

두 경우 모두 JAXB 도메인 개체 생성 프로세스가 빌드 도구의 수명 주기에 연결되어 있으므로 성공적으로 빌드한 후에는 추가 단계를 실행할 필요가 없습니다.

### Country 서비스 클라이언트 생성
웹 서비스 클라이언트를 생성하려면 WebServiceGatewaySupport 클래스를 확장하고 작업을 코딩해야 합니다.


