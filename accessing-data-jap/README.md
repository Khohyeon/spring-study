## JPA로 데이터 액세스
이 가이드는 Spring Data JPA를 사용하여 관계형 데이터베이스에서 데이터를 저장하고 검색하는 애플리케이션을 구축하는 과정을 안내합니다.

### 무엇을 만들 것인가
Customer메모리 기반 데이터베이스에 POJO(Plain Old Java Objects)를 저장하는 애플리케이션을 빌드합니다 .

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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-accessing-data-jpa.git

* cd 로gs-accessing-data-jpa/initial

* Simple Entity 정의 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-accessing-data-jpa/complete.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.
2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.
3. 종속성을 클릭 하고 Spring Data JPA를 선택한 다음 H2 Database를 선택합니다 .
4. 생성 을 클릭합니다 .
5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 단순 엔터티 정의
이 예제에서는 각각 JPA 엔터티로 주석이 달린 Customer 개체를 저장합니다. 다음 목록은 Customer 클래스(src/main/java/com/example/accessingdatajpa/Customer.java에 있음)를 보여줍니다.

```java
@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String firstName;
	private String lastName;

	protected Customer() {}

	public Customer(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return String.format(
				"Customer[id=%d, firstName='%s', lastName='%s']",
				id, firstName, lastName);
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
}
```

- 여기에는 id, firstName 및 lastName의 세 가지 속성이 있는 Customer 클래스가 있습니다. 두 개의 생성자도 있습니다. 기본 생성자는 JPA를 위해서만 존재합니다. 직접 사용하지 않으므로 보호 대상으로 지정됩니다. 다른 생성자는 데이터베이스에 저장할 Customer 인스턴스를 만드는 데 사용하는 생성자입니다.
- Customer 클래스는 JPA 엔터티임을 나타내는 @Entity로 주석 처리됩니다. (@Table 주석이 없기 때문에 이 엔터티는 Customer라는 테이블에 매핑된 것으로 가정합니다.)
- Customer 객체의 id 속성은 JPA가 객체의 ID로 인식할 수 있도록 @Id로 주석 처리됩니다. id 속성에는 @GeneratedValue 주석이 추가되어 ID가 자동으로 생성되어야 함을 나타냅니다.
- 다른 두 속성인 firstName과 lastName은 주석이 없는 상태로 남습니다. 속성 자체와 동일한 이름을 공유하는 열에 매핑된다고 가정합니다.
- 편리한 toString() 메서드는 고객의 속성을 출력합니다.

### 간단한 쿼리 만들기

- JPA를 사용하여 관계형 데이터베이스에 데이터를 저장하는 데 중점을 둡니다. 가장 매력적인 기능은 저장소 인터페이스에서 런타임에 자동으로 저장소 구현을 생성하는 기능입니다.

- 이것이 어떻게 작동하는지 보려면 다음 목록(src/main/java/com/example/accessingdatajpa/CustomerRepository.java에 있음)에 표시된 대로 Customer 엔터티와 함께 작동하는 리포지토리 인터페이스를 만듭니다.

```java

public interface CustomerRepository extends CrudRepository<Customer, Long> {

  List<Customer> findByLastName(String lastName);

  Customer findById(long id);
}
```

- CustomerRepository는 CrudRepository 인터페이스를 확장합니다. 함께 작동하는 엔터티 유형 및 ID(Customer 및 Long)는 CrudRepository의 일반 매개변수에 지정됩니다. CrudRepository를 확장함으로써 CustomerRepository는 Customer 엔터티를 저장, 삭제 및 찾는 방법을 포함하여 Customer 지속성 작업을 위한 여러 방법을 상속합니다.
- Spring Data JPA를 사용하면 메서드 서명을 선언하여 다른 쿼리 메서드를 정의할 수도 있습니다. 예를 들어 CustomerRepository에는 findByLastName() 메서드가 포함되어 있습니다.
- 일반적인 Java 애플리케이션에서는 CustomerRepository를 구현하는 클래스를 작성할 것으로 예상할 수 있습니다. 그러나 이것이 Spring Data JPA를 강력하게 만드는 이유입니다. 저장소 인터페이스의 구현을 작성할 필요가 없습니다. Spring Data JPA는 애플리케이션을 실행할 때 구현을 생성합니다.
- 이제 이 예제를 연결하고 어떻게 보이는지 확인할 수 있습니다!

### CustomerRepository 와 JPARepository의 차이점
- JPARepository는 CrudRepository를 확장합니다. CrudRepository는 CRUD 기능을 제공합니다.
- findByFirstName(String firstName)와 같은 메서드를 선언하면, firstName 필드를 기준으로 데이터를 조회할 수 있습니다. CrudRepository는 기본적인 CRUD 작업을 위한 메서드를 제공하지만, 자동 생성된 쿼리 메서드를 사용하지 않습니다.

### 애플리케이션 클래스 생성

Spring Initializr는 애플리케이션을 위한 간단한 클래스를 생성합니다. 다음 목록은 Initializr가 이 예제에 대해 생성한 클래스를 보여줍니다(src/main/java/com/example/accessingdatajpa/AccessingDataJpaApplication.java).

```java
@SpringBootApplication
public class AccessingDataJpaApplication {

	private static final Logger log = LoggerFactory.getLogger(AccessingDataJpaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AccessingDataJpaApplication.class);
	}

	@Bean
	public CommandLineRunner demo(CustomerRepository repository) {
		return (args) -> {
			// save a few customers
			repository.save(new Customer("Jack", "Bauer"));
			repository.save(new Customer("Chloe", "O'Brian"));
			repository.save(new Customer("Kim", "Bauer"));
			repository.save(new Customer("David", "Palmer"));
			repository.save(new Customer("Michelle", "Dessler"));

			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Customer customer : repository.findAll()) {
				log.info(customer.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			Customer customer = repository.findById(1L);
			log.info("Customer found with findById(1L):");
			log.info("--------------------------------");
			log.info(customer.toString());
			log.info("");

			// fetch customers by last name
			log.info("Customer found with findByLastName('Bauer'):");
			log.info("--------------------------------------------");
			repository.findByLastName("Bauer").forEach(bauer -> {
				log.info(bauer.toString());
			});
			// for (Customer bauer : repository.findByLastName("Bauer")) {
			// 	log.info(bauer.toString());
			// }
			log.info("");
		};
	}

}
```

AccessingDataJpaApplication 클래스에는 몇 가지 테스트를 통해 CustomerRepository를 배치하는 demo() 메서드가 포함되어 있습니다. 
1. Spring 애플리케이션 컨텍스트에서 CustomerRepository를 가져옵니다.
2. 소수의 Customer 개체를 저장하고 save() 메서드를 시연하고 작업할 일부 데이터를 설정합니다.
3. findAll()을 호출하여 데이터베이스에서 모든 Customer 개체를 가져옵니다.
4. findById()를 호출하여 해당 ID로 단일 고객을 가져옵니다. 
5. findByLastName()을 호출하여 성이 "Bauer"인 모든 고객을 찾습니다.
demo() 메서드는 애플리케이션이 시작될 때 자동으로 코드를 실행하는 CommandLineRunner 빈을 반환합니다.

```
기본적으로 Spring Boot는 JPA 저장소 지원을 활성화하고 @SpringBootApplication이 있는 패키지(및 해당 하위 패키지)를 찾습니다. 
구성에 표시되지 않는 패키지에 JPA 저장소 인터페이스 정의가 있는 경우 @EnableJpaRepositories 및 유형이 안전한 basePackageClasses=MyRepository.class 매개변수를 사용하여 대체 패키지를 지정할 수 있습니다.
```

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/accessing-data-jpa-0.1.0.jar
```

애플리케이션을 실행하면 다음과 유사한 출력이 표시됩니다.

#### findAll()
```
Customers found with findAll():
-------------------------------
Customer[id=1, firstName='Jack', lastName='Bauer']
Customer[id=2, firstName='Chloe', lastName='O'Brian']
Customer[id=3, firstName='Kim', lastName='Bauer']
Customer[id=4, firstName='David', lastName='Palmer']
Customer[id=5, firstName='Michelle', lastName='Dessler']
```

#### findById()
```
Customer found with findById(1L):
--------------------------------
Customer[id=1, firstName='Jack', lastName='Bauer']
```

#### findByLastName('Bauer')
```
Customer found with findByLastName('Bauer'):
--------------------------------------------
Customer[id=1, firstName='Jack', lastName='Bauer']
Customer[id=3, firstName='Kim', lastName='Bauer']
```
