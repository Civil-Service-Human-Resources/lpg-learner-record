package uk.gov.cslearning.record;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.cslearning.record.util.WireMockServer;
import uk.gov.cslearning.record.util.stub.StubService;

import java.time.Instant;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles({"wiremock", "h2"})
@Import(SpringTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTestBase extends WireMockServer {

    protected MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext context;
    @Autowired
    protected StubService stubService;
    protected String userUid = "userId";

    protected Jwt getJwt() {
        return new Jwt("token", Instant.now(), Instant.MAX, Map.of("alg", "none"),
                Map.of(
                        JwtClaimNames.SUB, userUid,
                        "user_name", userUid
                ));
    }

    @BeforeEach
    public void setup() {
        SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtPostProcessor = jwt()
                .jwt(getJwt())
                .authorities(new SimpleGrantedAuthority("LEARNER"));
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .defaultRequest(get("/").with(jwtPostProcessor))
                .defaultRequest(post("/").with(jwtPostProcessor))
                .defaultRequest(put("/").with(jwtPostProcessor))
                .alwaysDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .build();
    }
}
