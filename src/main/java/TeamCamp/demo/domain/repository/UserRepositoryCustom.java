package TeamCamp.demo.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import TeamCamp.demo.dto.UserDto;


public interface UserRepositoryCustom  {

    Page<UserDto.UserListResponse> searchByUsers(UserDto.UserSearchCondition searchRequest, Pageable pageable);
}
