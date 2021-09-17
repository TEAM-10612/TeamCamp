package TeamCamp.demo.service;

import TeamCamp.demo.domain.model.users.UserStatus;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.dto.UserDto.UserDetailResponse;
import TeamCamp.demo.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import TeamCamp.demo.domain.repository.UserRepository;
import TeamCamp.demo.dto.UserDto;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;

    public Page<UserDto.UserListResponse> findUsers(UserDto.UserSearchCondition request, Pageable pageable){
        return  userRepository.searchByUsers(request,pageable);
    }

    public UserDetailResponse getUser(Long  id){
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException("존재하지 않는 회원입니다."));
        return user.toUserDetailsDto();
    }

    @Transactional
    public void updateBanUser(UserDto.UserBanRequest request){
        Long id = request.getId();
        UserStatus userStatus =  request.getUserStatus();
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));
        user.updatedUserStatus(userStatus);
    }
}
