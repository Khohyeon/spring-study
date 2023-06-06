## REST로 JPA 데이터에 액세스
이 가이드는 하이퍼미디어 기반 RESTful 프런트 엔드를 통해 관계형 JPA 데이터에 액세스하는 애플리케이션을 만드는 과정을 안내합니다.

### 무엇을 만들 것인가
Spring 데이터 REST를 사용하여 데이터베이스에 저장된 Person 개체를 생성하고 검색할 수 있는 Spring 애플리케이션을 빌드합니다. 
Spring Data REST는 Spring HATEOAS 및 Spring Data JPA의 기능을 가져와 자동으로 함께 결합합니다.
```
Spring Data REST는 또한 Spring Data Neo4j, Spring Data Gemfire 및 Spring Data MongoDB를 백엔드 데이터 저장소로 지원하지만 이 가이드의 일부는 아닙니다.
```

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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다. git clone https://github.com/spring-guides/gs-accessing-data-rest.git

* accessing-data-rest/initial로 cd

* 도메인 개체 만들기로 이동합니다.

완료하면 gs-accessing-data-rest/complete의 코드와 비교하여 결과를 확인할 수 있습니다.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io로 이동합니다. 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭하고 Rest Repositories, Spring Data JPA 및 H2 데이터베이스를 선택합니다.

4. 생성을 클릭합니다.

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 도메인 개체 만들기
다음 목록(src/main/java/com/example/accessingdatarest/Person.java에 있음)에 표시된 것처럼 사람을 나타내는 새 도메인 개체를 만듭니다.

```java
@Entity
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

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

Person 개체에는 이름과 성이 있습니다. (자동으로 생성되도록 설정한 ID 객체도 있으니 따로 다루지 않아도 됩니다.)

### 개인 저장소 생성
다음으로 다음 목록(src/main/java/com/example/accessingdatarest/PersonRepository.java에 있음)에 표시된 것처럼 간단한 리포지토리를 만들어야 합니다.
```java
@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface PersonRepository extends PagingAndSortingRepository<Person, Long>, CrudRepository<Person, Long> {
    
    List<Person> findByLastName(@Param("name") String lastName);
}

```

이 Repository는 Person 개체와 관련된 다양한 작업을 수행할 수 있는 인터페이스입니다. Spring Data Commons에 정의된 PagingAndSortingRepository 인터페이스를 확장하여 이러한 작업을 가져옵니다.

런타임에 Spring Data REST는 이 인터페이스의 구현을 자동으로 생성합니다. 그런 다음 @RepositoryRestResource 주석을 사용하여 /people에서 RESTful 엔드포인트를 생성하도록 Spring MVC에 지시합니다.\

```
Repository를 내보내는 데 @RepositoryRestResource가 필요하지 않습니다. 기본값인 /persons 대신 /people을 사용하는 등 내보내기 세부 정보를 변경하는 데에만 사용됩니다.
```

Spring Boot는 Spring Data JPA를 자동으로 가동하여 PersonRepository의 구체적인 구현을 생성하고 JPA를 사용하여 백엔드 인 메모리 데이터베이스와 통신하도록 구성합니다.

Spring Data REST는 Spring MVC 위에 구축됩니다. RESTful 프런트 엔드를 제공하기 위해 Spring MVC 컨트롤러, JSON 변환기 및 기타 빈 모음을 생성합니다. 이러한 구성 요소는 Spring Data JPA 백엔드에 연결됩니다. Spring Boot를 사용하면 이 모든 것이 자동으로 구성됩니다. 작동 방식을 조사하려면 Spring Data REST에서 RepositoryRestMvcConfiguration을 살펴보십시오.

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/accessing-data-rest-0.1.0.jar
```

로깅 출력이 표시됩니다. 서비스가 몇 초 내에 시작되어 실행되어야 합니다.

### 애플리케이션 테스트
이제 애플리케이션이 실행 중이므로 테스트할 수 있습니다. 원하는 REST 클라이언트를 사용할 수 있습니다. 다음 예제에서는 *nix 도구인 curl을 사용합니다.

먼저 최상위 서비스를 보고 싶습니다. 다음 예에서는 이를 수행하는 방법을 보여줍니다.

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
앞의 예는 이 서버가 제공해야 하는 것을 처음으로 보여줍니다. http://localhost:8080/people에 사람 링크가 있습니다. ?page, ?size 및 ?sort와 같은 몇 가지 옵션이 있습니다.

```
Spring Data REST는 JSON 출력에 HAL 형식을 사용합니다. 이는 유연하며 제공되는 데이터에 인접한 링크를 제공하는 편리한 방법을 제공합니다.
```

```
입력: $ curl http://localhost:8080/people

출력:
{
  "_embedded" : {
    "people" : []
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
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

현재 요소가 없으므로 페이지가 없습니다. 새로운 사람을 만들 시간입니다! 다음 목록은 그렇게 하는 방법을 보여줍니다.
```
입력 : $ curl -i -H "Content-Type:application/json" -d '{"firstName": "Frodo", "lastName": "Baggins"}' http://localhost:8080/people

출력 : 
HTTP/1.1 201 Created
Server: Apache-Coyote/1.1
Location: http://localhost:8080/people/1
Content-Length: 0
Date: Wed, 26 Feb 2014 20:26:55 GMT
```

-i: 헤더를 포함한 응답 메시지를 볼 수 있는지 확인합니다. 새로 생성된 Person의 URI가 표시됩니다.

-H "Content-Type:application/json": 애플리케이션이 페이로드에 JSON 개체가 포함되어 있음을 알 수 있도록 콘텐츠 유형을 설정합니다.

-d '{"firstName": "Frodo", "lastName": "Baggins"}': 전송 중인 데이터입니다.

Windows를 사용 중인 경우 위의 명령이 WSL에서 작동합니다. WSL을 설치할 수 없는 경우 작은따옴표를 큰따옴표로 바꾸고 기존 큰따옴표를 이스케이프 처리해야 할 수 있습니다(예: -d "{\"firstName\": \"Frodo\", \"lastName\": \). "Baggins\"}".

```
POST 작업에 대한 응답에 Location 헤더가 어떻게 포함되는지 확인하십시오. 여기에는 새로 생성된 리소스의 URI가 포함됩니다. Spring Data REST에는 방금 생성된 리소스의 표현을 즉시 반환하도록 프레임워크를 구성하는 데 사용할 수 있는 두 가지 메서드(RepositoryRestConfiguration.setReturnBodyOnCreate(…) 및 setReturnBodyOnUpdate(…))도 있습니다. RepositoryRestConfiguration.setReturnBodyForPutAndPost(…)는 생성 및 업데이트 작업에 대한 표현 응답을 활성화하는 바로 가기 메서드입니다.
```

다음 예제와 같이 모든 사람에 대해 쿼리할 수 있습니다.

```
$ curl http://localhost:8080/people
{
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
    },
    "search" : {
      "href" : "http://localhost:8080/people/search"
    }
  },
  "_embedded" : {
    "people" : [ {
      "firstName" : "Frodo",
      "lastName" : "Baggins",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/people/1"
        }
      }
    } ]
  },
  "page" : {
    "size" : 20,
    "totalElements" : 1,
    "totalPages" : 1,
    "number" : 0
  }
}
```


people 개체에는 Frodo가 포함된 목록이 포함되어 있습니다. 자체 링크가 어떻게 포함되어 있는지 확인하십시오. Spring Data REST는 또한 Evo Inflector를 사용하여 그룹화를 위한 엔터티 이름을 복수화합니다.

다음과 같이 개별 레코드를 직접 쿼리할 수 있습니다.

```
$ curl http://localhost:8080/people/1
{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/1"
    }
  }
}
```

이것은 순전히 웹 기반으로 보일 수 있습니다. 그러나 배후에는 H2 관계형 데이터베이스가 있습니다. 프로덕션에서는 아마도 PostgreSQL과 같은 실제 것을 사용할 것입니다.

이 가이드에는 도메인 개체가 하나만 있습니다. 도메인 개체가 서로 관련된 보다 복잡한 시스템에서 Spring Data REST는 연결된 레코드를 탐색하는 데 도움이 되는 추가 링크를 렌더링합니다.

다음 예와 같이 모든 사용자 지정 쿼리를 찾을 수 있습니다.

```
$ curl http://localhost:8080/people/search
{
  "_links" : {
    "findByLastName" : {
      "href" : "http://localhost:8080/people/search/findByLastName{?name}",
      "templated" : true
    }
  }
}
```

HTTP 쿼리 매개변수 이름을 포함하여 쿼리의 URL을 볼 수 있습니다. 이는 인터페이스에 포함된 @Param("name") 주석과 일치합니다.

다음 예에서는 findByLastName 쿼리를 사용하는 방법을 보여줍니다.

```
$ curl http://localhost:8080/people/search/findByLastName?name=Baggins
{
  "_embedded" : {
    "persons" : [ {
      "firstName" : "Frodo",
      "lastName" : "Baggins",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/people/1"
        }
      }
    } ]
  }
}
```

- 코드에서 List<Person>을 반환하도록 정의했으므로 모든 결과를 반환합니다. Person만 반환하도록 정의한 경우 반환할 Person 개체 중 하나를 선택합니다. 
이것은 예측할 수 없기 때문에 여러 항목을 반환할 수 있는 쿼리에 대해서는 그렇게 하고 싶지 않을 것입니다.

- PUT, PATCH 및 DELETE REST 호출을 실행하여 기존 레코드를 교체, 업데이트 또는 삭제할 수 있습니다(각각). 다음 예제에서는 PUT 호출을 사용합니다.
```
$ curl -X PUT -H "Content-Type:application/json" -d '{"firstName": "Bilbo", "lastName": "Baggins"}' http://localhost:8080/people/1
$ curl http://localhost:8080/people/1
{
  "firstName" : "Bilbo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/1"
    }
  }
}
```

다음 예에서는 PATCH 호출을 사용합니다.

```
두개의 입력을 입력했을 때 같은 응답이 나옴
$ curl -X PATCH -H "Content-Type:application/json" -d '{"firstName": "Bilbo Jr."}' http://localhost:8080/people/1
$ curl http://localhost:8080/people/1
{
  "firstName" : "Bilbo Jr.",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/1"
    }
  }
}
```
```
PUT은 전체 레코드를 대체합니다. 제공되지 않은 필드는 null로 대체됩니다. PATCH를 사용하여 항목의 하위 집합을 업데이트할 수 있습니다.
```

다음 예와 같이 레코드를 삭제할 수도 있습니다.

```
$ curl -X DELETE http://localhost:8080/people/1
$ curl http://localhost:8080/people
{
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
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
이 하이퍼미디어 기반 인터페이스의 편리한 측면은 curl(또는 원하는 REST 클라이언트)을 사용하여 모든 RESTful 끝점을 검색할 수 있다는 것입니다. 
고객과 공식적인 계약 또는 인터페이스 문서를 교환할 필요가 없습니다.



