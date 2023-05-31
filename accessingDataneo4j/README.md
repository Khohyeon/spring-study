## Neo4j로 데이터 액세스
이 가이드는 그래프 기반 데이터베이스인 Neo4j 에서 데이터를 저장하고 검색하는 애플리케이션을 빌드하기 위해 Spring Data Neo4j를 사용하는 프로세스를 안내합니다 .

### 무엇을 만들 것인가
Neo4j의 NoSQL 그래프 기반 데이터 저장소를 사용하여 임베디드 Neo4j 서버를 구축하고 엔터티 및 관계를 저장하며 쿼리를 개발합니다.

### 필요한 것
* 약 15분

* 선호하는 텍스트 편집기 또는 IDE

* 자바 17 이상

* Gradle 7.5+ 또는 Maven 3.5+

코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작 하려면 Spring Initializr로 시작하기 로 이동하십시오 .

기본 사항을 건너뛰 려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-accessing-data-neo4j.git
* cd 로gs-accessing-data-neo4j/initial
* Simple Entity 정의 로 이동하십시오 .
작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-accessing-data-neo4j/complete.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭 하고 Spring Data Neo4j를 선택합니다 .

4. 생성 을 클릭합니다 .

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.



### docker 이용 neo4j 설치

#### neo4j 받기

```shell
docker pull neo4j
```
#### neo4j 실행

```shell
docker run --publish=7474:7474 --publish=7473:7473 --publish=7687:7687 --volume=/home/username/neo4j/data:/data neo4j
```

#### neo4j 실행 확인
```shell
2023-05-31 01:30:50.129+0000 INFO  Starting...
2023-05-31 01:30:51.286+0000 INFO  This instance is ServerId{a5689bb5} (a5689bb5-db64-4bde-9729-4f0ab4a4c8e5)
2023-05-31 01:30:52.193+0000 INFO  ======== Neo4j 5.8.0 ========
2023-05-31 01:30:53.800+0000 INFO  Bolt enabled on 0.0.0.0:7687.
2023-05-31 01:30:54.642+0000 INFO  Remote interface available at http://localhost:7474/
2023-05-31 01:30:54.646+0000 INFO  id: B73B4747BE71A36E881BF8D98ABF3DD13BB2CDE99062E9D7DD6689E09157CA02
2023-05-31 01:30:54.647+0000 INFO  name: system
2023-05-31 01:30:54.648+0000 INFO  creationDate: 2023-05-31T01:29:19.135Z
2023-05-31 01:30:54.648+0000 INFO  Started.
```

### 단순 엔터티 정의
Neo4j는 엔터티와 엔터티의 관계를 캡처하며 두 측면 모두 똑같이 중요합니다. 각 사람에 대한 레코드를 저장하는 시스템을 모델링한다고 상상해 보십시오. 그러나 개인의 동료도 추적하려고 합니다( teammates이 예에서는). Spring Data Neo4j를 사용하면 다음 목록(in src/main/java/com/example/accessingdataneo4j/Person.java)과 같이 몇 가지 간단한 주석으로 모든 것을 캡처할 수 있습니다.

```java
package com.example.accessingdataneo4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

@Node
public class Person {

  @Id @GeneratedValue private Long id;

  private String name;

  private Person() {
    // Empty constructor required as of Neo4j API 2.0.5
  };

  public Person(String name) {
    this.name = name;
  }

  /**
   * Neo4j doesn't REALLY have bi-directional relationships. It just means when querying
   * to ignore the direction of the relationship.
   * https://dzone.com/articles/modelling-data-neo4j
   */
  @Relationship(type = "TEAMMATE")
  public Set<Person> teammates;

  public void worksWith(Person person) {
    if (teammates == null) {
      teammates = new HashSet<>();
    }
    teammates.add(person);
  }

  public String toString() {

    return this.name + "'s teammates => "
      + Optional.ofNullable(this.teammates).orElse(
          Collections.emptySet()).stream()
            .map(Person::getName)
            .collect(Collectors.toList());
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
```
Lombok을 사용하지 않고 직접 코드를 작성하면 다음과 같습니다.

@Node 
- Neo4j에 저장할 수 있는 엔터티임을 나타냅니다.
- 이 애노테이션이 선언된 클래스는 Neo4j 데이터베이스의 노드로 저장되고, 그래프 데이터를 표현하며, 노드 간의 관계를 형성할 수 있습니다.
Neo4j가 저장하면 새 노드가 생성됩니다. id이 클래스 에는 @GraphId. Neo4j는 @GraphId 데이터를 추적하기 위해 내부적으로 사용합니다.
다음으로 중요한 부분은 teammates. 간단 Set<Person>하지만 로 표시되어 있습니다 @Relationship. 이는 이 집합의 모든 구성원이 별도의 노드로도 존재할 것으로 예상됨을 의미합니다 Person. 방향이 어떻게 설정되어 있는지 확인하십시오 UNDIRECTED. 즉, TEAMMATE관계를 쿼리할 때 Spring Data Neo4j는 관계의 방향을 무시합니다.

### 간단한 쿼리 만들기
Spring Data Neo4j는 Neo4j에 데이터를 저장하는 데 중점을 둡니다. 그러나 쿼리 파생 기능을 포함하여 Spring Data Commons 프로젝트의 기능을 상속합니다. 기본적으로 Neo4j의 쿼리 언어를 배울 필요가 없습니다. 대신 몇 가지 메서드를 작성하고 쿼리가 자동으로 작성되도록 할 수 있습니다.

이것이 어떻게 작동하는지 보려면 Person노드를 쿼리하는 인터페이스를 만드십시오. 다음 목록( src/main/java/com/example/accessingdataneo4j/PersonRepository.java)은 이러한 쿼리를 보여줍니다.

```java
package com.example.accessingdataneo4j;

import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

  Person findByName(String name);
  List<Person> findByTeammatesName(String name);
}
```

PersonRepository 인터페이스를 확장하고 Neo4jRepository 가 작동하는 유형을 연결합니다

이 인터페이스는 표준 CRUD 작업을 비롯한 많은 작업과 함께 제공됩니다.

### Neo4j 액세스 권한
Neo4j Community Edition에 액세스하려면 자격 증명이 필요합니다. src/main/resources/application.properties다음 목록과 같이 몇 가지 속성(에서)을 설정하여 이러한 자격 증명을 구성할 수 있습니다 .

```properties
spring.neo4j.uri=bolt://localhost:7687 
spring.neo4j.authentication.username=neo4j 
spring.neo4j.authentication.password=비밀
```

### 애플리케이션 클래스 생성
Spring Initializr는 애플리케이션을 위한 간단한 클래스를 생성합니다. 다음 목록은 Initializr가 이 예제(에서 src/main/java/com/example/accessingdataneo4j/AccessingDataNeo4jApplication.java)에 대해 만든 클래스를 보여줍니다.
```java
package com.example.accessingdataneo4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccessingDataNeo4jApplication {

  public static void main(String[] args) {
    SpringApplication.run(AccessingDataNeo4jApplication.class, args);
  }

}
```

```androiddatabinding
기본적으로 @EnableNeo4jRepositories는 Spring Data의 리포지토리 인터페이스 중 하나를 확장하는 모든 인터페이스에 대해 현재 패키지를 스캔합니다. 
basePackageClasses=MyRepository.class는 프로젝트 레이아웃에 여러 프로젝트가 있고 리포지토리를 찾지 못하는 경우 이를 사용하여 Spring Data Neo4j에 유형별로 다른 루트 패키지를 스캔하도록 안전하게 지시할 수 있습니다 .
```
PersonRepository 이제 이전에 정의한 인스턴스를 자동연결합니다 . Spring Data Neo4j는 해당 인터페이스를 동적으로 구현하고 필요한 쿼리 코드를 연결하여 인터페이스의 의무를 충족합니다.

이 main 메서드는 Spring Boot를 사용하여 SpringApplication.run()애플리케이션을 시작하고 CommandLineRunner 관계를 구축하는 를 호출합니다.

#### CommandLineRunner 란??
- 스프링 프레임워크에서 제공하는 인터페이스로, 애플리케이션을 시작할 때 특정한 작업을 수행하도록 도와주는 역할을 합니다. CommandLineRunner를 구현한 빈은 스프링 컨텍스트가 초기화된 후에 실행됩니다.
- CommandLineRunner를 구현하는 클래스는 스프링 빈으로 등록되어야 합니다. 그러면 스프링 컨텍스트가 초기화되면서 해당 빈의 run 메서드가 자동으로 호출됩니다. 
- 사용시 애플리케이션 시작 시 특정한 초기화 작업을 수행할 수 있습니다. 예를 들어, 데이터베이스 연결 설정, 캐시 초기화, 스케줄러 등의 작업을 수행할 수 있습니다.

마지막으로 "누가 누구와 함께 일합니까?"라는 질문에 답하면서 뒤를 돌아보는 다른 쿼리를 확인하십시오.

다음 목록은 완성된 AccessingDataNeo4jApplication 클래스를 보여줍니다( src/main/java/com/example/accessingdataneo4j/AccessingDataNeo4jApplication.java)

```java
package com.example.accessingdataneo4j;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories
public class AccessingDataNeo4jApplication {

	private final static Logger log = LoggerFactory.getLogger(AccessingDataNeo4jApplication.class);

	public static void main(String[] args) throws Exception {
		SpringApplication.run(AccessingDataNeo4jApplication.class, args);
		System.exit(0);
	}

	@Bean
	CommandLineRunner demo(PersonRepository personRepository) {
		return args -> {

			personRepository.deleteAll();

			Person greg = new Person("Greg");
			Person roy = new Person("Roy");
			Person craig = new Person("Craig");

			List<Person> team = Arrays.asList(greg, roy, craig);

			log.info("Before linking up with Neo4j...");

			team.stream().forEach(person -> log.info("\t" + person.toString()));

			personRepository.save(greg);
			personRepository.save(roy);
			personRepository.save(craig);

			greg = personRepository.findByName(greg.getName());
			greg.worksWith(roy);
			greg.worksWith(craig);
			personRepository.save(greg);

			roy = personRepository.findByName(roy.getName());
			roy.worksWith(craig);
			// We already know that roy works with greg
			personRepository.save(roy);

			// We already know craig works with roy and greg

			log.info("Lookup each person by name...");
			team.stream().forEach(person -> log.info(
					"\t" + personRepository.findByName(person.getName()).toString()));

			List<Person> teammates = personRepository.findByTeammatesName(greg.getName());
			log.info("The following have Greg as a teammate...");
			teammates.stream().forEach(person -> log.info("\t" + person.getName()));
		};
	}

}
```
#### Gardle을 사용하는 경우

```
java -jar 빌드/libs/gs-accessing-data-neo4j-0.1.0.jar
```


