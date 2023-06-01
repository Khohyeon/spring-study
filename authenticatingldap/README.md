## LDAP로 사용자 인증

이 가이드는 애플리케이션을 생성하고 이를 Spring Security LDAP 모듈 로 보호하는 프로세스를 안내합니다 .

### 무엇을 만들 것인가
Spring Security의 임베디드 Java 기반 LDAP 서버로 보호되는 간단한 웹 애플리케이션을 빌드합니다. 사용자 집합이 포함된 데이터 파일과 함께 LDAP 서버를 로드합니다.

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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-authenticating-ldap.git

* cd 로gs-authenticating-ldap/initial

* 간단한 웹 컨트롤러 만들기 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-authenticating-ldap/complete.

### 스프링 이니셜라이저로 시작하기

```
이 가이드의 요점은 보안되지 않은 웹 애플리케이션을 보호하는 것이므로 먼저 보안되지 않은 웹 애플리케이션을 빌드하고 나중에 가이드에서 Spring Security 및 LDAP 기능에 대한 종속성을 추가합니다.
```

이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭 하고 Spring Web 을 선택하십시오 .

4. 생성 을 클릭합니다 .

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 간단한 웹 컨트롤러 만들기
Spring에서 REST 끝점은 Spring MVC 컨트롤러입니다. 다음 Spring MVC 컨트롤러( from src/main/java/com/example/authenticatingldap/HomeController.java) 는 GET /간단한 메시지를 반환하여 요청을 처리합니다.

```java
package com.example.authenticatingldap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  @GetMapping("/")
  public String index() {
    return "Welcome to the home page!";
  }

}
```

전체 클래스는 @RestControllerSpring MVC가 컨트롤러를 자동 감지하고(내장된 스캔 기능을 사용하여) 필요한 웹 경로를 자동으로 구성할 수 있도록 마크업됩니다.

@RestController또한 보기가 없기 때문에 Spring MVC에 HTTP 응답 본문에 직접 텍스트를 작성하도록 지시합니다. 대신 페이지를 방문하면 브라우저에 간단한 메시지가 표시됩니다(이 가이드의 초점은 LDAP로 페이지를 보호하는 것이므로).

### 보안되지 않은 웹 애플리케이션 구축
웹 애플리케이션을 보호하기 전에 작동하는지 확인해야 합니다. 이를 위해서는 클래스를 생성하여 수행할 수 있는 몇 가지 키 빈을 정의해야 합니다 Application. 다음 목록(에서 src/main/java/com/example/authenticatingldap/AuthenticatingLdapApplication.java)은 해당 클래스를 보여줍니다.

```java
package com.example.authenticatingldap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthenticatingLdapApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuthenticatingLdapApplication.class, args);
  }

}
```

@SpringBootApplication다음을 모두 추가하는 편의 주석입니다.

* @Configuration: 애플리케이션 컨텍스트에 대한 빈 정의의 소스로 클래스에 태그를 지정합니다.

* @EnableAutoConfiguration: 클래스 경로 설정, 기타 빈 및 다양한 속성 설정을 기반으로 빈 추가를 시작하도록 Spring Boot에 지시합니다. 예를 들어 spring-webmvc클래스 경로에 있는 경우 이 주석은 애플리케이션을 웹 애플리케이션으로 플래그 지정하고 DispatcherServlet.

* @ComponentScancom/example: 컨트롤러를 찾을 수 있도록 패키지 에서 다른 구성 요소, 구성 및 서비스를 찾도록 Spring에 지시합니다 .

이 main()메서드는 Spring Boot의 SpringApplication.run()메서드를 사용하여 애플리케이션을 시작합니다. XML이 한 줄도 없다는 사실을 눈치채셨나요? web.xml파일도 없습니다 . 이 웹 애플리케이션은 100% 순수 Java이며 배관이나 인프라 구성을 처리할 필요가 없습니다.

### Gradle을 사용하는 경우 

./gradlew bootRun. ./gradlew build또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .

```
java -jar build/libs/gs-authenticating-ldap-0.1.0.jar
```

브라우저를 열고 http://localhost:8080 을 방문하면 다음과 같은 일반 텍스트가 표시됩니다.

```
Welcome to the home page!
```

### 스프링 보안 설정
Spring Security를 구성하려면 먼저 빌드에 몇 가지 추가 종속성을 추가해야 합니다.

Gradle 기반 빌드의 경우 build.gradle파일에 다음 종속성을 추가합니다.

```gradle
implementation("org.springframework.boot:spring-boot-starter-security")
implementation("org.springframework.ldap:spring-ldap-core")
implementation("org.springframework.security:spring-security-ldap")
implementation("com.unboundid:unboundid-ldapsdk")
```

Gradle의 아티팩트 해결 문제로 인해 spring-tx를 가져와야 합니다. 그렇지 않으면 Gradle이 작동하지 않는 이전 항목을 가져옵니다.

이러한 종속성은 Spring Security 및 오픈 소스 LDAP 서버인 UnboundId를 추가합니다. 이러한 종속성이 있으면 다음 예제(출처 src/main/java/com/example/authenticatingldap/WebSecurityConfig.java)에서와 같이 순수 Java를 사용하여 보안 정책을 구성할 수 있습니다.

```java
package com.example.authenticatingldap;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;


@Configuration
public class WebSecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .authorizeRequests()
        .anyRequest().fullyAuthenticated()
        .and()
      .formLogin();

    return http.build();
  }

  @Autowired
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .ldapAuthentication()
        .userDnPatterns("uid={0},ou=people")
        .groupSearchBase("ou=groups")
        .contextSource()
          .url("ldap://localhost:8389/dc=springframework,dc=org")
          .and()
        .passwordCompare()
          .passwordEncoder(new BCryptPasswordEncoder())
          .passwordAttribute("userPassword");
  }

}

//@Configuration
//public class WebSecurityConfig {

//  @Bean
//  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
//    return http
//      .authorizeRequests()
//      .anyRequest().authenticated()
//      .and()
//      .formLogin(Customizer.withDefaults())
//      .build();
//  }
//}
```

보안 설정을 사용자 지정하려면 WebSecurityConfigurer. WebSecurityConfigurerAdapter위의 예에서 이는 인터페이스를 구현하는 메서드를 재정의하여 수행됩니다 WebSecurityConfigurer.

LDAP 서버도 필요합니다. Spring Boot는 이 가이드에 사용되는 순수 Java로 작성된 임베디드 서버에 대한 자동 구성을 제공합니다. 이 메소드는 LDAP 서버에서 검색할 수 있도록 ldapAuthentication()로그인 폼의 사용자 이름을 플러그인하도록 구성합니다 . 또한 메서드는 인코더와 암호 속성의 이름을 구성합니다.{0}uid={0},ou=people,dc=springframework,dc=orgpasswordCompare()

### 사용자 데이터 설정
LDAP 서버는 LDIF(LDAP Data Interchange Format) 파일을 사용하여 사용자 데이터를 교환할 수 있습니다. spring.ldap.embedded.ldif내부 속성을 통해 application.propertiesSpring Boot는 LDIF 데이터 파일을 가져올 수 있습니다. 이를 통해 데모 데이터를 쉽게 사전 로드할 수 있습니다. 다음 목록(에서 src/main/resources/test-server.ldif)은 이 예제와 함께 작동하는 LDIF 파일을 보여줍니다.

LDIF란? 
- LDAP(Lightweight Directory Access Protocol) 서비스에 사용되는 데이터 교환 형식입니다.
- LDAP 디렉터리에 데이터를 생성, 수정, 삭제 및 검색하기 위한 일련의 작업을 설명하는 텍스트 파일 형식입니다.
- 일련의 레코드로 구성되며, 각 레코드는 속성-값 쌍으로 구성된 항목을 나타냅니다.

```ldif
dn: dc=springframework,dc=org
objectclass: top
objectclass: domain
objectclass: extensibleObject
dc: springframework

dn: ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: groups

dn: ou=subgroups,ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: subgroups

dn: ou=people,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: people

dn: ou=space cadets,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: space cadets

dn: ou=\"quoted people\",dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: "quoted people"

dn: ou=otherpeople,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: otherpeople

dn: uid=ben,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Ben Alex
sn: Alex
uid: ben
userPassword: $2a$10$c6bSeWPhg06xB1lvmaWNNe4NROmZiSpYhlocU/98HNr2MhIOiSt36

dn: uid=bob,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Bob Hamilton
sn: Hamilton
uid: bob
userPassword: bobspassword

dn: uid=joe,ou=otherpeople,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Joe Smeth
sn: Smeth
uid: joe
userPassword: joespassword

dn: cn=mouse\, jerry,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Mouse, Jerry
sn: Mouse
uid: jerry
userPassword: jerryspassword

dn: cn=slash/guy,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: slash/guy
sn: Slash
uid: slashguy
userPassword: slashguyspassword

dn: cn=quote\"guy,ou=\"quoted people\",dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: quote\"guy
sn: Quote
uid: quoteguy
userPassword: quoteguyspassword

dn: uid=space cadet,ou=space cadets,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Space Cadet
sn: Cadet
uid: space cadet
userPassword: spacecadetspassword



dn: cn=developers,ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: groupOfUniqueNames
cn: developers
ou: developer
uniqueMember: uid=ben,ou=people,dc=springframework,dc=org
uniqueMember: uid=bob,ou=people,dc=springframework,dc=org

dn: cn=managers,ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: groupOfUniqueNames
cn: managers
ou: manager
uniqueMember: uid=ben,ou=people,dc=springframework,dc=org
uniqueMember: cn=mouse\, jerry,ou=people,dc=springframework,dc=org

dn: cn=submanagers,ou=subgroups,ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: groupOfUniqueNames
cn: submanagers
ou: submanager
uniqueMember: uid=ben,ou=people,dc=springframework,dc=org
```
```
LDIF 파일을 사용하는 것은 프로덕션 시스템의 표준 구성이 아닙니다. 그러나 테스트 목적이나 가이드에는 유용합니다.
```

http://localhost:8080 사이트를 방문하면 Spring Security에서 제공하는 로그인 페이지로 리디렉션되어야 합니다.

사용자의 이름 ben과 사용자의 암호 benspassword를 입력합니다 .<br> 
브라우저에 다음 메시지가 표시되어야 합니다.

```
Welcome to the home page!
```
