## Spring Cloud Vault

### Vault Configuration
이 가이드는 Spring Cloud Vault를 사용하여 HashiCorp Vault 에서 구성 속성을 검색하는 애플리케이션을 빌드하는 과정을 안내합니다.

### 무엇을 만들 것인가
Vault를 시작하고, Vault 내부에 구성 속성을 저장하고, Spring 애플리케이션을 빌드하고 Vault와 연결합니다.

### 필요한 것
* 약 15분의 시간
* 좋아하는 텍스트 편집기 또는 IDE
* 자바 17 이상
* Gradle 7.5+ 또는 Maven 3.5+
* 코드를 IDE로 바로 가져올 수도 있습니다.
  * 스프링 툴 스위트(STS)
  * IntelliJ IDEA
  * VSCode

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작 하려면 Build with Gradle 로 이동합니다 .

기본 사항을 건너뛰 려면 다음을 수행하십시오.
* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-vault-config.git
* cd 로gs-vault-config/initial
* HashiCorp Vault 설치 및 실행 으로 이동하십시오 
작업을 마치면 gs-vault-config/complete의 코드와 비교하여 결과를 확인할 수 있습니다.

### Gradle로 빌드
먼저 기본 빌드 스크립트를 설정합니다. Spring으로 앱을 빌드할 때 원하는 빌드 시스템을 사용할 수 있지만 Gradle 및 Maven 과 함께 작업하는 데 필요한 코드가 여기에 포함되어 있습니다. 익숙하지 않은 경우 Gradle을 사용하여 Java 프로젝트 빌드 또는 Maven을 사용하여 Java 프로젝트 빌드를 참조하십시오 .

Gradle 빌드 파일 만들기
아래는 초기 Gradle 빌드 파일 입니다.

`build.gradle`
```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.0.0'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'
}

group = 'gs-vault-config'
version = '0.1.0'
sourceCompatibility = '1.8'

repositories {
    mavenCentral()
    maven {
        url "https://repo.spring.io/milestone/"
    }
}

ext {
    set('springCloudVersion', "2022.0.0-RC3")
}

dependencies {
    implementation('org.springframework.cloud:spring-cloud-starter-vault-config')
    testImplementation('org.springframework.boot:spring-boot-starter-test')
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}

tasks.named('test') {
    useJUnitPlatform()
}
```
Spring Boot Gradle 플러그인은 많은 편리한 기능을 제공합니다.

* 클래스 경로에 있는 모든 jar를 수집하고 실행 가능한 단일 "über-jar"를 빌드하여 서비스를 보다 편리하게 실행하고 전송할 수 있습니다.
* public static void main()실행 가능한 클래스로 표시할 메서드를 검색합니다 .
* Spring Boot 종속성 과 일치하도록 버전 번호를 설정하는 내장 종속성 확인자를 제공합니다 . 원하는 버전을 재정의할 수 있지만 기본적으로 Boot에서 선택한 버전 집합이 됩니다.

### Maven으로 구축
먼저 기본 빌드 스크립트를 설정합니다. Spring으로 앱을 빌드할 때 원하는 빌드 시스템을 사용할 수 있지만 Maven 으로 작업하는 데 필요한 코드가 여기에 포함되어 있습니다. Maven에 익숙하지 않은 경우 Maven으로 Java 프로젝트 빌드 를 참조하십시오 .
`pom.xml`
```xml 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.springframework</groupId>
    <artifactId>gs-vault-config</artifactId>
    <version>0.1.0</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.0.0</version>
    </parent>

    <dependencies>

        <!-- Vault Starter -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-vault-config</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <properties>
        <spring-cloud.version>2022.0.0-RC3</spring-cloud.version>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

    <repositories>
        <repository>
            <id>spring-libs-milestone</id>
            <url>https://repo.spring.io/libs-milestone</url>
        </repository>
    </repositories>

</project>
```
Spring Boot Maven 플러그인은 많은 편리한 기능을 제공합니다.

* 클래스 경로에 있는 모든 jar를 수집하고 실행 가능한 단일 "über-jar"를 빌드하여 서비스를 보다 편리하게 실행하고 전송할 수 있습니다.
* public static void main()실행 가능한 클래스로 표시할 메서드를 검색합니다 .
* Spring Boot 종속성 과 일치하도록 버전 번호를 설정하는 내장 종속성 확인자를 제공합니다 . 원하는 버전을 재정의할 수 있지만 기본적으로 Boot에서 선택한 버전 집합이 됩니다.

### IDE로 구축
* 이 가이드를 Spring Tool Suite 로 바로 가져오는 방법을 읽어보세요.
* IntelliJ IDEA 에서 이 가이드로 작업하는 방법을 읽어보세요.

### HashiCorp Vault 설치 및 실행
프로젝트가 설정되면 HashiCorp Vault를 설치하고 실행할 수 있습니다.

홈브류와 함께 Mac을 사용하는 경우 다음과 같이 간단합니다.
```shell
$ brew install vault
```

또는 https://www.vaultproject.io/downloads.html 에서 운영 체제용 Vault를 다운로드합니다.
```shell
$ https://releases.hashicorp.com/vault/1.12.2/vault_1.12.2_darwin_amd64.zip 
$ unzip vault_1.12.2_darwin_amd64.zip
```
Redhat, Ubuntu, Debian, CentOS 및 Windows와 같은 패키지 관리 기능이 있는 다른 시스템의 경우 https://www.vaultproject.io/docs/install/index.html 의 지침을 참조하십시오 .

Vault를 설치한 후 콘솔 창에서 실행합니다. 이 명령은 또한 서버 프로세스를 시작합니다.

```shell
$ vault server --dev --dev-root-token-id="00000000-0000-0000-0000-000000000000"
```
마지막 출력 라인 중 하나로 다음이 표시되어야 합니다.
```shell
[INFO] core: post-unseal setup complete
```

> 위의 명령은 전송 암호화 없이 메모리 내 저장소를 사용하여 개발 모드에서 Vault를 시작합니다. 로컬에서 Vault를 평가하는 데 적합합니다. 생산용으로 적절한 SSL 인증서와 신뢰할 수 있는 스토리지 백엔드를 사용해야 합니다. 자세한 내용은 Vault의 생산 강화 가이드(https://www.vaultproject.io/guides/production.html)를 참조하세요.

### Vault에 구성 저장
Vault는 미사용 상태에서 암호화된 중요한 데이터를 저장할 수 있는 비밀 관리 시스템입니다. 암호, 암호화 키, API 키와 같은 민감한 구성 세부 정보를 저장하는 것이 이상적입니다.

Vault 명령줄을 사용하여 Vault에 응용 프로그램 구성을 저장하려면 다른 콘솔 창을 실행하십시오.

먼저 Vault CLI가 Vault 끝점을 가리키도록 두 개의 환경 변수를 설정하고 인증 토큰을 제공해야 합니다.

```shell
$ export export VAULT_TOKEN="00000000-0000-0000-0000-000000000000" 
$ export VAULT_ADDR="http://127.0.0.1:8200"
```
이제 Vault 내에 구성 키-값 쌍을 저장할 수 있습니다.
```shell
$ vault kv put secret/gs-vault-config example.username=demouser example.password=demopassword 
$ vault kv put secret/gs-vault-config/cloud example.username=clouduser example.password=cloudpassword
```

### 구성 클래스 정의
Spring 애플리케이션에 대한 간단한 구성을 만듭니다.

src/main/java/com/example/springcloudvault/MyConfiguration.java
```java
package com.example.springcloudvault;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("example")
public class MyConfiguration {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```
### 애플리케이션 구성
여기에서 application.properties. 아래 코드는 Vault에서 구성을 가져올 수 있는 Spring Boot의 구성 데이터 API를 사용합니다.

src/main/resources/application.properties

```properties
spring.application.name=vault-config
spring.cloud.vault.token=00000000-0000-0000-0000-000000000000
spring.cloud.vault.scheme=http
spring.cloud.vault.kv.enabled=true
spring.config.import= vault://
```

### 애플리케이션 클래스 생성
여기에서 모든 구성 요소가 포함된 Application 클래스를 만듭니다.

src/main/java/com/example/springcloudvault/SpringCloudVaultApplication.java
```java
package com.example.springcloudvault;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MyConfiguration.class)
public class SpringCloudVaultApplication implements CommandLineRunner {

    private final MyConfiguration myConfiguration;

    public SpringCloudVaultApplication(MyConfiguration myConfiguration) {
        this.myConfiguration = myConfiguration;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudVaultApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        Logger logger = LoggerFactory.getLogger(SpringCloudVaultApplication.class);

        logger.info("----------------------------------------");
        logger.info("Configuration properties");
        logger.info("   example.username is {}", myConfiguration.getUsername());
        logger.info("   example.password is {}", myConfiguration.getPassword());
        logger.info("----------------------------------------");
    }
}
```
Spring Cloud Vault는 VaultOperationsVault와 상호 작용하는 데 사용됩니다. MyConfigurationVault의 속성은 형식이 안전한 액세스를 위해 매핑됩니다 . @EnableConfigurationProperties(MyConfiguration.class)구성 속성 매핑을 활성화하고 MyConfiguration빈을 등록합니다.

Applicationmain()의 인스턴스를 자동 연결하는 메서드를 포함합니다 MyConfiguration.

실행 가능한 JAR 빌드
Gradle 또는 Maven을 사용하여 명령줄에서 애플리케이션을 실행할 수 있습니다. 필요한 모든 종속성, 클래스 및 리소스를 포함하는 단일 실행 가능 JAR 파일을 빌드하고 실행할 수도 있습니다. 실행 가능한 jar을 빌드하면 개발 수명 주기 전체, 다양한 환경 등에 서비스를 애플리케이션으로 쉽게 제공, 버전 지정 및 배포할 수 있습니다.

Gradle을 사용하는 경우 ./gradlew bootRun. ./gradlew build또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```shell
java -jar build/libs/gs-vault-config-0.1.0.jar
```
Maven을 사용하는 경우 ./mvnw spring-boot:run. ./mvnw clean package또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```shell
java -jar target/gs-vault-config-0.1.0.jar
```

>
여기에 설명된 단계는 실행 가능한 JAR을 만듭니다. 클래식 WAR 파일을 빌드(https://spring.io/guides/gs/convert-jar-to-war/) 할 수도 있습니다 .

Application구현 으로 CommandLineRunner메서드 run는 부팅이 시작될 때 자동으로 호출됩니다. 다음과 같은 내용이 표시되어야 합니다.

```shell
----------------------------------------
Configuration properties
        example.username is demouser
        example.password is demopassword
----------------------------------------
```
이제 활성화된 프로파일로 애플리케이션을 시작하십시오 cloud. 다음과 같은 내용이 표시되어야 합니다.
```shell
----------------------------------------
Configuration properties
        example.username is clouduser
        example.password is cloudpassword
----------------------------------------
```
구성 속성은 활성화된 프로필에 따라 바인딩됩니다. Spring Cloud Vault는 프로필 이름( ) 을 추가하는 Vault 컨텍스트 경로를 구성하므로 spring.application.name프로필 을 활성화하면 에서 추가로 구성 속성을 가져옵니다 .gs-vaultcloudcloudsecret/gs-vault-config/cloud
