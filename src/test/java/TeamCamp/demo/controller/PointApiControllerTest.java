package TeamCamp.demo.controller;

import TeamCamp.demo.domain.model.point.PointDivision;
import TeamCamp.demo.dto.PointDto;
import TeamCamp.demo.dto.TradeDto;
import TeamCamp.demo.service.PointService;
import TeamCamp.demo.service.WishListService;
import TeamCamp.demo.service.loginservice.SessionLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(PointApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class PointApiControllerTest {
    @MockBean
    private PointService pointService;

    @MockBean
    private SessionLoginService sessionLoginService;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(sharedHttpSession())
                .build();
    }

    private List<PointDto.PointHistoryDto> createPointChargeHistoryList(){
        List<PointDto.PointHistoryDto> list = new ArrayList<>();

        PointDto.PointHistoryDto  chargingData = PointDto.PointHistoryDto.builder()
                .time(LocalDateTime.now())
                .division(PointDivision.CHARGE)
                .amount(30000L)
                .build();

        list.add(chargingData);

        PointDto.PointHistoryDto  saleData = PointDto.PointHistoryDto.builder()
                .time(LocalDateTime.now())
                .division(PointDivision.SALES_REVENUE)
                .amount(40000L)
                .build();

        list.add(saleData);

        return list;
    }


    private List<PointDto.PointHistoryDto> createPointDeductionHistoryList(){
        List<PointDto.PointHistoryDto> list = new ArrayList<>();

        PointDto.PointHistoryDto  withDrawData = PointDto.PointHistoryDto.builder()
                .time(LocalDateTime.now())
                .division(PointDivision.CHARGE)
                .amount(30000L)
                .build();

        list.add(withDrawData);

        PointDto.PointHistoryDto  purchaseData = PointDto.PointHistoryDto.builder()
                .time(LocalDateTime.now())
                .division(PointDivision.SALES_REVENUE)
                .amount(40000L)
                .build();

        list.add(purchaseData);

        return list;
    }


    @Test
    @DisplayName("포인트를 충전한다.")
    void chargingPoint()throws Exception{
        //given
        PointDto.ChargeRequest request = PointDto.ChargeRequest.builder()
                .chargeAmount(100000L)
                .build();

        String email = "rdj1014@naver.com";
        //when
        doNothing().when(pointService).charging(email,request);

        //then
        mockMvc.perform(post("/points/charging")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("points/charging", requestFields(
                        fieldWithPath("chargeAmount").type(JsonFieldType.NUMBER).description("충전할 포인트")
                )));

    }

    @Test
    @DisplayName("포인트 출금")
    void withdrawalPoint()throws Exception{
        //given
        PointDto.WithdrawalRequest request = PointDto.WithdrawalRequest.builder()
                .password("0070")
                .withdrawalAmount(4000L)
                .build();

        String email = "rdj1014@naver.com";
        //when
        doNothing().when(pointService).withdrawal(email,request);

        //then

        mockMvc.perform(post("/points/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("points/withdrawal", requestFields(
                        fieldWithPath("withdrawalAmount").type(JsonFieldType.NUMBER).description("출금할 포인트"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                )));

    }

    @Test
    @DisplayName("포인트 추가 내역(판매 대금 / 충전)을 반환한다.")
    void chargingHistory()throws Exception{
        //given
        List<PointDto.PointHistoryDto> chargingHistory  = createPointChargeHistoryList();

        //when
        given(pointService.getChargingHistory(any())).willReturn(chargingHistory);

        //then
        mockMvc.perform(get("/points/charging-details")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("points/history/charging",
                        responseFields(
                        fieldWithPath("[].time").type(JsonFieldType.STRING).description("생성 날짜"),
                        fieldWithPath("[].amount").type(JsonFieldType.NUMBER).description("충전 금액 "),
                                fieldWithPath("[].division").type(JsonFieldType.STRING).description("종류")
                )));


    }

    @Test
    @DisplayName("포인트 차감 내역(구매 대금 /출금)을 반환 한다.")
    void deductionHistory()throws Exception{
        //given
        List<PointDto.PointHistoryDto> deductionHistory = createPointDeductionHistoryList();

        //when
        given(pointService.getDeductionHistory(any())).willReturn(deductionHistory);
        //then
        mockMvc.perform(get("/points/deduction-details")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("points/history/deduction",
                        responseFields(
                                fieldWithPath("[].time").type(JsonFieldType.STRING).description("생성 날짜"),
                                fieldWithPath("[].amount").type(JsonFieldType.NUMBER).description("충전 금액 "),
                                fieldWithPath("[].division").type(JsonFieldType.STRING).description("종류")
                        )));

    }
}