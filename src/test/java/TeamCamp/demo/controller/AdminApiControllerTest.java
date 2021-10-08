package TeamCamp.demo.controller;

import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.domain.model.users.UserStatus;
import TeamCamp.demo.domain.model.users.user.Account;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.service.AdminService;
import TeamCamp.demo.service.loginservice.SessionLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AdminApiController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
class AdminApiControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private SessionLoginService sessionLoginService;

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

    private List<UserDto.UserListResponse> setUsers(){
        List<UserDto.UserListResponse>list = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            UserDto.UserListResponse userListResponse = UserDto.UserListResponse.builder()
                    .id((long) i)
                    .email("rdj1014@naver.com"+i)
                    .userLevel(UserLevel.AUTH)
                    .build();
            list.add(userListResponse);
        }
        return list;
    }

    @Test
    @DisplayName("관리자가 회원 전체 조회")
    void getAllUser()throws Exception{
        //given
        List<UserDto.UserListResponse> list = setUsers();
        long total = list.size();
        Pageable pageable = PageRequest.of(0,10);
        Page<UserDto.UserListResponse>result = new PageImpl<>(list,pageable,total);

        //when
        given(adminService.findUsers(any(),any())).willReturn(result);

        //then
        mockMvc.perform(get("/admin/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].id").value(list.get(0).getId()))
                .andExpect(jsonPath("$.content.[0].email").value(list.get(0).getEmail()))
                .andExpect(jsonPath("$.content.[0].userLevel").value("AUTH"))
                .andExpect(jsonPath("$.content.[1].id").value(list.get(1).getId()))
                .andExpect(jsonPath("$.content.[1].email").value(list.get(1).getEmail()))
                .andExpect(jsonPath("$.content.[1].userLevel").value("AUTH"))
                .andDo(print())
                .andDo(document("admin/get/findAll",
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("ID"),
                                fieldWithPath("content.[].email").type(JsonFieldType.STRING)
                                        .description("email"),
                                fieldWithPath("content.[].userLevel").type(JsonFieldType.STRING)
                                        .description("userLevel"),
                                fieldWithPath("pageable.offset").ignored(),
                                fieldWithPath("pageable.pageSize").ignored(),
                                fieldWithPath("pageable.pageNumber").ignored(),
                                fieldWithPath("pageable.paged").ignored(),
                                fieldWithPath("pageable.unpaged").ignored(),
                                fieldWithPath("pageable.sort.sorted").ignored(),
                                fieldWithPath("pageable.sort.unsorted").ignored(),
                                fieldWithPath("pageable.sort.empty").ignored(),
                                fieldWithPath("sort.empty").ignored(),
                                fieldWithPath("sort.sorted").ignored(),
                                fieldWithPath("sort.unsorted").ignored(),
                                fieldWithPath("totalPages").ignored(),
                                fieldWithPath("size").ignored(),
                                fieldWithPath("number").ignored(),
                                fieldWithPath("first").ignored(),
                                fieldWithPath("last").ignored(),
                                fieldWithPath("numberOfElements").ignored(),
                                fieldWithPath("empty").ignored(),
                                fieldWithPath("totalElements").ignored()
                        )
                ));
    }


    @Test
    @DisplayName("관리자가 ID[PK]로 회원을 검색한다.")
    void findById()throws Exception{
        //given
        List<UserDto.UserListResponse>list = setUsers();
        long total = list.size();
        Pageable pageable = PageRequest.of(0,10);
        Page<UserDto.UserListResponse> result = new PageImpl<>(list,pageable,total);

        //when
        given(adminService.findUsers(any(),any())).willReturn(result);

        //then
        mockMvc.perform(get("/admin/users?id=1")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].id").value(list.get(0).getId()))
                .andExpect(jsonPath("$.content.[0].email").value(list.get(0).getEmail()))
                .andExpect(jsonPath("$.content.[0].userLevel").value("AUTH"))
                .andDo(document("admin/get/findById",
                        requestParameters(
                                parameterWithName("id").description("검색회원의 ID")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("ID"),
                                fieldWithPath("content.[].email").type(JsonFieldType.STRING)
                                        .description("email"),
                                fieldWithPath("content.[].userLevel").type(JsonFieldType.STRING)
                                        .description("userLevel"),
                                fieldWithPath("content.[].userLevel").ignored(),
                                fieldWithPath("pageable.offset").ignored(),
                                fieldWithPath("pageable.pageSize").ignored(),
                                fieldWithPath("pageable.pageNumber").ignored(),
                                fieldWithPath("pageable.paged").ignored(),
                                fieldWithPath("pageable.unpaged").ignored(),
                                fieldWithPath("pageable.sort.sorted").ignored(),
                                fieldWithPath("pageable.sort.unsorted").ignored(),
                                fieldWithPath("pageable.sort.empty").ignored(),
                                fieldWithPath("sort.empty").ignored(),
                                fieldWithPath("sort.sorted").ignored(),
                                fieldWithPath("sort.unsorted").ignored(),
                                fieldWithPath("totalPages").ignored(),
                                fieldWithPath("size").ignored(),
                                fieldWithPath("number").ignored(),
                                fieldWithPath("first").ignored(),
                                fieldWithPath("last").ignored(),
                                fieldWithPath("numberOfElements").ignored(),
                                fieldWithPath("empty").ignored(),
                                fieldWithPath("totalElements").ignored()
                        ))
                );


    }
    @Test
    @DisplayName("관리자가 유저레벨로 회원을 검색")
    void findByUserLevel()throws Exception{

        //given
        List<UserDto.UserListResponse> list = setUsers();
        long total = list.size();
        Pageable pageable = PageRequest.of(0,10);
        Page<UserDto.UserListResponse>result = new PageImpl<>(list,pageable,total);

        //when
        given(adminService.findUsers(any(),any())).willReturn(result);
        //then
        mockMvc.perform(get("/admin/users?userLevel=AUTH")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].id").value(list.get(0).getId()))
                .andExpect(jsonPath("$.content.[0].email").value(list.get(0).getEmail()))
                .andExpect(jsonPath("$.content.[0].userLevel").value("AUTH"))
                .andDo(document("admin/get/findByUserLevel",
                        requestParameters(
                                parameterWithName("userLevel").description("검색회원의 userLevel")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("ID"),
                                fieldWithPath("content.[].email").type(JsonFieldType.STRING)
                                        .description("email"),
                                fieldWithPath("content.[].userLevel").type(JsonFieldType.STRING)
                                        .description("userLevel"),
                                fieldWithPath("pageable.offset").ignored(),
                                fieldWithPath("pageable.pageSize").ignored(),
                                fieldWithPath("pageable.pageNumber").ignored(),
                                fieldWithPath("pageable.paged").ignored(),
                                fieldWithPath("pageable.unpaged").ignored(),
                                fieldWithPath("pageable.sort.sorted").ignored(),
                                fieldWithPath("pageable.sort.unsorted").ignored(),
                                fieldWithPath("pageable.sort.empty").ignored(),
                                fieldWithPath("sort.empty").ignored(),
                                fieldWithPath("sort.sorted").ignored(),
                                fieldWithPath("sort.unsorted").ignored(),
                                fieldWithPath("totalPages").ignored(),
                                fieldWithPath("size").ignored(),
                                fieldWithPath("number").ignored(),
                                fieldWithPath("first").ignored(),
                                fieldWithPath("last").ignored(),
                                fieldWithPath("numberOfElements").ignored(),
                                fieldWithPath("empty").ignored(),
                                fieldWithPath("totalElements").ignored()
                        ))
                );

    }

    @Test
    @DisplayName("관리자가 이메일로 회원을 검색한다.")
    void findByEmail()throws Exception{
        //given
        List<UserDto.UserListResponse> list = setUsers();
        long total = list.size();
        Pageable pageable = PageRequest.of(0,10);
        Page<UserDto.UserListResponse>result = new PageImpl<>(list,pageable,total);

        //when
        given(adminService.findUsers(any(),any())).willReturn(result);
        //then
        mockMvc.perform(get("/admin/users?email=rdj1014@naver.com0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].id").value(list.get(0).getId()))
                .andExpect(jsonPath("$.content.[0].email").value(list.get(0).getEmail()))
                .andExpect(jsonPath("$.content.[0].userLevel").value("AUTH"))
                .andExpect(jsonPath("$.content.[1].id").value(list.get(1).getId()))
                .andExpect(jsonPath("$.content.[1].email").value(list.get(1).getEmail()))
                .andExpect(jsonPath("$.content.[1].userLevel").value("AUTH"))
                .andDo(document("admin/get/findByEmail",
                        requestParameters(
                                parameterWithName("email").description("검색회원의 email")
                        ),
                        responseFields(
                                fieldWithPath("content.[].id").type(JsonFieldType.NUMBER).description("ID"),
                                fieldWithPath("content.[].email").type(JsonFieldType.STRING)
                                        .description("email"),
                                fieldWithPath("content.[].userLevel").type(JsonFieldType.STRING)
                                        .description("userLevel"),
                                fieldWithPath("pageable.offset").ignored(),
                                fieldWithPath("pageable.pageSize").ignored(),
                                fieldWithPath("pageable.pageNumber").ignored(),
                                fieldWithPath("pageable.paged").ignored(),
                                fieldWithPath("pageable.unpaged").ignored(),
                                fieldWithPath("pageable.sort.sorted").ignored(),
                                fieldWithPath("pageable.sort.unsorted").ignored(),
                                fieldWithPath("pageable.sort.empty").ignored(),
                                fieldWithPath("sort.empty").ignored(),
                                fieldWithPath("sort.sorted").ignored(),
                                fieldWithPath("sort.unsorted").ignored(),
                                fieldWithPath("totalPages").ignored(),
                                fieldWithPath("size").ignored(),
                                fieldWithPath("number").ignored(),
                                fieldWithPath("first").ignored(),
                                fieldWithPath("last").ignored(),
                                fieldWithPath("numberOfElements").ignored(),
                                fieldWithPath("empty").ignored(),
                                fieldWithPath("totalElements").ignored()
                        ))
                );
    }

    @Test
    @DisplayName("관리자가 유저 상세정보 조회")
    void getUserDetail()throws Exception{
        //given
        UserDto.UserDetailResponse userDetailResponse = UserDto.UserDetailResponse.builder()
                .id(1L)
                .nickname("ryudjdasd")
                .phoneNumber("01012345678")
                .email("rdj1014@naver.com")
                .account(new Account("Sc","7999999999","ryu"))
                .modifiedDate(LocalDateTime.now())
                .createDate(LocalDateTime.now())
                .userLevel(UserLevel.UNAUTH)
                .userStatus(UserStatus.NORMAL)
                .build();
        //when
        given(adminService.getUser(any())).willReturn(userDetailResponse);

        //then
        mockMvc.perform(get("/admin/users/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get("/admin/users/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin/get/details", pathParameters(
                                parameterWithName("id").description("상세 정보를 조회할 회원의 ID[PK]")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("회원 ID[PK]"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 닉네임"),
                                fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("회원 휴대폰번호"),
                                subsectionWithPath("account").type(JsonFieldType.OBJECT)
                                        .description("회원의 계좌정보"),
                                fieldWithPath("createDate").type(JsonFieldType.VARIES).description("회원 가입 일시"),
                                fieldWithPath("modifiedDate").type(JsonFieldType.VARIES)
                                        .description("회원 정보 수정 일시"),
                                fieldWithPath("userLevel").type(JsonFieldType.STRING)
                                        .description("회원 권한[UNAUTH/AUTH/ADMIN"),
                                fieldWithPath("userStatus").type(JsonFieldType.STRING)
                                        .description("회원 상태[BAN/NORMAL]")
                        )

                ));
    }

    @Test
    @DisplayName("관리자가 회원상태를 NORMAL 이나 BAN 으로 변경 ")
    void userBan()throws Exception{
        //given
        UserDto.UserBanRequest request = UserDto.UserBanRequest.builder()
                .id(1L)
                .userStatus(UserStatus.BAN)
                .build();

        //when

        //then
        mockMvc.perform(post("/admin/users/ban")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin/ban",
                        requestFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("상태를 변경할 회원의 아이디")
                                ,fieldWithPath("userStatus").type(JsonFieldType.STRING)
                                        .description("BAN/NORMAL")
                        )));

    }

}