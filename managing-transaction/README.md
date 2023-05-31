## 트랜잭션 관리
이 가이드는 비간섭 트랜잭션으로 데이터베이스 작업을 래핑하는 프로세스를 안내합니다.

### 무엇을 만들 것인가
특수 JDBC 코드를 작성할 필요 없이 데이터베이스 작업을 트랜잭션으로 만드는 간단한 JDBC 응용 프로그램을 빌드합니다 .

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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-managing-transactions.git

* cd 로gs-managing-transactions/initial

* 예약 서비스 만들기 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-managing-transactions/complete.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.
2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.
3. 종속성을 클릭 하고 Spring Data JDBC 및 H2 데이터베이스를 선택합니다 .
4. 생성 을 클릭합니다 .
5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 예약 서비스 만들기
예약 서비스 만들기
먼저 BookingService 클래스를 사용하여 사람들을 이름으로 시스템에 예약하는 JDBC 기반 서비스를 찾아보세요. 다음 목록(에서 src/main/java/com/example/managingtransactions/BookingService.java)은 이를 실천하는 방법을 보여줍니다.

```java
package com.example.managingtransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BookingService {

	private final static Logger logger = LoggerFactory.getLogger(BookingService.class);

	private final JdbcTemplate jdbcTemplate;

	public BookingService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional
	public void book(String... persons) {
		for (String person : persons) {
			logger.info("Booking " + person + " in a seat...");
			jdbcTemplate.update("insert into BOOKINGS(FIRST_NAME) values (?)", person);
		}
	}

	public List<String> findAllBookings() {
		return jdbcTemplate.query("select FIRST_NAME from BOOKINGS",
				(rs, rowNum) -> rs.getString("FIRST_NAME"));
	}

}
```

- 이 코드에는 나머지 코드에 필요한 모든 데이터베이스 상호 작용을 수행하는 편리한 템플릿 클래스인 자동 연결 JdbcTemplate이 있습니다.

- 여러 사람을 예약할 수 있는 예약 방법도 있습니다. 사람 목록을 반복하고 각 사람에 대해 JdbcTemplate을 사용하여 해당 사람을 BOOKINGS 테이블에 삽입합니다. 이 메서드는 @Transactional 태그가 붙습니다. 즉, 오류가 발생하면 전체 작업이 이전 상태로 롤백되고 원래 예외가 다시 발생합니다. 즉, 한 사람이 추가되지 않으면 아무도 BOOKINGS에 추가되지 않습니다.

- 데이터베이스를 쿼리하는 findAllBookings 메서드도 있습니다. 데이터베이스에서 가져온 각 행은 문자열로 변환되고 모든 행은 목록으로 어셈블됩니다. 

### 애플리케이션 구축
Spring Initializr는 애플리케이션 클래스를 제공합니다.
이 경우 이 애플리케이션 클래스를 수정할 필요가 없습니다. 
다음 목록(src/main/java/com/example/managingtransactions/ManagingTransactionsApplication.java에서)은 애플리케이션 클래스를 보여줍니다.

```java
package com.example.managingtransactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ManagingTransactionsApplication {

  public static void main(String[] args) {
    SpringApplication.run(ManagingTransactionsApplication.class, args);
  }

}
```

@SpringBootApplication은 다음을 모두 추가하는 편리한 주석입니다.

- @Configuration: 애플리케이션 컨텍스트에 대한 빈 정의의 소스로 클래스에 태그를 지정합니다.
- @EnableAutoConfiguration: 클래스 경로 설정, 기타 빈 및 다양한 속성 설정을 기반으로 빈 추가를 시작하도록 Spring Boot에 지시합니다. 예를 들어 spring-webmvc가 클래스 경로에 있는 경우 이 주석은 애플리케이션을 웹 애플리케이션으로 플래그 지정하고 DispatcherServlet 설정과 같은 주요 동작을 활성화합니다.
- @ComponentScan: com/example 패키지에서 다른 구성 요소, 구성 및 서비스를 찾도록 Spring에 지시하여 컨트롤러를 찾도록 합니다.

main() 메서드는 Spring Boot의 SpringApplication.run() 메서드를 사용하여 애플리케이션을 시작합니다. 이 웹 애플리케이션은 100% 순수 Java이며 배관이나 인프라 구성을 처리할 필요가 없습니다.

Spring Boot는 클래스 경로에서 spring-jdbc 및 h2를 감지하고 자동으로 DataSource 및 JdbcTemplate을 생성합니다. 이제 이 인프라를 사용할 수 있고 전용 구성이 없기 때문에 DataSourceTransactionManager도 생성됩니다. 이는 @Transactional 주석이 달린 메서드(예: BookingService의 book 메서드)를 가로채는 구성 요소입니다. BookingService는 클래스 경로 스캔으로 감지됩니다.

이 가이드에서 설명하는 또 다른 Spring Boot 기능은 시작 시 스키마를 초기화하는 기능입니다. 다음 파일(src/main/resources/schema.sql에서)은 데이터베이스 스키마를 정의합니다.

```sql
drop table BOOKINGS if exists;
create table BOOKINGS(ID serial, FIRST_NAME varchar(5) NOT NULL);
```


BookingService를 주입하고 다양한 트랜잭션 사용 사례를 보여주는 CommandLineRunner도 있습니다. 다음 목록(src/main/java/com/example/managingtransactions/AppRunner.java)은 명령줄 실행기를 보여줍니다.

```java
package com.example.managingtransactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class AppRunner implements CommandLineRunner {

  private final static Logger logger = LoggerFactory.getLogger(AppRunner.class);

  private final BookingService bookingService;

  public AppRunner(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @Override
  public void run(String... args) throws Exception {
    bookingService.book("Alice", "Bob", "Carol");
    Assert.isTrue(bookingService.findAllBookings().size() == 3,
        "First booking should work with no problem");
    logger.info("Alice, Bob and Carol have been booked");
    try {
      bookingService.book("Chris", "Samuel");
    } catch (RuntimeException e) {
      logger.info("v--- The following exception is expect because 'Samuel' is too " +
          "big for the DB ---v");
      logger.error(e.getMessage());
    }

    for (String person : bookingService.findAllBookings()) {
      logger.info("So far, " + person + " is booked.");
    }
    logger.info("You shouldn't see Chris or Samuel. Samuel violated DB constraints, " +
        "and Chris was rolled back in the same TX");
    Assert.isTrue(bookingService.findAllBookings().size() == 3,
        "'Samuel' should have triggered a rollback");

    try {
      bookingService.book("Buddy", null);
    } catch (RuntimeException e) {
      logger.info("v--- The following exception is expect because null is not " +
          "valid for the DB ---v");
      logger.error(e.getMessage());
    }

    for (String person : bookingService.findAllBookings()) {
      logger.info("So far, " + person + " is booked.");
    }
    logger.info("You shouldn't see Buddy or null. null violated DB constraints, and " +
        "Buddy was rolled back in the same TX");
    Assert.isTrue(bookingService.findAllBookings().size() == 3,
        "'null' should have triggered a rollback");
  }

}
```

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/managing-transactions-0.0.1-SNAPSHOT.jar
```

다음 출력이 표시되어야 합니다.

```shell
2023-05-31T16:41:45.288+09:00  INFO 7040 --- [           main] c.e.m.ManagingTransactionsApplication    : Starting ManagingTransactionsApplication using Java 17.0.7 with PID 7040 (C:\Users\HoHyeon Kim\IdeaProjects\spring-guide\spring-study\managing-transaction\build\classes\java\main started by HoHyeon Kim in C:\Users\HoHyeon Kim\IdeaProjects\spring-guide\spring-study)
2023-05-31T16:41:45.292+09:00  INFO 7040 --- [           main] c.e.m.ManagingTransactionsApplication    : No active profile set, falling back to 1 default profile: "default"
2023-05-31T16:41:45.597+09:00  INFO 7040 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JDBC repositories in DEFAULT mode.
2023-05-31T16:41:45.612+09:00  INFO 7040 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 9 ms. Found 0 JDBC repository interfaces.
2023-05-31T16:41:45.778+09:00  INFO 7040 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2023-05-31T16:41:45.908+09:00  INFO 7040 --- [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection conn0: url=jdbc:h2:mem:a91c6d03-c11e-41d4-a8a0-1ec8e022a000 user=SA
2023-05-31T16:41:45.911+09:00  INFO 7040 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2023-05-31T16:41:46.089+09:00  INFO 7040 --- [           main] c.e.m.ManagingTransactionsApplication    : Started ManagingTransactionsApplication in 1.653 seconds (process running for 2.227)
2023-05-31T16:41:46.094+09:00  INFO 7040 --- [           main] c.e.managingtransaction.BookingService   : Booking Alice in a seat...
2023-05-31T16:41:46.102+09:00  INFO 7040 --- [           main] c.e.managingtransaction.BookingService   : Booking Bob in a seat...
2023-05-31T16:41:46.102+09:00  INFO 7040 --- [           main] c.e.managingtransaction.BookingService   : Booking Carol in a seat...
2023-05-31T16:41:46.111+09:00  INFO 7040 --- [           main] c.example.managingtransaction.AppRunner  : Alice, Bob and Carol have been booked
2023-05-31T16:41:46.111+09:00  INFO 7040 --- [           main] c.e.managingtransaction.BookingService   : Booking Chris in a seat...
2023-05-31T16:41:46.111+09:00  INFO 7040 --- [           main] c.e.managingtransaction.BookingService   : Booking Samuel in a seat...
2023-05-31T16:41:46.117+09:00  INFO 7040 --- [           main] c.example.managingtransaction.AppRunner  : v--- The following exception is expect because 'Samuel' is too big for the DB ---v
2023-05-31T16:41:46.118+09:00 ERROR 7040 --- [           main] c.example.managingtransaction.AppRunner  : PreparedStatementCallback; SQL [insert into BOOKINGS(FIRST_NAME) values (?)]; Value too long for column "FIRST_NAME CHARACTER VARYING(5)": "'Samuel' (6)"; SQL statement:
insert into BOOKINGS(FIRST_NAME) values (?) [22001-214]
2023-05-31T16:41:46.118+09:00  INFO 7040 --- [           main] c.example.managingtransaction.AppRunner  : So far, Alice is booked.
2023-05-31T16:41:46.118+09:00  INFO 7040 --- [           main] c.example.managingtransaction.AppRunner  : So far, Bob is booked.
2023-05-31T16:41:46.118+09:00  INFO 7040 --- [           main] c.example.managingtransaction.AppRunner  : So far, Carol is booked.
2023-05-31T16:41:46.118+09:00  INFO 7040 --- [           main] c.example.managingtransaction.AppRunner  : You shouldn't see Chris or Samuel. Samuel violated DB constraints, and Chris was rolled back in the same TX
2023-05-31T16:41:46.118+09:00  INFO 7040 --- [           main] c.e.managingtransaction.BookingService   : Booking Buddy in a seat...
2023-05-31T16:41:46.118+09:00  INFO 7040 --- [           main] c.e.managingtransaction.BookingService   : Booking null in a seat...
2023-05-31T16:41:46.120+09:00  INFO 7040 --- [           main] c.example.managingtransaction.AppRunner  : v--- The following exception is expect because null is not valid for the DB ---v
2023-05-31T16:41:46.120+09:00 ERROR 7040 --- [           main] c.example.managingtransaction.AppRunner  : PreparedStatementCallback; SQL [insert into BOOKINGS(FIRST_NAME) values (?)]; NULL not allowed for column "FIRST_NAME"; SQL statement:
insert into BOOKINGS(FIRST_NAME) values (?) [23502-214]
2023-05-31T16:41:46.120+09:00  INFO 7040 --- [           main] c.example.managingtransaction.AppRunner  : So far, Alice is booked.
2023-05-31T16:41:46.120+09:00  INFO 7040 --- [           main] c.example.managingtransaction.AppRunner  : So far, Bob is booked.
2023-05-31T16:41:46.120+09:00  INFO 7040 --- [           main] c.example.managingtransaction.AppRunner  : So far, Carol is booked.
2023-05-31T16:41:46.120+09:00  INFO 7040 --- [           main] c.example.managingtransaction.AppRunner  : You shouldn't see Buddy or null. null violated DB constraints, and Buddy was rolled back in the same TX
2023-05-31T16:41:46.123+09:00  INFO 7040 --- [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2023-05-31T16:41:46.131+09:00  INFO 7040 --- [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
```