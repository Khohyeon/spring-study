### 파일 업로드
이 가이드는 HTTP 멀티파트 파일 업로드를 수신할 수 있는 서버 응용 프로그램을 만드는 과정을 안내합니다.

#### 무엇을 만들 것인가
파일 업로드를 허용하는 Spring Boot 웹 애플리케이션을 생성합니다. 또한 테스트 파일을 업로드하기 위한 간단한 HTML 인터페이스를 구축합니다.

### 필요한 것
* 약 15분
* 선호하는 텍스트 편집기 또는 IDE
* 자바 17 이상
* Gradle 7.5+ 또는 Maven 3.5+
* 코드를 IDE로 바로 가져올 수도 있습니다.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용 하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:
1. https://start.spring.io 로 이동합니다 . 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭 하고 Spring Web 및 Thymeleaf를 선택하십시오 .

4. 생성 을 클릭합니다 .

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 애플리케이션 클래스 생성
Spring Boot MVC 애플리케이션을 시작하려면 먼저 스타터가 필요합니다. 이 샘플에서는 spring-boot-starter-thymeleaf및가 spring-boot-starter-web이미 종속 항목으로 추가되었습니다. MultipartConfigElementServlet 컨테이너로 파일을 업로드하려면 클래스(web.xml에 있음 ) 를 등록해야 합니다 <multipart-config>. Spring Boot 덕분에 모든 것이 자동으로 구성됩니다!

이 응용 프로그램을 시작하는 데 필요한 것은 다음 UploadingFilesApplication클래스(출처 src/main/java/com/example/uploadingfiles/UploadingFilesApplication.java)입니다.

```agsl
package com.example.uploadingfiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UploadingFilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploadingFilesApplication.class, args);
	}

}
```

자동 구성 Spring MVC의 일부로 Spring Boot는 MultipartConfigElementbean을 생성하고 파일 업로드 준비를 합니다.

### 파일 업로드 컨트롤러 만들기
초기 애플리케이션에는 업로드된 파일을 디스크에 저장하고 로드하는 작업을 처리하는 몇 가지 클래스가 이미 포함되어 있습니다. 그들은 모두 패키지에 있습니다 com.example.uploadingfiles.storage. 새 FileUploadController. 다음 목록(에서 src/main/java/com/example/uploadingfiles/FileUploadController.java)은 파일 업로드 컨트롤러를 보여줍니다.

```agsl
package com.example.uploadingfiles;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import com.example.uploadingfiles.storage.StorageService;

@Controller
public class FileUploadController {

	private final StorageService storageService;

	@Autowired
	public FileUploadController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {

		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
						"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList()));

		return "uploadForm";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {

		storageService.store(file);
		redirectAttributes.addFlashAttribute("message",
				"You successfully uploaded " + file.getOriginalFilename() + "!");

		return "redirect:/";
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
```

Spring MVC가 클래스를 선택하고 경로를 찾을 수 있도록 클래스 FileUploadController에 주석이 달려 있습니다 . @Controller각 메서드에는 경로 및 HTTP 작업을 특정 컨트롤러 작업에 연결하기 위해 @GetMapping태그 가 지정됩니다.@PostMapping

* GET /: 에서 현재 업로드된 파일 목록을 조회하여 StorageServiceThymeleaf 템플릿에 로드합니다. 를 사용하여 실제 리소스에 대한 링크를 계산합니다 MvcUriComponentsBuilder.

* GET /files/{filename}: 리소스(있는 경우)를 로드하고 Content-Disposition응답 헤더를 사용하여 다운로드하도록 브라우저로 보냅니다.

* POST /: 여러 부분으로 구성된 메시지를 처리 file하고 저장을 위해 에 제공합니다 StorageService.


StorageService컨트롤러가 스토리지 계층(예: 파일 시스템)과 상호 작용할 수 있도록 를 제공해야 합니다 . 다음 목록(에서 src/main/java/com/example/uploadingfiles/storage/StorageService.java)은 해당 인터페이스를 보여줍니다.
```java
package com.example.uploadingfiles.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

	void init();

	void store(MultipartFile file);

	Stream<Path> loadAll();

	Path load(String filename);

	Resource loadAsResource(String filename);

	void deleteAll();

}
```

### HTML 템플릿 만들기
다음 Thymeleaf 템플릿(에서 src/main/resources/templates/uploadForm.html)은 파일을 업로드하고 업로드된 내용을 표시하는 방법의 예를 보여줍니다.

```html
<html xmlns:th="https://www.thymeleaf.org">
<body>

<div th:if="${message}">
    <h2 th:text="${message}"/>
</div>

<div>
    <form method="POST" enctype="multipart/form-data" action="/">
        <table>
            <tr><td>File to upload:</td><td><input type="file" name="file" /></td></tr>
            <tr><td></td><td><input type="submit" value="Upload" /></td></tr>
        </table>
    </form>
</div>

<div>
    <ul>
        <li th:each="file : ${files}">
            <a th:href="${file}" th:text="${file}" />
        </li>
    </ul>
</div>

</body>
</html>
```

#### 이 템플릿은 세 부분으로 구성됩니다.

* Spring MVC가 플래시 범위 메시지를 작성하는 상단의 선택적 메시지 .

* 사용자가 파일을 업로드할 수 있는 양식입니다.

* 백엔드에서 제공되는 파일 목록입니다.

### 파일 업로드 제한 조정
파일 업로드를 구성할 때 파일 크기에 대한 제한을 설정하는 것이 유용한 경우가 많습니다. 5GB 파일 업로드를 처리한다고 상상해 보십시오! MultipartConfigElementSpring Boot를 사용하면 일부 속성 설정으로 자동 구성을 조정할 수 있습니다 .

기존 속성 설정에 다음 속성을 추가합니다(에서 src/main/resources/application.properties).

```properties
spring.servlet.multipart.max-file-size=128KB
spring.servlet.multipart.max-request-size=128KB
```

### 애플리케이션 실행
파일을 업로드할 대상 폴더가 필요하므로 Spring Initializr가 생성한 기본 클래스를 개선 하고 시작 시 해당 폴더를 삭제하고 다시 생성하는 UploadingFilesApplicationBoot를 추가 해야 합니다. CommandLineRunner다음 목록(에서 src/main/java/com/example/uploadingfiles/UploadingFilesApplication.java)은 이를 수행하는 방법을 보여줍니다.

```java
package com.example.uploadingfiles;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.example.uploadingfiles.storage.StorageProperties;
import com.example.uploadingfiles.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class UploadingFilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploadingFilesApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}
```

@SpringBootApplication다음을 모두 추가하는 편의 주석입니다.

* @Configuration: 애플리케이션 컨텍스트에 대한 빈 정의의 소스로 클래스에 태그를 지정합니다.

* @EnableAutoConfiguration: 클래스 경로 설정, 기타 빈 및 다양한 속성 설정을 기반으로 빈 추가를 시작하도록 Spring Boot에 지시합니다. 예를 들어 spring-webmvc클래스 경로에 있는 경우 이 주석은 애플리케이션을 웹 애플리케이션으로 플래그 지정하고 DispatcherServlet.

* @ComponentScancom/example: 컨트롤러를 찾을 수 있도록 패키지 에서 다른 구성 요소, 구성 및 서비스를 찾도록 Spring에 지시합니다

이 main()방법은 Spring Boot의 SpringApplication.run()방법을 사용하여 애플리케이션을 시작합니다. XML이 한 줄도 없다는 사실을 눈치채셨나요? web.xml파일도 없습니다 . 이 웹 애플리케이션은 100% 순수 Java이며 배관이나 인프라 구성을 처리할 필요가 없습니다.

### 실행 가능한 JAR 빌드
Gradle 또는 Maven을 사용하여 명령줄에서 애플리케이션을 실행할 수 있습니다. 필요한 모든 종속성, 클래스 및 리소스를 포함하는 단일 실행 가능 JAR 파일을 빌드하고 실행할 수도 있습니다. 실행 가능한 jar을 빌드하면 개발 수명 주기 전체, 다양한 환경 등에 서비스를 애플리케이션으로 쉽게 제공, 버전 지정 및 배포할 수 있습니다.

Gradle을 사용하는 경우 ./gradlew bootRun. ./gradlew build또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
``` 
java -jar builld/libs/gs-uploading-files-0.1.0.jar
```

### 애플리케이션 테스트
애플리케이션에서 이 특정 기능을 테스트하는 방법에는 여러 가지가 있습니다. 다음 목록(출처 ) 은 ​​서블릿 컨테이너를 시작할 필요가 없도록 src/test/java/com/example/uploadingfiles/FileUploadTests.java사용하는 한 가지 예를 보여줍니다 .MockMvc

```java
package com.example.uploadingfiles;

import java.nio.file.Paths;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import com.example.uploadingfiles.storage.StorageService;

@AutoConfigureMockMvc
@SpringBootTest
public class FileUploadTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private StorageService storageService;

	@Test
	public void shouldListAllFiles() throws Exception {
		given(this.storageService.loadAll())
				.willReturn(Stream.of(Paths.get("first.txt"), Paths.get("second.txt")));

		this.mvc.perform(get("/")).andExpect(status().isOk())
				.andExpect(model().attribute("files",
						Matchers.contains("http://localhost/files/first.txt",
								"http://localhost/files/second.txt")));
	}

	@Test
	public void shouldSaveUploadedFile() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
				"text/plain", "Spring Framework".getBytes());
		this.mvc.perform(multipart("/").file(multipartFile))
				.andExpect(status().isFound())
				.andExpect(header().string("Location", "/"));

		then(this.storageService).should().store(multipartFile);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void should404WhenMissingFile() throws Exception {
		given(this.storageService.loadAsResource("test.txt"))
				.willThrow(StorageFileNotFoundException.class);

		this.mvc.perform(get("/files/test.txt")).andExpect(status().isNotFound());
	}

}
```

이러한 테스트에서 다양한 모의를 사용하여 컨트롤러 및 를 StorageService사용하여 Servlet 컨테이너 자체와의 상호 작용을 설정합니다 MockMultipartFile.

통합 테스트의 예는 클래스 FileUploadIntegrationTests( 에 있음 src/test/java/com/example/uploadingfiles)를 참조하십시오.