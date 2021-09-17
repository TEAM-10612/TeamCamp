package TeamCamp.demo.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(ProductApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class ProductControllerTest {

}