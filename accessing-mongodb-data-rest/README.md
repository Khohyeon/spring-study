## REST로 MongoDB 데이터에 액세스
이 가이드는 하이퍼미디어 기반 RESTful 프런트 엔드를 통해 문서 기반 데이터에 액세스하는 애플리케이션을 만드는 과정을 안내합니다 .

### 무엇을 만들 것인가
Spring 데이터 REST를 사용하여 MongoDB NoSQL 데이터베이스에 저장된 Person 개체를 생성하고 검색할 수 있는 Spring 애플리케이션을 빌드합니다. Spring Data REST는 Spring HATEOAS 및 Spring Data MongoDB의 기능을 가져와 자동으로 함께 결합합니다.

```
Spring Data REST는 또한 Spring Data JPA, Spring Data Gemfire 및 Spring Data Neo4j를 백엔드 데이터 저장소로 지원하지만 이 가이드의 일부는 아닙니다.
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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다. git clone https://github.com/spring-guides/gs-accessing-mongodb-data-rest.git

* gs-accessing-mongodb-data-rest/initial로 cd

* MongoDB 설치 및 실행으로 이동하십시오.

완료하면 gs-accessing-mongodb-data-rest/complete의 코드와 비교하여 결과를 확인할 수 있습니다.

### MongoDB 설치 및 실행
1. https://www.mongodb.com/try/download/enterprise 에서 MongoDB Enterprise Server를 다운로드하고 설치합니다.
2. 환경변수를 설정합니다.
3. localhost:27017 에서 MongoDB 실행합니다.
- It looks like you are trying to access MongoDB over HTTP on the native driver port. 이 메시지 나올 경우 실행 되고 있다고 생각하면 됩니다.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

https://start.spring.io 로 이동합니다. 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

종속성을 클릭하고 Rest Repositories 및 Spring Data MongoDB를 선택합니다.

생성을 클릭합니다.

선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 도메인 객체 생성
다음 예제(src/main/java/com/example/accessingmongodbdatarest/Person.java에 있음)에 표시된 것처럼 사람을 나타내는 새 도메인 개체를 만듭니다.

```java
public class Person {

	@Id private String id;

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
Person 개체에는 이름과 성이 있습니다. (자동으로 생성되도록 설정한 ID 객체도 있으니 처리할 필요는 없습니다.)

### PersonRepository 생성
다음으로 다음 목록(src/main/java/com/example/accessingmongodbdatarest/PersonRepository.java에 있음)에 표시된 것처럼 간단한 리포지토리를 만들어야 합니다.

```java
@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface PersonRepository extends MongoRepository<Person, String> {

	List<Person> findByLastName(@Param("name") String name);

}
```
이 리포지토리는 인터페이스이며 Person 개체와 관련된 다양한 작업을 수행할 수 있습니다. Spring Data Commons에 정의된 PagingAndSortingRepository 인터페이스를 차례로 확장하는 MongoRepository를 확장하여 이러한 작업을 가져옵니다.

런타임에 Spring Data REST는 이 인터페이스의 구현을 자동으로 생성합니다. 그런 다음 @RepositoryRestResource 주석을 사용하여 /people에서 RESTful 엔드포인트를 생성하도록 Spring MVC에 지시합니다.

```
리포지토리를 내보내는 데 @RepositoryRestResource가 필요하지 않습니다. 기본값인 /persons 대신 /people을 사용하는 등 내보내기 세부 정보를 변경하는 데에만 사용됩니다.
```

여기에서 lastName 값을 기반으로 Person 개체 목록을 검색하는 사용자 지정 쿼리도 정의했습니다. 이 가이드에서 더 자세히 호출하는 방법을 확인할 수 있습니다.

```
기본적으로 Spring Boot는 로컬로 호스팅되는 MongoDB 인스턴스에 연결을 시도합니다. 애플리케이션이 다른 곳에서 호스팅되는 MongoDB 인스턴스를 가리키는 방법에 대한 참조 문서를 읽으십시오.
```

@SpringBootApplication은 다음을 모두 추가하는 편리한 주석입니다.

* @Configuration: 애플리케이션 컨텍스트에 대한 빈 정의의 소스로 클래스에 태그를 지정합니다.

* @EnableAutoConfiguration: 클래스 경로 설정, 기타 빈 및 다양한 속성 설정을 기반으로 빈 추가를 시작하도록 Spring Boot에 지시합니다. 예를 들어 spring-webmvc가 클래스 경로에 있는 경우 이 주석은 애플리케이션을 웹 애플리케이션으로 플래그 지정하고 DispatcherServlet 설정과 같은 주요 동작을 활성화합니다.

* @ComponentScan: com/example 패키지에서 다른 구성 요소, 구성 및 서비스를 찾도록 Spring에 지시하여 컨트롤러를 찾도록 합니다.

main() 메서드는 Spring Boot의 SpringApplication.run() 메서드를 사용하여 애플리케이션을 시작합니다. XML이 한 줄도 없다는 사실을 눈치채셨나요? web.xml 파일도 없습니다. 이 웹 애플리케이션은 100% 순수 Java이며 배관이나 인프라 구성을 처리할 필요가 없습니다.

### 실행 가능한 JAR 빌드
Gradle 또는 Maven을 사용하여 명령줄에서 애플리케이션을 실행할 수 있습니다. 필요한 모든 종속성, 클래스 및 리소스를 포함하는 단일 실행 가능 JAR 파일을 빌드하고 실행할 수도 있습니다. 실행 가능한 jar을 빌드하면 개발 수명 주기 전체, 다양한 환경 등에 서비스를 애플리케이션으로 쉽게 제공, 버전 지정 및 배포할 수 있습니다.

Gradle을 사용하는 경우 ./gradlew bootRun을 사용하여 애플리케이션을 실행할 수 있습니다. 또는 다음과 같이 ./gradlew build를 사용하여 JAR 파일을 빌드한 다음 JAR 파일을 실행할 수 있습니다.

```
java -jar build/libs/accessing-mongodb-data-rest-0.0.1-SNAPSHOT.jar
```
로깅 출력이 표시됩니다. 서비스가 몇 초 내에 시작되어 실행되어야 합니다.

### 애플리케이션 테스트
이제 애플리케이션이 실행 중이므로 테스트할 수 있습니다. 원하는 REST 클라이언트를 사용할 수 있습니다. 다음 예제에서는 *nix 도구 curl을 사용합니다.

먼저 다음 예제와 같이 최상위 서비스를 확인하려고 합니다.

```
요청 : $ curl http://localhost:8080

응답 :
{
  "_links" : {
    "people" : {
      "href" : "http://localhost:8080/people{?page,size,sort}",
      "templated" : true
    },
    "profile" : {
      "href" : "http://localhost:8080/profile"
    }
  }
}

```

앞의 예는 이 서버가 제공해야 하는 것을 처음으로 보여줍니다. http://localhost:8080/people에 사람 링크가 있습니다. ?page, ?size 및 ?sort와 같은 몇 가지 옵션이 있습니다.

```

Spring Data REST는 JSON 출력에 HAL 형식을 사용합니다. 이는 유연하며 제공되는 데이터에 인접한 링크를 제공하는 편리한 방법을 제공합니다.
```

people 링크를 사용하면 데이터베이스에서 Person 레코드를 볼 수 있습니다(현재 없음).

```
요청 : $ curl http://localhost:8080/people

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

현재 요소가 없으므로 페이지가 없습니다. 새로운 사람을 만들 때입니다!

```
이 가이드를 여러 번 실행하면 데이터가 남을 수 있습니다. 새로 시작해야 하는 경우 데이터베이스를 찾아 삭제하는 명령은 MongoDB 셸 빠른 참조를 참조하세요.
```

다음 명령은 "Frodo Baggins"라는 사람을 만듭니다.
```
요청 : $ curl -i -X POST -H "Content-Type:application/json" -d "{  \"firstName\" : \"Frodo\",  \"lastName\" : \"Baggins\" }" http://localhost:8080/people
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
Location: http://localhost:8080/people/6481859e1e86895e65d1c049
Content-Type: application/hal+json
Transfer-Encoding: chunked
Date: Thu, 08 Jun 2023 07:39:10 GMT

{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
    },
    "person" : {
      "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
    }
  }
}

```

```
이전 POST 작업에 Location 헤더가 어떻게 포함되어 있는지 확인하십시오. 여기에는 새로 생성된 리소스의 URI가 포함됩니다. Spring Data REST에는 방금 생성/업데이트된 리소스의 표현을 즉시 반환하도록 프레임워크를 구성하는 데 사용할 수 있는 두 가지 메서드(RepositoryRestConfiguration.setReturnBodyOnCreate(…) 및 setReturnBodyOnUpdate(…))도 있습니다.
```
여기에서 다음 예제와 같이 모든 사람을 쿼리할 수 있습니다.

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
          "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
        },
        "person" : {
          "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
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
  "page" : {
    "size" : 20,
    "totalElements" : 1,
    "totalPages" : 1,
    "number" : 0
  }
}

```

people 개체에는 Frodo가 포함된 목록이 포함되어 있습니다. 자체 링크가 어떻게 포함되어 있는지 확인하십시오. Spring Data REST는 또한 Evo Inflector를 사용하여 그룹화를 위한 엔터티 이름을 복수화합니다.

다음 예제와 같이 개별 레코드를 직접 쿼리할 수 있습니다.
```
요청 : $ curl http://localhost:8080/people/6481859e1e86895e65d1c049

응답 :
{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
    },
    "person" : {
      "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
    }
  }
}

```
```
이것은 순전히 웹 기반으로 보일 수 있지만 뒤에서는 사용자가 시작한 MongoDB 데이터베이스와 통신하고 있습니다.
```

이 가이드에는 도메인 개체가 하나만 있습니다. 도메인 개체가 서로 관련된 보다 복잡한 시스템에서 Spring Data REST는 연결된 레코드를 탐색하는 데 도움이 되는 추가 링크를 렌더링합니다.

다음 예제와 같이 모든 사용자 지정 쿼리를 찾습니다.

```
$ curl http://localhost:8080/people/search
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
          "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
        },
        "person" : {
          "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
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

PUT, PATCH 및 DELETE REST 호출을 실행하여 각각 기존 레코드를 교체, 업데이트 또는 삭제할 수도 있습니다. 다음 예제에서는 PUT 호출을 사용합니다.

#### PUT 요청

```
요청 : $ curl -X PUT -H "Content-Type:application/json" -d "{ \"firstName\": \"Bilbo\", \"lastName\": \"Baggins\" }" http://localhost:8080/people/6481859e1e86895e65d1c049

응답 :
{
  "firstName" : "Bilbo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
    },
    "person" : {
      "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
    }
  }
}
```

다음 예에서는 PATCH 호출을 사용합니다.

#### PATCH 요청

```
요청 : $ curl -X PATCH -H "Content-Type:application/json" -d "{ \"firstName\": \"Bilbo Jr.\" }" http://localhost:8080/people/6481859e1e86895e65d1c049

응답 :
{
  "firstName" : "Bilbo Jr.",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
    },
    "person" : {
      "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
    }
  }
}
```

```
PUT은 전체 레코드를 대체합니다. 제공되지 않은 필드는 null로 대체됩니다. PATCH를 사용하여 항목의 하위 집합을 업데이트할 수 있습니다.
```


다음 예와 같이 레코드를 삭제할 수도 있습니다.
#### DELETE 요청

```
요청 : $ curl -X DELETE http://localhost:8080/people/6481859e1e86895e65d1c049

응답 :
{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
    },
    "person" : {
      "href" : "http://localhost:8080/people/6481859e1e86895e65d1c049"
    }
  }
}
```

이제 데이터베이스에는 레코드가 없습니다.

```
요청 : $ curl http://localhost:8080/people

응답 : 
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

이 하이퍼미디어 기반 인터페이스의 편리한 측면은 curl(또는 원하는 REST 클라이언트)을 사용하여 모든 RESTful 끝점을 검색할 수 있는 방법입니다. 고객과 공식적인 계약 또는 인터페이스 문서를 교환할 필요가 없습니다.
