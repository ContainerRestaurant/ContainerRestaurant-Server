package container.restaurant.server.web;

import container.restaurant.server.domain.contract.Contract;
import container.restaurant.server.domain.contract.ContractRepository;
import container.restaurant.server.domain.contract.ContractService;
import container.restaurant.server.web.base.BaseMvcControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ContractControllerTest extends BaseMvcControllerTest {

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    ContractService contractService;

    @Test
    @DisplayName("약관 테스트")
    void getContract() throws Exception {
        for (int i = 0; i < 3; i++) {
            contractRepository.save(Contract.builder()
                    .title("title" + i)
                    .article("article" + i)
                    .build());
        }
        contractService.putContract();

        ResultActions perform = mvc.perform(get("/api/contract"));

        perform
                .andExpect(status().isOk())
                .andDo(document("contracts"))
                .andDo(document("contract",
                        responseFields(beneathPath("_embedded.contractInfoDtoList"),
                                fieldWithPath("title").description("이용 약관 제목"),
                                fieldWithPath("article").description("이용 약관 내용")
                        )));
    }
}