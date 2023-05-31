## 배치 서비스 생성
이 가이드는 기본 일괄 처리 기반 솔루션을 만드는 과정을 안내합니다.

### 무엇을 만들 것인가
CSV 스프레드시트에서 데이터를 가져와 사용자 정의 코드로 변환하고 최종 결과를 데이터베이스에 저장하는 서비스를 구축합니다.

### 필요한 것
* 약 15분
* 선호하는 텍스트 편집기 또는 IDE
* 자바 17 이상
* Gradle 7.5+ 또는 Maven 3.5+
* 코드를 IDE로 바로 가져올 수도 있습니다.

이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.
처음부터 시작 하려면 Spring Initializr로 시작하기 로 이동하십시오 .
기본 사항을 건너뛰 려면 다음을 수행하십시오.
* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-batch-processing.git
* cd 로gs-batch-processing/initial
* 비즈니스 클래스 만들기 로 이동하십시오 .
작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-batch-processing/complete.

### 비즈니스 데이터
일반적으로 고객이나 비즈니스 분석가가 스프레드시트를 제공합니다. 
이 간단한 예의 경우 다음에서 구성 데이터를 찾을 수 있습니다. (src/main/resources/sample-data.csv)

```
Jill,Doe
Joe,Doe
Justin,Doe
Jane,Doe
John,Doe
```

이 스프레드시트에는 각 행에 쉼표로 구분된 이름과 성이 포함되어 있습니다. 이것은 Spring이 사용자 지정 없이 처리할 수 있는 상당히 일반적인 패턴입니다.

다음으로 데이터를 저장할 테이블을 생성하는 SQL 스크립트를 작성해야 합니다. 
다음에서 이러한 스크립트를 찾을 수 있습니다. (src/main/resources/schema-all.sql)
```
DROP TABLE people IF EXISTS;

CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);
```

```

Spring Boot는 시작 중에 schema-@@platform@@.sql을 자동으로 실행합니다. -all은 모든 플랫폼의 기본값입니다.
```

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.
2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.
3. 종속성을 클릭 하고 Spring Batch 및 HyperSQL 데이터베이스를 선택합니다 .
4. 생성 을 클릭합니다 .
5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 비즈니스 클래스 만들기
데이터 입력 및 출력의 형식을 볼 수 있으므로 다음 예제 (src/main/java/com/example/batchprocessing/Person.java) 에서 와 같이 데이터 행을 나타내는 코드를 작성할 수 있습니다 .

```java
package com.example.batchprocessing;

public class Person {

  private String lastName;
  private String firstName;

  public Person() {
  }

  public Person(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public String toString() {
    return "firstName: " + firstName + ", lastName: " + lastName;
  }

}
```
Person 생성자를 통해 이름과 성을 사용하거나 속성을 설정하여 클래스를 인스턴스화할 수 있습니다 .

### 중간 프로세서 만들기
일괄 처리의 일반적인 패러다임은 데이터를 수집하고 변환한 다음 다른 곳으로 파이프하는 것입니다.
여기에서 이름을 대문자로 변환하는 간단한 변환기를 작성해야 합니다. 다음 목록 (src/main/java/com/example/batchprocessing/PersonItemProcessor.java)은 이를 수행하는 방법을 보여줍니다.

```java
package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

  private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

  @Override
  public Person process(final Person person) throws Exception {
    final String firstName = person.getFirstName().toUpperCase();
    final String lastName = person.getLastName().toUpperCase();

    final Person transformedPerson = new Person(firstName, lastName);

    log.info("Converting (" + person + ") into (" + transformedPerson + ")");

    return transformedPerson;
  }

}
```


PersonItemProcessor는 Spring Batch의 ItemProcessor 인터페이스를 구현합니다. 이렇게 하면 이 가이드의 뒷부분에서 정의할 배치 작업에 코드를 쉽게 연결할 수 있습니다. 인터페이스에 따르면 들어오는 Person 개체를 수신한 후 대문자 Person으로 변환합니다.

```
입력 및 출력 유형이 동일할 필요는 없습니다. 실제로 한 데이터 소스를 읽은 후 애플리케이션의 데이터 흐름에 다른 데이터 유형이 필요한 경우가 있습니다.
```

### 일괄 작업 정리
이제 실제 배치 작업을 결합해야 합니다. Spring Batch는 사용자 지정 코드를 작성할 필요성을 줄이는 많은 유틸리티 클래스를 제공합니다. 대신 비즈니스 논리에 집중할 수 있습니다.

작업을 구성하려면 먼저 src/main/java/com/exampe/batchprocessing/BatchConfiguration.java에서 다음 예제와 같은 Spring @Configuration 클래스를 생성해야 합니다. 이 예에서는 메모리 기반 데이터베이스를 사용합니다. 즉, 완료되면 데이터가 사라집니다. 이제 BatchConfiguration 클래스에 다음 빈을 추가하여 판독기, 프로세서 및 기록기를 정의합니다.

```java
@Bean
public FlatFileItemReader<Person> reader() {
  return new FlatFileItemReaderBuilder<Person>()
    .name("personItemReader")
    .resource(new ClassPathResource("sample-data.csv"))
    .delimited()
    .names(new String[]{"firstName", "lastName"})
    .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
      setTargetType(Person.class);
    }})
    .build();
}

@Bean
public PersonItemProcessor processor() {
  return new PersonItemProcessor();
}

@Bean
public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
  return new JdbcBatchItemWriterBuilder<Person>()
    .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
    .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
    .dataSource(dataSource)
    .build();
}
```
1. reader() 메서드는 ItemReader를 생성합니다. sample-data.csv라는 파일을 찾고 이를 Person으로 바꾸기에 충분한 정보가 있는 각 줄 항목을 구문 분석합니다.
2. processor()는 데이터를 대문자로 변환하기 위해 이전에 정의한 PersonItemProcessor의 인스턴스를 생성합니다.
3. writer(DataSource)는 ItemWriter를 생성합니다. 
이것은 JDBC 대상을 대상으로 하며 @EnableBatchProcessing에 의해 생성된 데이터 소스의 복사본을 자동으로 가져옵니다. 여기에는 Java bean 특성에 의해 구동되는 단일 Person을 삽입하는 데 필요한 SQL문이 포함됩니다.

```
@Bean
public Job importUserJob(JobRepository jobRepository,
    JobCompletionNotificationListener listener, Step step1) {
  return new JobBuilder("importUserJob", jobRepository)
    .incrementer(new RunIdIncrementer())
    .listener(listener)
    .flow(step1)
    .end()
    .build();
}

@Bean
public Step step1(JobRepository jobRepository,
    PlatformTransactionManager transactionManager, JdbcBatchItemWriter<Person> writer) {
  return new StepBuilder("step1", jobRepository)
    .<Person, Person> chunk(10, transactionManager)
    .reader(reader())
    .processor(processor())
    .writer(writer)
    .build();
}
```

첫 번째 방법은 작업을 정의하고 두 번째 방법은 단일 단계를 정의합니다. 작업은 단계로 구성되며 각 단계에는 판독기, 프로세서 및 작성기가 포함될 수 있습니다.

이 작업 정의에서는 작업이 데이터베이스를 사용하여 실행 상태를 유지하기 때문에 증분기가 필요합니다. 그런 다음 각 단계를 나열합니다(이 작업에는 하나의 단계만 있음). 작업이 종료되고 Java API가 완벽하게 구성된 작업을 생성합니다.

단계 정의에서 한 번에 쓸 데이터 양을 정의합니다. 이 경우 한 번에 최대 10개의 레코드를 씁니다. 다음으로 앞서 주입한 빈을 사용하여 판독기, 프로세서 및 기록기를 구성합니다.

배치 구성의 마지막 부분은 작업이 완료될 때 알림을 받는 방법입니다. 다음 예제(src/main/java/com/example/batchprocessing/JobCompletionNotificationListener.java)는 이러한 클래스를 보여줍니다.

```java
package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

  private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("!!! JOB FINISHED! Time to verify the results");

      jdbcTemplate.query("SELECT first_name, last_name FROM people",
        (rs, row) -> new Person(
          rs.getString(1),
          rs.getString(2))
      ).forEach(person -> log.info("Found <{{}}> in the database.", person));
    }
  }
}
```

JobCompletionNotificationListener는 작업이 BatchStatus.COMPLETED일 때 수신한 다음 JdbcTemplate을 사용하여 결과를 검사합니다.

### 응용 프로그램을 실행 가능하게 만들기
일괄 처리를 웹 앱 및 WAR 파일에 포함할 수 있지만 아래에 설명된 더 간단한 접근 방식은 독립 실행형 애플리케이션을 만듭니다. 오래된 Java main()메서드로 구동되는 실행 가능한 단일 JAR 파일에 모든 것을 패키징합니다.

Spring Initializr는 당신을 위한 애플리케이션 클래스를 생성했습니다. 이 간단한 예제의 경우 추가 수정 없이 작동합니다. 다음 목록(src/main/java/com/example/batchprocessing/BatchProcessingApplication.java)은 애플리케이션 클래스를 보여줍니다.

```java
package com.example.batchprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchProcessingApplication {

  public static void main(String[] args) throws Exception {
    System.exit(SpringApplication.exit(SpringApplication.run(BatchProcessingApplication.class, args)));
  }
}
```

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/gs-batch-processing-0.1.0.jar
```

작업은 변형되는 각 사람에 대해 한 줄을 인쇄합니다. 작업이 실행된 후 데이터베이스 쿼리의 출력도 볼 수 있습니다. 다음 출력과 유사해야 합니다.

```
Converting (firstName: Jill, lastName: Doe) into (firstName: JILL, lastName: DOE)
Converting (firstName: Joe, lastName: Doe) into (firstName: JOE, lastName: DOE)
Converting (firstName: Justin, lastName: Doe) into (firstName: JUSTIN, lastName: DOE)
Converting (firstName: Jane, lastName: Doe) into (firstName: JANE, lastName: DOE)
Converting (firstName: John, lastName: Doe) into (firstName: JOHN, lastName: DOE)
Step: [step1] executed in 49ms
!!! JOB FINISHED! Time to verify the results
Found <{firstName: JILL, lastName: DOE}> in the database.
Found <{firstName: JOE, lastName: DOE}> in the database.
Found <{firstName: JUSTIN, lastName: DOE}> in the database.
Found <{firstName: JANE, lastName: DOE}> in the database.
Found <{firstName: JOHN, lastName: DOE}> in the database.
Job: [FlowJob: [name=importUserJob]] completed with the following parameters: [{'run.id':'{value=1, type=class java.lang.Long, identifying=true}'}] and the following status: [COMPLETED] in 63ms
```