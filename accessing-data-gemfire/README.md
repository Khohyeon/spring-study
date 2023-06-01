## Pivotal GemFire에서 데이터 액세스
이 가이드는 Apache Geode 데이터 관리 시스템 애플리케이션을 구축하는 과정을 안내합니다 .

### 무엇을 만들 것인가
Apache Geode용 Spring Data를 사용하여 POJO를 저장하고 검색합니다.

### 필요한 것
* 약 15분
* 선호하는 텍스트 편집기 또는 IDE
* 자바 1.8 이상
* Gradle 7.5+ 또는 Maven 3.5+
* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작 하려면 Spring Initializr로 시작하기 로 이동하십시오 .

기본 사항을 건너뛰 려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-accessing-data-gemfire.git
* cd 로 gs-accessing-data-gemfire/initial
* Simple Entity 정의 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-accessing-data-gemfire/complete.

### 스프링 이니셜라이저로 시작하기
모든 Spring 애플리케이션의 경우 Spring Initializr 로 시작해야 합니다 . Spring Initializr는 애플리케이션에 필요한 모든 종속성을 가져오는 빠른 방법을 제공하고 많은 설정을 수행합니다. 이 예제에는 Apache Geode 종속성을 위한 Spring이 필요합니다.

이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:
1. 웹 브라우저에서 https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.
2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.
3. 종속성을 클릭 하고 Apache Geode용 Spring을 선택합니다 .
4. 생성 을 클릭합니다 .
5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 단순 엔터티 정의
- Apache Geode는 데이터를 지역에 매핑하는 메모리 내 데이터 그리드(IMDG)입니다. 클러스터의 여러 노드에서 데이터를 분할하고 복제하는 분산 영역을 구성할 수 있습니다. 그러나 이 가이드에서는 전체 서버 클러스터와 같은 추가 설정이 필요하지 않도록 LOCAL 지역을 사용합니다.
- Apache Geode는 키/값 저장소이며 영역은 java.util.concurrent.ConcurrentMap 인터페이스를 구현합니다. 영역을 java.util.Map으로 취급할 수 있지만 데이터가 분산, 복제 및 일반적으로 영역 내에서 관리된다는 점을 고려하면 단순한 Java 맵보다 훨씬 더 정교합니다.
- 이 예제(src/main/java/hello/Person.java) 에서는 몇 가지 주석만 사용하여 Apache Geode(지역)에 Person 객체를 저장합니다.

```java
package com.example.accessingdatagemfire;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.gemfire.mapping.annotation.Region;

import lombok.Getter;

@Region(value = "People")
public class Person implements Serializable {

	@Id
	@Getter
	private final String name;

	@Getter
	private final int age;

	@PersistenceConstructor
	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	@Override
	public String toString() {
		return String.format("%s is %d years old", getName(), getAge());
	}
}

```

- 여기에 이름과 나이라는 두 개의 필드가 있는 Person 클래스가 있습니다. 또한 새 인스턴스를 만들 때 엔터티를 채우는 단일 영구 생성자가 있습니다. 이 클래스는 프로젝트 Lombok을 사용하여 구현을 단순화합니다.
- 이 클래스에는 @Region("People") 주석이 달려 있습니다. Apache Geode가 이 클래스의 인스턴스를 저장할 때 People 영역 내에 새 항목이 생성됩니다. 이 클래스는 또한 이름 필드를 @Id로 표시합니다. 이는 Apache Geode 내에서 개인 데이터를 식별하고 추적하는 데 사용되는 식별자를 나타냅니다. 기본적으로 @Id 주석 필드(예: 이름)는 키이고 Person 인스턴스는 키/값 항목의 값입니다. Apache Geode에는 자동화된 키 생성이 없으므로 엔티티를 Apache Geode에 유지하기 전에 ID(이름)를 설정해야 합니다.
- 다음으로 중요한 부분은 그 사람의 나이입니다. 이 가이드의 뒷부분에서 이를 사용하여 일부 쿼리를 구성합니다.
- 재정의된 toString() 메서드는 사람의 이름과 나이를 출력합니다.

### 간단한 쿼리 만들기

- Apache Geode용 Spring Data는 Spring을 사용하여 Apache Geode에 데이터를 저장하고 액세스하는 데 중점을 둡니다. 또한 쿼리 파생 기능과 같은 Spring Data Commons 프로젝트의 강력한 기능을 상속합니다. 기본적으로 Apache Geode(OQL)의 쿼리 언어를 배울 필요가 없습니다. 소수의 메서드를 작성할 수 있으며 프레임워크가 쿼리를 작성 합니다.
- 이것이 어떻게 작동하는지 보려면 Apache Geode에 저장된 Person 개체를 쿼리하는 인터페이스를 만듭니다.

  (src/main/java/hello/PersonRepository.java)

```java
package com.example.accessingdatagemfire;

import org.springframework.data.gemfire.repository.query.annotation.Trace;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, String> {

	@Trace
	Person findByName(String name);

	@Trace
	Iterable<Person> findByAgeGreaterThan(int age);

	@Trace
	Iterable<Person> findByAgeLessThan(int age);

	@Trace
	Iterable<Person> findByAgeGreaterThanAndAgeLessThan(int greaterThanAge, int lessThanAge);

}

```


PersonRepository는 Spring Data Commons에서 CrudRepository 인터페이스를 확장하고 리포지토리가 작동하는 값과 ID(키)(각각 사람 ​​및 문자열)에 대한 일반 유형 매개변수의 유형을 지정합니다. 이 인터페이스는 기본 CRUD(생성, 읽기, 업데이트, 삭제) 및 간단한 쿼리 데이터 액세스 작업(예: findById(..))을 비롯한 많은 작업과 함께 제공됩니다.

메서드 서명을 선언하여 필요에 따라 다른 쿼리를 정의할 수 있습니다. 이 경우 기본적으로 Person 유형의 객체를 검색하고 이름과 일치하는 객체를 찾는 findByName을 추가합니다.

메서드 종류
- findByAgeGreaterThan: 특정 연령 이상의 사람 찾기
- findByAgeLessThan: 특정 연령 미만의 사람 찾기
- findByAgeGreaterThanAndAgeLessThan: 특정 연령대의 사람 찾기

#### @Trace 어노테이션 
```
@Trace는 스프링 애플리케이션에서 메서드 실행의 추적(trace)을 수행하기 위해 사용되는 애노테이션입니다. 이 애노테이션은 애플리케이션의 실행 흐름을 추적하여 성능 문제를 진단하고, 응답 시간을 측정하고, 메서드 간의 호출 관계를 시각화하는 데 도움을 줍니다.
```
#### 주요역할
- 메서드 추적
- 호출 계승 구성
- 성능 모니터링 
- 에러추적

### 애플리케이션 클래스 생성
다음 예제에서는 모든 구성 요소가 포함된 애플리케이션 클래스를 만듭니다.

(src/main/java/hello/Application.java)
```java
package com.example.accessingdatagemfire;

import static java.util.Arrays.asList;
import static java.util.stream.StreamSupport.stream;

import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

@SpringBootApplication
@ClientCacheApplication(name = "AccessingDataGemFireApplication")
@EnableEntityDefinedRegions(
	basePackageClasses = Person.class,
	clientRegionShortcut = ClientRegionShortcut.LOCAL
)
@EnableGemfireRepositories
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	ApplicationRunner run(PersonRepository personRepository) {

		return args -> {

			Person alice = new Person("Adult Alice", 40);
			Person bob = new Person("Baby Bob", 1);
			Person carol = new Person("Teen Carol", 13);

			System.out.println("Before accessing data in Apache Geode...");

			asList(alice, bob, carol).forEach(person -> System.out.println("\t" + person));

			System.out.println("Saving Alice, Bob and Carol to Pivotal GemFire...");

			personRepository.save(alice);
			personRepository.save(bob);
			personRepository.save(carol);

			System.out.println("Lookup each person by name...");

			asList(alice.getName(), bob.getName(), carol.getName())
			  .forEach(name -> System.out.println("\t" + personRepository.findByName(name)));

			System.out.println("Query adults (over 18):");

			stream(personRepository.findByAgeGreaterThan(18).spliterator(), false)
			  .forEach(person -> System.out.println("\t" + person));

			System.out.println("Query babies (less than 5):");

			stream(personRepository.findByAgeLessThan(5).spliterator(), false)
			  .forEach(person -> System.out.println("\t" + person));

			System.out.println("Query teens (between 12 and 20):");

			stream(personRepository.findByAgeGreaterThanAndAgeLessThan(12, 20).spliterator(), false)
			  .forEach(person -> System.out.println("\t" + person));
		};
	}
}

```

구성에서 @EnableGemfireRepositories 주석을 추가해야 합니다.
- 기본적으로 @EnableGemfireRepositories는 Spring Data의 리포지토리 인터페이스 중 하나를 확장하는 모든 인터페이스에 대해 현재 패키지를 스캔합니다. basePackageClasses = MyRepository.class를 사용하여 애플리케이션별 리포지토리 확장에 대해 유형별로 다른 루트 패키지를 검색하도록 Spring Data for Apache Geode에 안전하게 알릴 수 있습니다.

모든 데이터를 저장하려면 하나 이상의 지역을 포함하는 Apache Geode 캐시가 필요합니다. 이를 위해 Apache Geode의 편리한 구성 기반 주석(@ClientCacheApplication, @PeerCacheApplication 또는 @CacheServerApplication)용 스프링 데이터 중 하나를 사용합니다.

Apache Geode는 클라이언트/서버, p2p(peer-to-peer), 심지어 WAN 배열과 같은 다양한 캐시 토폴로지를 지원합니다. p2p에서 피어 캐시 인스턴스는 애플리케이션에 포함되며 애플리케이션은 피어 캐시 구성원으로 클러스터에 참여할 수 있습니다. 그러나 애플리케이션은 클러스터의 피어 구성원이 되는 모든 제약 조건을 따르므로 클라이언트/서버 토폴로지만큼 일반적으로 사용되지 않습니다.

우리의 경우 @ClientCacheApplication을 사용하여 서버 클러스터에 연결하고 통신할 수 있는 "클라이언트" 캐시 인스턴스를 만듭니다. 그러나 간단하게 유지하기 위해 클라이언트는 서버를 설정하거나 실행할 필요 없이 LOCAL 클라이언트 영역을 사용하여 로컬로 데이터를 저장합니다.

이제 SDG 매핑 주석 @Region("People")을 사용하여 People 이라는 영역에 저장되도록 Person에 태그를 지정했던 방법을 기억하십니까? 여기에서 ClientRegionFactoryBean<String, Person> 빈 정의를 사용하여 해당 영역을 정의합니다. People이라는 이름을 지정하면서 방금 정의한 캐시의 인스턴스를 주입해야 합니다.


```
Apache Geode 캐시 인스턴스(피어 또는 클라이언트)는 데이터를 저장하는 지역의 컨테이너일 뿐입니다. 캐시는 RDBMS의 스키마로, 리전은 테이블로 생각할 수 있습니다. 그러나 캐시는 모든 지역을 제어하고 관리하기 위한 다른 관리 기능도 수행합니다.
```

```

유형은 키 유형(String)과 값 유형(Person)을 일치시키는 <String, Person>입니다.
```


public static void main 메서드는 Spring Boot의 SpringApplication.run()을 사용하여 애플리케이션을 시작하고 애플리케이션의 Spring Data 저장소를 사용하여 Apache Geode에서 데이터 액세스 작업을 수행하는 ApplicationRunner(다른 빈 정의)를 호출합니다.

응용 프로그램은 방금 정의한 PersonRepository의 인스턴스를 자동 연결합니다. Spring Data for Apache Geode는 이 인터페이스를 구현하는 구체적인 클래스를 동적으로 생성하고 인터페이스의 의무를 충족하기 위해 필요한 쿼리 코드를 연결합니다. 이 리포지토리 인스턴스는 run() 메서드에서 기능을 시연하는 데 사용됩니다.

데이터 저장 및 가져오기
이 가이드에서는 Alice, Baby Bob 및 Teen Carol의 세 가지 로컬 Person 개체를 만듭니다. 처음에는 메모리에만 존재합니다. 만든 후 Apache Geode에 저장해야 합니다.

이제 여러 쿼리를 실행할 수 있습니다. 첫 번째는 이름으로 모든 사람을 조회합니다. 그런 다음 나이 속성을 사용하여 성인, 아기 및 청소년을 찾는 몇 가지 쿼리를 실행할 수 있습니다. 로깅을 켜면 Apache Geode용 Spring Data가 대신 작성하는 쿼리를 볼 수 있습니다.

```
SDG에서 생성된 Apache Geode OQL 쿼리를 보려면 @ClientCacheApplication 주석 logLevel 속성을 config로 변경하십시오. 쿼리 메서드(예: findByName)는 SDG의 @Trace 주석으로 주석이 추가되기 때문에 Apache Geode의 OQL 쿼리 추적(쿼리 수준 로깅)이 활성화되어 생성된 OQL, 실행 시간, Apache Geode 인덱스가 결과를 수집하기 위한 쿼리 및 쿼리에서 반환된 행 수.
```

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/accessing-data-gemfile-0.0.1-SNAPSHOT.jar
```


다음과 같이 표시되어야 합니다(쿼리와 같은 다른 콘텐츠 포함).

```
Before linking up with {apache-geode-name}...
	Alice is 40 years old.
	Baby Bob is 1 years old.
	Teen Carol is 13 years old.
Lookup each person by name...
	Alice is 40 years old.
	Baby Bob is 1 years old.
	Teen Carol is 13 years old.
Adults (over 18):
	Alice is 40 years old.
Babies (less than 5):
	Baby Bob is 1 years old.
Teens (between 12 and 20):
	Teen Carol is 13 years old.
```