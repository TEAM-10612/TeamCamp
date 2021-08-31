package TeamCamp.demo.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import TeamCamp.demo.domain.repository.UserRepository;
import TeamCamp.demo.dto.UserDto;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;

    public Page<UserDto.UserListResponse> findUsers(UserDto.UserSearchCondition request, Pageable pageable){
        return  userRepository.searchByUsers(request,pageable);
    }
}
