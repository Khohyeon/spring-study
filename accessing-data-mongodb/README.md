## MongoDB로 데이터 액세스
이 가이드는 문서 기반 데이터베이스인 MongoDB 에서 데이터를 저장하고 검색하는 애플리케이션을 빌드하기 위해 Spring Data MongoDB를 사용하는 프로세스를 안내합니다 .

### 무엇을 만들 것인가
Spring Data MongoDB를 사용하여 MongoDB 데이터베이스에 Customer POJO(Plain Old Java Objects)를 저장합니다.

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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-accessing-data-mongodb.git

* cd 로gs-accessing-data-mongodb/initial

* MongoDB 설치 및 실행 으로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-accessing-data-mongodb/complete.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.
2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.
3. 종속성을 클릭 하고 Spring Data MongoDB를 선택합니다 .
4. 생성 을 클릭합니다 .
5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.


### 단순 엔터티 정의

MongoDB는 NoSQL 문서 저장소입니다. 이 예제에서는 고객 개체를 저장합니다. 다음 목록은 Customer 클래스를 보여줍니다(src/main/java/com/example/accessingdatamongodb/Customer.java에 있음).

```java
public class Customer {

  @Id
  public String id;

  public String firstName;
  public String lastName;

  public Customer() {}

  public Customer(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @Override
  public String toString() {
    return String.format(
        "Customer[id=%s, firstName='%s', lastName='%s']",
        id, firstName, lastName);
  }

}
```


여기에는 id, firstName 및 lastName의 세 가지 속성이 있는 Customer 클래스가 있습니다. ID는 주로 MongoDB에서 내부용으로 사용됩니다. 또한 새 인스턴스를 만들 때 엔터티를 채우는 단일 생성자가 있습니다.

```
이 가이드에서는 간결함을 위해 일반적인 게터와 세터를 생략했습니다.
```

- id는 MongoDB ID의 표준 이름에 적합하므로 Spring Data MongoDB에 태그를 지정하기 위해 특별한 주석이 필요하지 않습니다.
- 다른 두 속성인 firstName과 lastName은 주석이 없는 상태로 남습니다. 속성 자체와 동일한 이름을 공유하는 필드에 매핑된다고 가정합니다.
- 편리한 toString() 메서드는 고객에 대한 세부 정보를 출력합니다.

```
MongoDB는 컬렉션에 데이터를 저장합니다. Spring Data MongoDB는 Customer 클래스를 customer라는 컬렉션에 매핑합니다. 컬렉션의 이름을 변경하려면 클래스에서 Spring Data MongoDB의 @Document 주석을 사용할 수 있습니다.
```

### 간단한 쿼리 만들기
- Spring Data MongoDB는 MongoDB에 데이터를 저장하는 데 중점을 둡니다. 또한 쿼리 파생 기능과 같은 Spring Data Commons 프로젝트의 기능을 상속합니다. 기본적으로 MongoDB의 쿼리 언어를 배울 필요가 없습니다. 소수의 메서드를 작성할 수 있으며 쿼리가 자동으로 작성됩니다.
- 이것이 어떻게 작동하는지 확인하려면 다음 목록(src/main/java/com/example/accessingdatamongodb/CustomerRepository.java에 있음)에 표시된 대로 고객 문서를 쿼리하는 리포지토리 인터페이스를 만듭니다.

```java

public interface CustomerRepository extends MongoRepository<Customer, String> {

  public Customer findByFirstName(String firstName);
  public List<Customer> findByLastName(String lastName);

}
```
- CustomerRepository는 MongoRepository 인터페이스를 확장하고 작동하는 값 유형 및 ID(각각 고객 및 문자열)를 연결합니다. 이 인터페이스는 표준 CRUD 작업(만들기, 읽기, 업데이트 및 삭제)을 비롯한 많은 작업과 함께 제공됩니다.

- 메서드 서명을 선언하여 다른 쿼리를 정의할 수 있습니다. 이 경우 기본적으로 Customer 유형의 문서를 찾고 firstName과 일치하는 문서를 찾는 findByFirstName을 추가하십시오.

- 또한 성으로 사람 목록을 찾는 findByLastName도 있습니다.

- 일반적인 Java 애플리케이션에서는 CustomerRepository를 구현하는 클래스를 작성하고 직접 쿼리를 작성합니다. Spring Data MongoDB를 매우 유용하게 만드는 것은 이 구현을 생성할 필요가 없다는 사실입니다. Spring Data MongoDB는 애플리케이션을 실행할 때 즉시 생성합니다.

- 이제 이 애플리케이션을 연결하고 어떻게 보이는지 확인할 수 있습니다!

MongoRepository 와 JPARepository 의 차이점
- MongoRepository 는 NOSQL인 MongoDB를 사용하기 위한 Repository 이다.
- JPARepository 는 RDBMS인 MySQL, Oracle 등 관계형 데이터베이스를 사용하기 위한 Repository 이다.
- MongoRepository는 문서(Document)를 저장하고 조회하기 위해 MongoDB의 자체 API를 사용합니다. 
- JpaRepository는 JPA의 Entity Manager를 사용하여 관계형 데이터베이스와 상호작용합니다.

### 애플리케이션 클래스 생성
Spring Initializr는 애플리케이션을 위한 간단한 클래스를 생성합니다. 다음 목록은 이 예제에 대해 Initializr가 만든 클래스를 보여줍니다(src/main/java/com/example/accessingdatamongodb/AccessingDataMongodbApplication.java).

```java
@SpringBootApplication
public class AccessingDataMongodbApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccessingDataMongodbApplication.class, args);
  }

}
```

Spring Boot는 @SpringBootApplication 클래스의 동일한 패키지(또는 하위 패키지)에 포함된 리포지토리를 자동으로 처리합니다. 등록 프로세스에 대한 더 많은 제어를 위해 @EnableMongoRepositories 주석을 사용할 수 있습니다.
```
기본적으로 @EnableMongoRepositories는 Spring Data의 리포지토리 인터페이스 중 하나를 확장하는 모든 인터페이스에 대해 현재 패키지를 스캔합니다. 프로젝트 레이아웃에 여러 프로젝트가 있고 리포지토리를 찾지 못하는 경우 basePackageClasses=MyRepository.class를 사용하여 Spring Data MongoDB에 유형별로 다른 루트 패키지를 스캔하도록 안전하게 지시할 수 있습니다.
```

Spring Data MongoDB는 MongoTemplate을 사용하여 find* 메서드 뒤의 쿼리를 실행합니다. 보다 복잡한 쿼리에 템플릿을 직접 사용할 수 있지만 이 가이드에서는 이에 대해 다루지 않습니다. (Spring Data MongoDB 참조 가이드 참조)

이제 Initializr가 생성한 간단한 클래스를 수정해야 합니다. 일부 데이터를 설정하고 이를 사용하여 출력을 생성해야 합니다. 다음 목록은 완료된 AccessingDataMongodbApplication 클래스(src/main/java/com/example/accessingdatamongodb/AccessingDataMongodbApplication.java에 있음)를 보여줍니다.

```java
@SpringBootApplication
public class AccessingDataMongodbApplication implements CommandLineRunner {

  @Autowired
  private CustomerRepository repository;

  public static void main(String[] args) {
    SpringApplication.run(AccessingDataMongodbApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

    repository.deleteAll();

    // save a couple of customers
    repository.save(new Customer("Alice", "Smith"));
    repository.save(new Customer("Bob", "Smith"));

    // fetch all customers
    System.out.println("Customers found with findAll():");
    System.out.println("-------------------------------");
    for (Customer customer : repository.findAll()) {
      System.out.println(customer);
    }
    System.out.println();

    // fetch an individual customer
    System.out.println("Customer found with findByFirstName('Alice'):");
    System.out.println("--------------------------------");
    System.out.println(repository.findByFirstName("Alice"));

    System.out.println("Customers found with findByLastName('Smith'):");
    System.out.println("--------------------------------");
    for (Customer customer : repository.findByLastName("Smith")) {
      System.out.println(customer);
    }

  }

}
```

AccessingDataMongodbApplication에는 CustomerRepository 인스턴스를 자동 연결하는 main() 메서드가 포함되어 있습니다. Spring Data MongoDB는 프록시를 동적으로 생성하고 거기에 주입합니다. 몇 가지 테스트를 통해 CustomerRepository를 사용합니다. 먼저 소수의 Customer 개체를 저장하고 save() 메서드를 시연하고 사용할 일부 데이터를 설정합니다. 다음으로 findAll()을 호출하여 데이터베이스에서 모든 Customer 개체를 가져옵니다. 그런 다음 그녀의 이름으로 단일 고객을 가져오기 위해 findByFirstName()을 호출합니다. 마지막으로 findByLastName()을 호출하여 성이 Smith인 모든 고객을 찾습니다.

```
기본적으로 Spring Boot는 로컬로 호스팅되는 MongoDB 인스턴스에 연결을 시도합니다. 애플리케이션이 다른 곳에서 호스팅되는 MongoDB 인스턴스를 가리키는 방법에 대한 자세한 내용은 참조 문서를 읽어보세요.
```

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/accessing-data-mongodb-0.1.0.jar
```


AccessingDataMongodbApplication은 CommandLineRunner를 구현하므로 Spring Boot가 시작될 때 run 메서드가 자동으로 호출됩니다. 다음과 같이 표시되어야 합니다(쿼리와 같은 다른 출력 포함).