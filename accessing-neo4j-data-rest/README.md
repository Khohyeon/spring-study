## REST로 Neo4j 데이터에 액세스
이 가이드는 하이퍼미디어 기반 RESTful 프런트 엔드를 통해 그래프 기반 데이터에 액세스하는 애플리케이션을 만드는 과정을 안내합니다 .

### 무엇을 만들 것인가
Spring 데이터 REST를 사용하여 Neo4jPerson NoSQL 데이터베이스 에 저장된 개체를 생성하고 검색할 수 있는 Spring 애플리케이션을 빌드합니다 . Spring Data REST는 Spring HATEOAS 및 Spring Data Neo4j 의 기능을 가져와 자동으로 함께 결합합니다.

### 필요한 것
* 약 15분

* 선호하는 텍스트 편집기 또는 IDE

* 자바 17 이상

* Gradle 7.5+ 또는 Maven 3.5+

* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작 하려면 [scratch] 로 이동합니다 .

기본 사항을 건너뛰 려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-accessing-neo4j-data-rest.git

* cd 로gs-accessing-neo4j-data-rest/initial

* Neo4j 액세스 권한으로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-accessing-neo4j-data-rest/complete.

### Neo4j 서버 세우기
이 애플리케이션을 빌드하기 전에 Neo4j 서버를 설정해야 합니다.

Neo4j에는 무료로 설치할 수 있는 오픈 소스 서버가 있습니다.

Homebrew가 설치된 Mac에서는 터미널 창에 다음을 입력할 수 있습니다.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io로 이동합니다. 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭하고 Rest Repositories 및 Spring Data Neo4j를 선택합니다.

4. 생성을 클릭합니다.

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### Neo4j 액세스 권한
Neo4j Community Edition에 액세스하려면 자격 증명이 필요합니다. 다음과 같이 src/main/resources/application.properties에서 속성을 설정하여 자격 증명을 구성할 수 있습니다.

```properties
spring.neo4j.uri=bolt://localhost:7687
spring.data.neo4j.username=neo4j
spring.data.neo4j.password=secret
```
여기에는 기본 사용자 이름(neo4j)과 이전에 설정한 새로 설정한 비밀번호(secret)가 포함됩니다.

```properties
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=secret
```
Spring Data Neo4j의 업데이트로 인해서 최신 버전으로 이렇게 변경 해줘야한다
새로운 버전의 Spring Boot 및 Spring Data Neo4j를 사용하는 경우, 설정 정보를 변경하여 spring.neo4j.authentication.username 및 spring.neo4j.authentication.password를 사용하도록 수정해야 합니다. 변경된 설정에 따라 데이터베이스에 대한 인증 정보를 제공할 수 있습니다.

### 도메인 객체 생성
다음 예제(src/main/java/com/example/accessingneo4jdatarest/Person.java에 있음)에 나와 있는 것처럼 사람을 나타내려면 새 도메인 개체를 만들어야 합니다.
```java
@Node
public class Person {

  @Id @GeneratedValue private Long id;

  private String firstName;
  private String lastName;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
```

Person 개체에는 이름과 성이 있습니다. 자동으로 생성되도록 구성된 ID 개체도 있으므로 그렇게 할 필요가 없습니다.


### 개인 저장소 생성
다음으로 다음 예제(src/main/java/com/example/accessingneo4jdatarest/PersonRepository.java에 있음)에 표시된 것처럼 간단한 리포지토리를 생성해야 합니다.

```java
@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface PersonRepository extends PagingAndSortingRepository<Person, Long>, CrudRepository<Person, Long> {

  List<Person> findByLastName(@Param("name") String name);

}
```

이 Repository는 인터페이스이며 Person 개체와 관련된 다양한 작업을 수행할 수 있습니다. Spring Data Commons에 정의된 PagingAndSortingRepositry 인터페이스를 확장하여 이러한 작업을 가져옵니다.

런타임에 Spring Data REST는 이 인터페이스의 구현을 자동으로 생성합니다. 그런 다음 @RepositoryRestResource 주석을 사용하여 /people에서 RESTful 엔드포인트를 생성하도록 Spring MVC에 지시합니다.

```
Repository를 내보내는 데 @RepositoryRestResource가 필요하지 않습니다. 기본값인 /persons 대신 /people을 사용하는 등 내보내기 세부 정보를 변경하는 데에만 사용됩니다.
```

여기에서 lastName 값을 기반으로 Person 개체 목록을 검색하는 사용자 지정 쿼리도 정의했습니다. 이 가이드의 뒷부분에서 이를 호출하는 방법을 확인할 수 있습니다.

### 애플리케이션 클래스 찾기
Spring Initializr는 프로젝트 생성에 사용할 때 애플리케이션 클래스를 생성합니다. 
src/main/java/com/example/accessingneo4jdatarest/Application.java에서 찾을 수 있습니다. Spring Initializr는 패키지 이름을 연결(및 대소문자를 적절하게 변경)하고 이를 애플리케이션에 추가하여 애플리케이션 케이스 이름을 생성합니다. 이 경우 다음 목록과 같이 AccessingNeo4jDataRestApplication을 얻습니다.

```java
@EnableTransactionManagement
@EnableNeo4jRepositories
@SpringBootApplication
public class AccessingNeo4jDataRestApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccessingNeo4jDataRestApplication.class, args);
  }
}
```

@EnableNeo4jRepositories 주석은 Spring Data Neo4j를 활성화합니다. Spring Data Neo4j는 PersonRepository의 구체적인 구현을 만들고 Cypher 쿼리 언어를 사용하여 임베디드 Neo4j 데이터베이스와 통신하도록 구성합니다.

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/accessing-neo4j-data-rest-0.0.1-SNAPSHOT.jar
```

### 애플리케이션 테스트
이제 애플리케이션이 실행 중이므로 테스트할 수 있습니다. 원하는 REST 클라이언트를 사용할 수 있습니다. 다음 예제에서는 curl이라는 *nix 도구를 사용합니다.

먼저 최상위 서비스를 보고 싶습니다. 다음 예제(출력 포함)에서는 이를 수행하는 방법을 보여줍니다.

```

입력 : $ curl http://localhost:8080

출력 : 
{
  "_links" : {
    "people" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
    }
  }
}
```

여기에서 이 서버가 제공하는 기능을 처음으로 엿볼 수 있습니다. http://localhost:8080/people 에 사람 링크가 있습니다. ?page, ?size 및 ?sort와 같은 몇 가지 옵션이 있습니다.

```
Spring Data REST는 JSON 출력에 HAL 형식을 사용합니다. 이는 유연하며 제공되는 데이터에 인접한 링크를 제공하는 편리한 방법을 제공합니다.
```
요청 : curl http://localhost:8080/people 을 했을 때 현재 데이터베이스에 담겨져 있는 데이터가 없어서 
```
{
  "_embedded" : {
    "people" : [ ]
  },
    "totalElements" : 0,
    "totalPages" : 0,
    "number" : 0
  }
}
```

이렇게 응답이 나왔습니다. 

```
요청 : 
$ curl -i -X POST -H "Content-Type:application/json" -d '{  "firstName" : "Frodo",  "lastName" : "Baggins" }' http://localhost:8080/people
```
POST 할 때 명령어 기능
* -i : 헤더를 포함한 응답 메시지를 볼 수 있도록 합니다. 새로 생성된 Person의 URI가 표시됩니다.
* -X : POST는 새 항목을 만드는 데 사용되는 POST 신호를 보냅니다.
* -H : "Content-Type:application/json"은 애플리케이션이 페이로드에 JSON 개체가 포함되어 있음을 알 수 있도록 콘텐츠 유형을 설정합니다.
* -d : '{ "firstName" : "Frodo", "lastName" : "Baggins" }'는 전송되는 데이터입니다.

```
응답 : 
HTTP/1.1 201 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Location: http://localhost:8080/people/0
Content-Type: application/hal+json
Transfer-Encoding: chunked
Date: Thu, 08 Jun 2023 04:44:41 GMT

{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/0"
    },
    "person" : {
      "href" : "http://localhost:8080/people/0"
    }
  }
}

```

POST로 데이터를 추가하면 firstName 과 lastName 을 가진 데이터가 하나 생성이 되었다는 것을 알 수 있었고 
다시 한번 curl http://localhost:8080/people 을 실행 해봤습니다.

```
요청 : $ curl http://localhost:8080/people

응답 :
{
  "_embedded" : {
    "people" : [ {
      "firstName" : "Frodo",
      "lastName" : "Baggins",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/people/0"
        },
        "person" : {
          "href" : "http://localhost:8080/people/0"
        }
      }
    } ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people?page=0&size=20"
    },
    "profile" : {
      "href" : "http://localhost:8080/profile/people"
    },
    "search" : {
      "href" : "http://localhost:8080/people/search"
    }
  },
    "size" : 20,
    "totalElements" : 1,
    "totalPages" : 1,
    "number" : 0
  }
}

```
다음과 같이 응답이 되는 부분을 알 수 있었습니다. 

people 개체에는 Frodo가 포함된 목록이 포함되어 있습니다. 자체 링크가 어떻게 포함되어 있는지 확인하십시오. Spring Data REST는 또한 Evo Inflector 라이브러리를 사용하여 그룹화를 위한 엔터티 이름을 복수화합니다.
```
요청 : $ curl http://localhost:8080/people/0

응답 :
{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/0"
    },
    "person" : {
      "href" : "http://localhost:8080/people/0"
    }
  }
}
```

이것은 순전히 웹 기반으로 보일 수 있지만 배후에는 내장된 Neo4j 그래프 데이터베이스가 있습니다. 프로덕션 환경에서는 아마도 독립형 Neo4j 서버에 연결할 것입니다.


이 가이드에는 도메인 개체가 하나만 있습니다. 도메인 개체가 서로 관련된 보다 복잡한 시스템에서 Spring Data REST는 연결된 레코드를 탐색하는 데 도움이 되는 추가 링크를 렌더링합니다.
```
{
  "_links" : {
    "findByLastName" : {
      "href" : "http://localhost:8080/people/search/findByLastName{?name}",
      "templated" : true
    },
    "self" : {
      "href" : "http://localhost:8080/people/search"
    }
  }
}

```

HTTP 쿼리 매개변수 이름을 포함하여 쿼리의 URL을 볼 수 있습니다. 이는 인터페이스에 포함된 @Param("name") 주석과 일치합니다.


findByLastName 쿼리를 사용하려면 다음 명령을 실행합니다.
```
요청 : $ curl http://localhost:8080/people/search/findByLastName?name=Baggins

응답 : 
{
  "_embedded" : {
    "people" : [ {
      "firstName" : "Frodo",
      "lastName" : "Baggins",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/people/0"
        },
        "person" : {
          "href" : "http://localhost:8080/people/0"
        }
      }
    } ]
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


#### PUT 요청
```
요청 : $ curl -X PUT -H "Content-Type:application/json" -d '{ "firstName": "Bilbo", "lastName": "Baggins" }' http://localhost:8080/people/0

응답 :
{
  "firstName" : "Bilbo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/0"
    },
    "person" : {
      "href" : "http://localhost:8080/people/0"
    }
  }
}

```

#### PATCH 요청

```
요청 : $ curl -X PATCH -H "Content-Type:application/json" -d '{ "firstName": "Bilbo Jr." }' http://localhost:8080/people/0

응답 :
{
  "firstName" : "Bilbo Jr.",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/0"
    },
    "person" : {
      "href" : "http://localhost:8080/people/0"
    }
  }
}

```

```
PUT은 전체 레코드를 대체합니다. 제공되지 않은 필드는 null로 대체됩니다. PATCH는 항목의 하위 집합을 업데이트하는 데 사용할 수 있습니다.
```


#### DELETE 요청
다음 예제(해당 출력과 함께 표시됨)와 같이 레코드를 삭제할 수도 있습니다.

```
요청 : $ curl -X DELETE http://localhost:8080/people/0

응답 :
{
  "firstName" : "Bilbo Jr.",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/0"
    },
    "person" : {
      "href" : "http://localhost:8080/people/0"
    }
  }
}

```

삭제 한 뒤 다시 실행
```
요청 : $  curl http://localhost:8080/people

응답 :
{
  "_embedded" : {
    "people" : [ ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people?page=0&size=20"
    },
    "profile" : {
      "href" : "http://localhost:8080/profile/people"
    },
    "search" : {
      "href" : "http://localhost:8080/people/search"
    }
  },
  "page" : {
    "size" : 20,
    "totalElements" : 0,
    "totalPages" : 0,
    "number" : 0
  }
}

```

잘 삭제가 된 것을 볼 수 있다.


이 하이퍼미디어 기반 인터페이스의 편리한 측면은 curl(또는 원하는 REST 클라이언트)을 사용하여 모든 RESTful 끝점을 검색할 수 있는 방법입니다. 고객과 공식적인 계약 또는 인터페이스 문서를 교환할 필요가 없습니다.