package br.gov.sifap.catalogo.infrastructure;

import br.gov.sifap.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProgramaSocialControllerTest {

    private static final String CAMINHO = "/api/v1/programas-sociais";

    private final MockMvc mockMvc;

    @Autowired
    ProgramaSocialControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    private static String corpo(String codigo, String valorBase) {
        return """
                {
                  "codigo": "%s",
                  "nome": "Bolsa Familia",
                  "tipo": "A",
                  "valorBase": %s,
                  "fatorAjuste": 0.0500,
                  "codigoElegibilidade": "E0001",
                  "dataCriacao": "2026-01-15",
                  "rendaPercapitaMaxima": 218.00,
                  "idadeMinima": 0,
                  "idadeMaxima": 17
                }
                """.formatted(codigo, valorBase);
    }

    private void incluir(String codigo) throws Exception {
        mockMvc.perform(post(CAMINHO).contentType(MediaType.APPLICATION_JSON).content(corpo(codigo, "600.00")))
                .andExpect(status().isCreated());
    }

    @Test
    void should_return_created_with_location_when_program_is_accepted() throws Exception { // REQ-001
        mockMvc.perform(post(CAMINHO).contentType(MediaType.APPLICATION_JSON).content(corpo("B001", "600.00")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith(CAMINHO + "/B001")))
                .andExpect(jsonPath("$.situacao").value("A"));
    }

    @Test
    void should_return_the_six_published_fields_when_program_is_found() throws Exception { // REQ-011
        incluir("B002");

        mockMvc.perform(get(CAMINHO + "/B002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("B002"))
                .andExpect(jsonPath("$.valorBase").value(600.00))
                .andExpect(jsonPath("$.length()").value(6));
    }

    @Test
    void should_return_not_found_as_problem_json_when_program_is_absent() throws Exception { // REQ-012
        mockMvc.perform(get(CAMINHO + "/8888"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.codigo").value("8888"));
    }

    @Test
    void should_return_bad_request_as_problem_json_when_amount_exceeds_the_ceiling() throws Exception { // REQ-006
        mockMvc.perform(post(CAMINHO).contentType(MediaType.APPLICATION_JSON).content(corpo("B003", "100000.00")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.campo").value("valorBase"));
    }

    @Test
    void should_return_conflict_when_code_already_exists() throws Exception { // REQ-002
        incluir("B004");

        mockMvc.perform(post(CAMINHO).contentType(MediaType.APPLICATION_JSON).content(corpo("B004", "600.00")))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void should_reject_any_operation_other_than_insertion_and_query() throws Exception { // REQ-010
        incluir("B005");

        mockMvc.perform(delete(CAMINHO + "/B005"))
                .andExpect(status().isMethodNotAllowed());
    }
}
