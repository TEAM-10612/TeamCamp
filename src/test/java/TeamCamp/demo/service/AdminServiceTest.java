package TeamCamp.demo.service;

import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.domain.model.users.UserStatus;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.repository.UserRepository;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.dto.UserDto.UserListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    AdminService adminService;

    private List<UserListResponse>setUser(){
        List<UserListResponse> list = new ArrayList<>();
        for(int i = 0; i < 3; i++ ){
            UserListResponse userListResponse = UserListResponse.builder()
                    .id((long) i)
                    .email("rdj1014@naver.com" + i)
                    .userLevel(UserLevel.AUTH)
                    .build();
            list.add(userListResponse);
        }
        return list;
    }
    @Test
    @DisplayName("전체 회원 조회")
    void findAll(){
        List<UserListResponse>list = setUser();
        long total = list.size();
        Pageable pageable = PageRequest.of(0,10);
        Page<UserListResponse> result = new PageImpl<>(list,pageable,total);
        BDDMockito.given(userRepository.searchByUsers(any(),any())).willReturn(result);

        adminService.findUsers(any(),any());

        assertThat(result.getContent().size()).isEqualTo(list.size());
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        verify(userRepository, atLeastOnce()).searchByUsers(any(),any());
    }

    @Test
    @DisplayName("요청한 ID에 해당하는 사용자를 BAN 처리 해야한다.")
    void updateBanUsers()throws Exception{
        //given
        User user = User.builder()
                .id(1L)
                .email("rdj1014@naver.com")
                .userStatus(UserStatus.NORMAL)
                .build();

        UserDto.UserBanRequest userBanRequest = UserDto.UserBanRequest.builder()
                .id(1L)
                .userStatus(UserStatus.BAN)
                .build();
        //when
        BDDMockito.given(userRepository.findById(any())).willReturn(Optional.of(user));
        adminService.updateBanUser(userBanRequest);

        //then
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.BAN);
        verify(userRepository,atLeastOnce()).findById(any());

    }

    @DisplayName("요청한 ID에 해당하는 사용자의 BAN을 해제 한다.")
    @Test
    void updateBanUsers_normal()throws Exception{
        //given
        User user = User.builder()
                .id(1L)
                .email("rdj1014@naver.com")
                .userStatus(UserStatus.BAN)
                .build();

        UserDto.UserBanRequest userBanRequest = UserDto.UserBanRequest.builder()
                .id(1L)
                .userStatus(UserStatus.NORMAL)
                .build();
        //when
        BDDMockito.given(userRepository.findById(any())).willReturn(Optional.of(user));
        adminService.updateBanUser(userBanRequest);
        //then

        assertThat(user.getUserStatus()).isEqualTo(UserStatus.NORMAL);
        verify(userRepository,atLeastOnce()).findById(any());

    }

    @Test
    @DisplayName("회원 상세 정보 조회 ")
    void getUser()throws Exception{
        //given
        User user = User.builder()
                .id(1L)
                .email("rdj1014@naver.com")
                .nickname("17171771")
                .phone("01012345678")
                .userLevel(UserLevel.UNAUTH)
                .userStatus(UserStatus.NORMAL)
                .build();
        //when
        Long id = 1L;
        BDDMockito.given(userRepository.findById(id)).willReturn(Optional.of(user));
        UserDto.UserDetailResponse response = adminService.getUser(id);
        //then

        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getNickname()).isEqualTo(user.getNickname());
        assertThat(response.getPhoneNumber()).isEqualTo(user.getPhone());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getUserStatus()).isEqualTo(user.getUserStatus());
        assertThat(response.getUserLevel()).isEqualTo(user.getUserLevel());
    }
}