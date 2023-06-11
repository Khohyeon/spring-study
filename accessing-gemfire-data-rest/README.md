## REST를 사용하여 Pivotal GemFire의 데이터에 액세스
이 가이드는 하이퍼미디어 기반 REST-ful 프런트엔드를 통해 Apache Geode에 저장된 데이터에 액세스하는 애플리케이션을 만드는 과정을 안내합니다.

### 무엇을 만들 것인가
Spring Data REST를 사용하여 Apache Geode In-Memory Data Grid(IMDG)에 저장된 Person 개체를 생성하고 검색할 수 있는 Spring 웹 애플리케이션을 빌드합니다. Spring Data REST는 Spring HATEOAS 및 Spring Data for Apache Geode의 기능을 가져와 자동으로 함께 결합합니다.

```
Spring Data REST는 또한 Spring Data JPA , Spring Data MongoDB 및 Spring Data Neo4j를 백엔드 데이터 저장소로 지원 하지만 이 가이드의 일부는 아닙니다.
```


Apache Geode 개념 및 Apache Geode의 데이터 액세스에 대한 일반적인 지식은 <a href="https://spring.io/guides/gs/accessing-data-gemfire/"> Apache Geode로 데이터 액세스 가이드</a>를 읽어보세요 .

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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다. git clone https://github.com/spring-guides/gs-accessing-gemfire-data-rest.git

* gs-accessing-gemfire-data-rest/initial로 cd

* 도메인 개체 만들기로 이동합니다.

완료하면 gs-accessing-gemfire-data-rest/complete의 코드와 비교하여 결과를 확인할 수 있습니다.

### 스프링 이니셜라이저로 시작하기
모든 Spring 애플리케이션의 경우 Spring Initializr로 시작해야 합니다. Spring Initializr는 애플리케이션에 필요한 모든 종속성을 가져오는 빠른 방법을 제공하고 많은 설정을 수행합니다. 이 예제에는 "Spring for Apache Geode" 종속성이 필요합니다.

#### Maven을 사용하는 경우 (pom.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

	<modelVersion>4.0.0</modelVersion>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.7.0</version>
	</parent>

	<groupId>org.springframework</groupId>
	<artifactId>gs-accessing-gemfire-data-rest</artifactId>
	<version>0.1.0</version>

	<properties>
		<spring-shell.version>1.2.0.RELEASE</spring-shell.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-rest</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.data</groupId>
			<artifactId>spring-data-geode</artifactId>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.shell</groupId>
			<artifactId>spring-shell</artifactId>
			<version>${spring-shell.version}</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
			<exclusions>
				<exclusion>
					<groupId>org.junit.vintage</groupId>
					<artifactId>junit-vintage-engine</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

#### Gradle을 사용하는 경우 (build.gradle)
```gradle
plugins {
    id 'org.springframework.boot' version '2.7.0'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'io.freefair.lombok' version '6.3.0'
    id 'java'
}

apply plugin: 'eclipse'
apply plugin: 'idea'

group = "org.springframework"
version = "0.1.0"
sourceCompatibility = 1.8
targetCompatibility = 1.8

repositories {
    mavenCentral()
}

dependencies {

    implementation "org.springframework.boot:spring-boot-starter-data-rest"
    implementation "org.springframework.data:spring-data-geode"
    implementation "org.projectlombok:lombok"

    runtimeOnly "org.springframework.shell:spring-shell:1.2.0.RELEASE"

    testImplementation "org.springframework.boot:spring-boot-starter-test"

}

test {
    useJUnitPlatform()
}

bootJar {
    baseName = 'gs-accessing-gemfire-data-rest'
    version =  '0.1.0'
}
```

### Person 도메인 개체 만들기
```java
@Data
@Region("People")
public class Person {

  private static AtomicLong COUNTER = new AtomicLong(0L);

  @Id
  private Long id;

  private String firstName;
  private String lastName;

  @PersistenceConstructor
  public Person() {
    this.id = COUNTER.incrementAndGet();
  }
}
```
사람은 이름과 성을 가지고 있습니다. Apache Geode 도메인 개체에는 ID가 필요하므로 AtomicLong은 각 Person 개체 생성과 함께 증가하는 데 사용됩니다.

### PersonRepository 만들기
```java
@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface PersonRepository extends CrudRepository<Person, Long> {

  List<Person> findByLastName(@Param("name") String name);

}
```
이 리포지토리는 인터페이스이며 Person 개체와 관련된 다양한 데이터 액세스 작업(예: 기본 CRUD 및 간단한 쿼리)을 수행할 수 있습니다. CrudRepository를 확장하여 이러한 작업을 가져옵니다.

런타임에 Apache Geode용 Spring Data는 이 인터페이스의 구현을 자동으로 생성합니다. 그런 다음 Spring Data REST는 @RepositoryRestResource 주석을 사용하여 /people에서 REST-ful 엔드포인트를 생성하도록 Spring MVC에 지시합니다.


```
리포지토리를 내보내는 데 @RepositoryRestResource가 필요하지 않습니다. 기본값인 /persons 대신 /people을 사용하는 등 내보내기 세부 정보를 변경하는 데에만 사용됩니다.
```

여기에서 lastName을 기반으로 Person 개체 목록을 검색하는 사용자 지정 쿼리도 정의했습니다. 이 가이드에서 더 자세히 호출하는 방법을 볼 수 있습니다.

### 애플리케이션을 실행 가능하게 만들기
외부 응용 프로그램 서버에 배포하기 위해 이 서비스를 기존 WAR 파일로 패키징할 수 있지만 아래에서 설명하는 더 간단한 접근 방식은 독립 실행형 응용 프로그램을 만듭니다. 오래된 Java main() 메서드로 구동되는 실행 가능한 단일 JAR 파일에 모든 것을 패키징합니다. 그 과정에서 외부 서블릿 컨테이너에 배포하는 대신 Tomcat 서블릿 컨테이너를 HTTP 런타임으로 포함하기 위한 Spring의 지원을 사용합니다.

```java
@SpringBootApplication
@ClientCacheApplication(name = "AccessingGemFireDataRestApplication")
@EnableEntityDefinedRegions(
  basePackageClasses = Person.class,
  clientRegionShortcut = ClientRegionShortcut.LOCAL
)
@EnableGemfireRepositories
@SuppressWarnings("unused")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
```


@EnableGemfireRepositories 주석은 Apache Geode 저장소에 대한 Spring 데이터를 활성화합니다. Apache Geode용 Spring Data는 PersonRepository 인터페이스의 구체적인 구현을 생성하고 포함된 Apache Geode 인스턴스와 통신하도록 구성합니다.

#### 실행 가능한 JAR 빌드
Gradle 또는 Maven을 사용하여 명령줄에서 애플리케이션을 실행할 수 있습니다. 필요한 모든 종속성, 클래스 및 리소스를 포함하는 단일 실행 가능 JAR 파일을 빌드하고 실행할 수도 있습니다. 실행 가능한 jar을 빌드하면 개발 수명 주기 전체, 다양한 환경 등에 서비스를 애플리케이션으로 쉽게 제공, 버전 지정 및 배포할 수 있습니다.

Gradle을 사용하는 경우 ./gradlew bootRun을 사용하여 애플리케이션을 실행할 수 있습니다. 또는 다음과 같이 ./gradlew build를 사용하여 JAR 파일을 빌드한 다음 JAR 파일을 실행할 수 있습니다.
```
java -jar build/libs/accessing-gemfire-data-rest-0.1.0.jar
```

Maven을 사용하는 경우 ./mvnw spring-boot:run을 사용하여 애플리케이션을 실행할 수 있습니다. 또는 다음과 같이 ./mvnw clean 패키지로 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다.
```
java -jar target/accessing-gemfire-data-rest-0.1.0.jar
```

### 애플리케이션 테스트
이제 애플리케이션이 실행 중이므로 테스트할 수 있습니다. 원하는 REST 클라이언트를 사용할 수 있습니다. 다음 예제에서는 *nix 도구 curl을 사용합니다.

먼저 최상위 서비스를 보고 싶습니다.

```
요청 : $ curl http://localhost:8080

응답 :
{
  "_links" : {
    "people" : {
      "href" : "http://localhost:8080/people"
    },
    "profile" : {
      "href" : "http://localhost:8080/profile"
    }
  }
}

```

여기에서 이 서버가 제공하는 기능을 처음으로 엿볼 수 있습니다. http://localhost:8080/people에 사람 링크가 있습니다. Apache Geode용 Spring Data는 다른 Spring Data REST 가이드처럼 페이지 매김을 지원하지 않으므로 추가 탐색 링크가 없습니다.

```
Spring Data REST는 JSON 출력에 HAL 형식을 사용합니다. 이는 유연하며 제공되는 데이터에 인접한 링크를 제공하는 편리한 방법을 제공합니다.
```

```
요청 : $ curl http://localhost:8080/people

응답 :
{
  "_embedded" : {
    "people" : [ ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people"
    },
    "profile" : {
      "href" : "http://localhost:8080/profile/people"
    },
    "search" : {
      "href" : "http://localhost:8080/people/search"
    }
  }
}
```

POST 하여 Person 생성하기
```
요청 : $ curl -i -X POST -H "Content-Type:application/json" -d '{  "firstName" : "Frodo",  "lastName" : "Baggins" }' http://localhost:8080/people

응답 :
HTTP/1.1 201 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Location: http://localhost:8080/people/1
Content-Type: application/hal+json
Transfer-Encoding: chunked
Date: Fri, 09 Jun 2023 02:43:25 GMT

{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/1"
    },
    "person" : {
      "href" : "http://localhost:8080/people/1"
    }
  }
}

```

```
이전 POST 작업에 Location 헤더가 어떻게 포함되어 있는지 확인하십시오. 여기에는 새로 생성된 리소스의 URI가 포함됩니다. Spring Data REST에는 또한 RepositoryRestConfiguration.setReturnBodyOnCreate(…) 및 setReturnBodyOnCreate(…)에 대한 두 가지 메서드가 있어 방금 생성된 리소스의 표현을 즉시 반환하도록 프레임워크를 구성하는 데 사용할 수 있습니다.
```

people 결과 다시 보기

```
$ curl http://localhost:8080/people
{
  "_embedded" : {
    "people" : [ {
      "firstName" : "Frodo",
      "lastName" : "Baggins",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/people/1"
        },
        "person" : {
          "href" : "http://localhost:8080/people/1"
        }
      }
    } ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people"
    },
    "profile" : {
      "href" : "http://localhost:8080/profile/people"
    },
    "search" : {
      "href" : "http://localhost:8080/people/search"
    }
  }
}

```
사람 컬렉션 리소스에는 Frodo가 포함된 목록이 포함되어 있습니다. 자체 링크가 어떻게 포함되어 있는지 확인하십시오. Spring Data REST는 또한 Evo Inflector를 사용하여 그룹화를 위한 엔터티 이름을 복수화합니다.

people 상세보기
```
요청 : $ curl http://localhost:8080/people/1

응답 :
{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/1"
    },
    "person" : {
      "href" : "http://localhost:8080/people/1"
    }
  }
}

```

```
순전히 웹 기반으로 보일 수 있지만 배후에는 내장된 Apache Geode 데이터베이스와 통신하고 있습니다.
```

이 가이드에는 도메인 개체가 하나만 있습니다. 도메인 개체가 서로 관련된 보다 복잡한 시스템에서 Spring Data REST는 연결된 레코드를 탐색하는 데 도움이 되는 추가 링크를 렌더링합니다

#### findByFirstName 을 추가해서 테스트 해보기
```
요청 : $ curl http://localhost:8080/people/search

응답 :
{
  "_links" : {
    "findByLastName" : {
      "href" : "http://localhost:8080/people/search/findByLastName{?name}",
      "templated" : true
    },
    "findByFirstName" : {
      "href" : "http://localhost:8080/people/search/findByFirstName{?name}",
      "templated" : true
    },
    "self" : {
      "href" : "http://localhost:8080/people/search"
    }
  }
}

```

HTTP 쿼리 매개변수 이름을 포함하는 쿼리의 URL을 볼 수 있습니다. 알다시피 이것은 인터페이스에 포함된 @Param("name") 주석과 일치합니다.
```
요청 : $ curl http://localhost:8080/people/search/findByLastName?name=Baggins

응답 :
{
  "_embedded" : {
    "people" : [ ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/search/findByLastName?name=Baggins"
    }
  }
}

```

코드에서 List<Person>을 반환하도록 정의했으므로 모든 결과를 반환합니다. Person만 반환하도록 정의한 경우 반환할 Person 개체 중 하나를 선택합니다. 이것은 예측할 수 없기 때문에 여러 항목을 반환할 수 있는 쿼리에 대해서는 그렇게 하고 싶지 않을 것입니다.

PUT, PATCH 및 DELETE REST 호출을 실행하여 기존 레코드를 교체, 업데이트 또는 삭제할 수도 있습니다.

#### PUT 으로 업데이트 하기
```
요청 : $ curl -X PUT -H "Content-Type:application/json" -d '{ "firstName": "Bilbo", "lastName": "Baggins" }' http://localhost:8080/people/1

응답 :
{
  "firstName" : "Bilbo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/1"
    },
    "person" : {
      "href" : "http://localhost:8080/people/1"
    }
  }
}
```

#### PATCH 로 업데이트 하기
```
요청 : $ curl -X PATCH -H "Content-Type:application/json" -d '{ "firstName": "Bilbo Jr." }' http://localhost:8080/people/1

응답 :
{
  "firstName" : "Bilbo Jr.",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/1"
    },
    "person" : {
      "href" : "http://localhost:8080/people/1"
    }
  }
}
```

```
PUT은 전체 레코드를 대체합니다. 제공되지 않은 필드는 null로 대체됩니다. PATCH는 항목의 하위 집합을 업데이트하는 데 사용할 수 있습니다.
```

#### DELETE 로 삭제하기
```
요청 : curl -X DELETE http://localhost:8080/people/1
```
아무런 응답이 없는 것을 확인하면 잘 삭제가 됐다고 생각을 할 수 있습니다. 다시 people을 조회 해보겠습니다.
```
요청 : $ curl http://localhost:8080/people

응답 :
{
  "_embedded" : {
    "people" : [ ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people"
    },
    "profile" : {
      "href" : "http://localhost:8080/profile/people"
    },
    "search" : {
      "href" : "http://localhost:8080/people/search"
    }
  }
}
```
이 하이퍼미디어 기반 인터페이스의 매우 편리한 측면은 curl(또는 사용 중인 REST 클라이언트)을 사용하여 모든 REST-ful 엔드포인트를 검색할 수 있는 방법입니다. 고객과 공식적인 계약 또는 인터페이스 문서를 교환할 필요가 없습니다.

