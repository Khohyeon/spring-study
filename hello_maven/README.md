## Maven으로 Java 프로젝트 빌드
이 가이드는 Maven을 사용하여 간단한 Java 프로젝트를 빌드하는 과정을 안내합니다.

### 무엇을 만들 것인가
하루 중 시간을 제공하는 애플리케이션을 만든 다음 Maven으로 빌드합니다.

### 필요한 것
* 약 15분

* 선호하는 텍스트 편집기 또는 IDE

* 자바 17 이상

* 메이븐 3.5+

코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작 하려면 프로젝트 설정 으로 이동합니다 .

기본 사항을 건너뛰 려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다 .git clone https://github.com/spring-guides/gs-maven.git

* cd 로gs-maven/initial

* [initial] 로 이동하십시오 .

작업을 마치면 의 코드와 비교하여 결과를 확인할 수 있습니다 gs-maven/complete.

### 프로젝트 설정
먼저 빌드할 Maven용 Java 프로젝트를 설정해야 합니다. Maven에 계속 초점을 맞추려면 지금은 프로젝트를 가능한 한 간단하게 만드십시오. 선택한 프로젝트 폴더에 이 구조를 만듭니다.

디렉토리 구조 생성
선택한 프로젝트 디렉터리에서 다음 하위 디렉터리 구조를 만듭니다. 예를 들어, mkdir -p src/main/java/hello *nix 시스템에서:
```
└── src
    └── main
        └── java
            └── hello

```

디렉토리 내에서 src/main/java/hello 원하는 Java 클래스를 작성할 수 있습니다. 이 가이드의 나머지 부분과 일관성을 유지하려면 다음 두 클래스를 만듭니다. HelloWorld.java및 Greeter.java.

src/main/java/hello/HelloWorld.java

```java
package hello;

public class HelloWorld {
  public static void main(String[] args) {
    Greeter greeter = new Greeter();
    System.out.println(greeter.sayHello());
  }
}
```

src/main/java/hello/Greeter.java

```java
package hello;

public class Greeter {
  public String sayHello() {
    return "Hello world!";
  }
}
```

이제 Maven으로 빌드할 준비가 된 프로젝트가 있으므로 다음 단계는 Maven을 설치하는 것입니다.

Maven은 https://maven.apache.org/download.cgi 에서 zip 파일로 다운로드할 수 있습니다 . 바이너리만 필요하므로 apache-maven- {version} -bin.zip 또는 apache-maven- {version} -bin.tar.gz 에 대한 링크를 찾으십시오.

zip 파일을 다운로드했으면 컴퓨터에 압축을 풉니다. 그런 다음 경로에 bin 폴더를 추가합니다.

Maven 설치를 테스트하려면 mvn명령줄에서 다음을 실행합니다.

```maven
mvn -v
```

터미널 창

```
"C:\Users\HoHyeon Kim\.jdks\corretto-17.0.7\bin\java.exe" "-Dmaven.multiModuleProjectDirectory=C:\Users\HoHyeon Kim\IdeaProjects\spring-guide\spring-study\hello-maven" -Djansi.passthrough=true "-Dmaven.home=C:\Users\HoHyeon Kim\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\231.9011.34\plugins\maven\lib\maven3" "-Dclassworlds.conf=C:\Users\HoHyeon Kim\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\231.9011.34\plugins\maven\lib\maven3\bin\m2.conf" "-Dmaven.ext.class.path=C:\Users\HoHyeon Kim\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\231.9011.34\plugins\maven\lib\maven-event-listener.jar" "-javaagent:C:\Users\HoHyeon Kim\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\231.9011.34\lib\idea_rt.jar=61542:C:\Users\HoHyeon Kim\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\231.9011.34\bin" -Dfile.encoding=UTF-8 -classpath "C:\Users\HoHyeon Kim\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\231.9011.34\plugins\maven\lib\maven3\boot\plexus-classworlds-2.6.0.jar;C:\Users\HoHyeon Kim\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\231.9011.34\plugins\maven\lib\maven3\boot\plexus-classworlds.license" org.codehaus.classworlds.Launcher -Didea.version=2023.1.2 -v
Apache Maven 3.8.1 (05c21c65bdfed0f71a2f2ada8b84da59348c4c5d)
Maven home: C:\Users\HoHyeon Kim\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\231.9011.34\plugins\maven\lib\maven3
Java version: 17.0.7, vendor: Amazon.com Inc., runtime: C:\Users\HoHyeon Kim\.jdks\corretto-17.0.7
Default locale: ko_KR, platform encoding: UTF-8
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

이제 Maven이 설치되었으므로 Maven을 사용하여 프로젝트를 빌드할 수 있습니다.

### 간단한 Maven 빌드 정의
이제 Maven이 설치되었으므로 Maven 프로젝트 정의를 만들어야 합니다. Maven 프로젝트는 pom.xml 이라는 XML 파일로 정의됩니다 . 무엇보다도 이 파일은 프로젝트의 이름, 버전 및 외부 라이브러리에 대한 종속성을 제공합니다.

프로젝트의 루트에 pom.xml 이라는 파일을 생성하고 (즉 src, 폴더 옆에 배치) 다음 내용을 제공합니다.

pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.springframework</groupId>
    <artifactId>gs-maven</artifactId>
    <packaging>jar</packaging>
    <version>0.1.0</version>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.2.4</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer
                                    implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>hello.HelloWorld</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

선택적 요소를 제외하고 이것은 Java 프로젝트를 빌드하는 데 필요한 <packaging>가장 간단한 pom.xml 파일입니다. 여기에는 프로젝트 구성에 대한 다음 세부 정보가 포함됩니다.

* modelVersion - POM 모델 버전(항상 4.0.0).

* groupId - 프로젝트가 속한 그룹 또는 조직. 종종 반전된 도메인 이름으로 표현됩니다.

* artifactId - 프로젝트의 라이브러리 아티팩트에 지정되는 이름입니다(예: 해당 JAR 또는 WAR 파일의 이름).

* version - 빌드 중인 프로젝트의 버전입니다.

* packaging - 프로젝트를 패키징하는 방법. JAR 파일 패키징의 경우 기본값은 "jar"입니다. WAR 파일 패키징에는 "war"를 사용하십시오.

### 자바 코드 빌드
이제 Maven이 프로젝트를 빌드할 준비가 되었습니다. 프로젝트의 코드를 컴파일하고 라이브러리 패키지(예: JAR 파일)를 만들고 로컬 Maven 종속성 저장소에 라이브러리를 설치하는 목표를 포함하여 이제 Maven을 사용하여 여러 빌드 수명 주기 목표를 실행할 수 있습니다.

빌드를 시도하려면 명령줄에서 다음을 실행합니다.
```shell
mvn compile
```

그러면 Maven이 실행되어 컴파일 목표를 실행하도록 지시합니다 . 완료되면 target/classes 디렉터리 에서 컴파일된 .class 파일을 찾아야 합니다 .

.class 파일을 직접 배포하거나 작업할 가능성은 거의 없으므로 패키지 목표를 대신 실행하고 싶을 것입니다 .

```shell
mvn package
```

그러면 Maven이 실행되어 컴파일 목표를 실행하도록 지시합니다 . 완료되면 target/classes 디렉터리 에서 컴파일된 .class 파일을 찾아야 합니다 .

.class 파일을 직접 배포하거나 작업할 가능성은 거의 없으므로 패키지 목표를 대신 실행하고 싶을 것입니다 .

```
java -jar target/gs-maven-0.1.0.jar
```

또한 Maven은 프로젝트 종속성에 빠르게 액세스할 수 있도록 로컬 시스템(일반적으로 홈 디렉터리의 .m2/repository 디렉터리) 에 대한 종속성 저장소를 유지 관리합니다 . 프로젝트의 JAR 파일을 해당 로컬 저장소에 설치하려면 목표를 호출해야 합니다 install.

```shell
mvc install
```


### 종속성 선언
간단한 Hello World 샘플은 완전히 독립적이며 추가 라이브러리에 의존하지 않습니다. 그러나 대부분의 응용 프로그램은 일반적이고 복잡한 기능을 처리하기 위해 외부 라이브러리에 의존합니다.

예를 들어 "Hello World!"라고 말하는 것 외에도 애플리케이션이 현재 날짜와 시간을 인쇄하기를 원한다고 가정합니다. 기본 Java 라이브러리에서 날짜 및 시간 기능을 사용할 수 있지만 Joda Time 라이브러리를 사용하여 더 흥미롭게 만들 수 있습니다.

먼저 HelloWorld.java를 다음과 같이 변경합니다.

src/main/java/hello/HelloWorld.java

```java
package hello;

import org.joda.time.LocalTime;

public class HelloWorld {
  public static void main(String[] args) {
    LocalTime currentTime = new LocalTime();
    System.out.println("The current local time is: " + currentTime);
    Greeter greeter = new Greeter();
    System.out.println(greeter.sayHello());
  }
}
```

여기서 HelloWorldJoda Time의 LocalTime클래스를 사용하여 현재 시간을 가져오고 인쇄합니다.

mvn compile지금 프로젝트를 빌드하기 위해 실행하는 경우 빌드에서 Joda Time을 컴파일 종속성으로 선언하지 않았기 때문에 빌드가 실패합니다. pom.xml ( <project>요소 내) 에 다음 줄을 추가하여 문제를 해결할 수 있습니다 .

```xml
<dependencies>
		<dependency>
			<groupId>joda-time</groupId>
			<artifactId>joda-time</artifactId>
			<version>2.9.2</version>
		</dependency>
</dependencies>
```

이 XML 블록은 프로젝트에 대한 종속성 목록을 선언합니다. 특히 Joda Time 라이브러리에 대한 단일 종속성을 선언합니다. 요소 내에서 <dependency>종속성 좌표는 세 가지 하위 요소로 정의됩니다.

* groupId - 종속성이 속한 그룹 또는 조직입니다.

* artifactId - 필요한 라이브러리.

* version - 필요한 라이브러리의 특정 버전.

기본적으로 모든 종속성은 compile종속성으로 범위가 지정됩니다. 즉, 컴파일 타임에 사용할 수 있어야 합니다( WAR의 /WEB-INF/libs 폴더를 포함하여 WAR 파일을 빌드하는 경우). <scope>또한 다음 범위 중 하나를 지정하는 요소를 지정할 수 있습니다 .

* provided- 프로젝트 코드를 컴파일하는 데 필요하지만 코드를 실행하는 컨테이너(예: Java Servlet API)에 의해 런타임에 제공되는 종속성.

* test- 테스트 컴파일 및 실행에 사용되지만 프로젝트의 런타임 코드를 빌드하거나 실행하는 데 필요하지 않은 종속성.

mvn compile이제 또는 를 실행하면 mvn packageMaven이 Maven Central 리포지토리에서 Joda Time 종속성을 해결하고 빌드가 성공합니다.

### 테스트 작성
먼저 테스트 범위에서 JUnit을 pom.xml에 대한 종속성으로 추가합니다.

```maven
<dependency>
	<groupId>junit</groupId>
	<artifactId>junit</artifactId>
	<version>4.12</version>
	<scope>test</scope>
</dependency>
```

그런 다음 다음과 같은 테스트 사례를 만듭니다.

src/test/java/hello/GreeterTest.java

```java
package hello;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import org.junit.Test;

public class GreeterTest {
  
  private Greeter greeter = new Greeter();

  @Test
  public void greeterSaysHello() {
    assertThat(greeter.sayHello(), containsString("Hello"));
  }

}
```

Maven은 "surefire"라는 플러그인을 사용하여 단위 테스트를 실행합니다. 이 플러그인의 기본 구성은 src/test/java이름이 일치하는 모든 클래스를 컴파일하고 실행합니다 *Test. 다음과 같이 명령줄에서 테스트를 실행할 수 있습니다.

```shell
mvn test
```

또는 mvn install위에서 이미 보여준 대로 단계를 사용하십시오("설치"의 단계로 "테스트"가 포함된 수명 주기 정의가 있습니다).

완성된 파일은 다음과 같습니다 pom.xml.

pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	
	<groupId>org.springframework</groupId>
	<artifactId>gs-maven</artifactId>
	<packaging>jar</packaging>
	<version>0.1.0</version>

	<properties>
		<maven.compiler.source>1.8</maven.compiler.source>
		<maven.compiler.target>1.8</maven.compiler.target>
	</properties>

	<dependencies>
		<!-- tag::joda[] -->
		<dependency>
			<groupId>joda-time</groupId>
			<artifactId>joda-time</artifactId>
			<version>2.9.2</version>
		</dependency>
		<!-- end::joda[] -->
		<!-- tag::junit[] -->
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.12</version>
			<scope>test</scope>
		</dependency>
		<!-- end::junit[] -->
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-shade-plugin</artifactId>
				<version>3.2.4</version>
				<executions>
					<execution>
						<phase>package</phase>
						<goals>
							<goal>shade</goal>
						</goals>
						<configuration>
							<transformers>
								<transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
									<mainClass>hello.HelloWorld</mainClass>
								</transformer>
							</transformers>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>

</project>
```

```
완성된 pom.xml 파일은 JAR 파일을 실행 가능하게 만드는 간단한 편의를 위해 Maven Shade Plugin을 사용하고 있습니다 . 이 가이드의 초점은 이 특정 플러그인을 사용하는 것이 아니라 Maven으로 시작하는 것입니다.
```