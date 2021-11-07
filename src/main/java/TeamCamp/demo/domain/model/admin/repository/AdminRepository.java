package TeamCamp.demo.domain.model.admin.repository;

import TeamCamp.demo.dto.UserDto.UserListResponse;
import TeamCamp.demo.dto.UserDto.UserSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import TeamCamp.demo.dto.UserDto;
import org.springframework.data.jpa.repository.Query;


public interface AdminRepository{
    Page<UserListResponse> searchByUsers(UserSearchCondition searchRequest, Pageable pageable);
}
