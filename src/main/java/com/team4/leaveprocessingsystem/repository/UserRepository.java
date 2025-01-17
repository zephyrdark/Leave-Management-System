package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u ORDER BY u.employee.id ASC")
    List<User> findAll();

    @Query("SELECT u FROM User u WHERE u.employee.id = :employeeId")
    List<User> findByEmployeeId(Integer employeeId);

    @Query("Select u from User u join u.employee userEmployee where userEmployee.id = :k")
    List<User> findUserRolesByEmployeeId(@Param("k") Integer employeeId);

    User findById(int id);

    @Query("Select u from User u " +
        "where CAST(u.role as String) like CONCAT('%', :k, '%')")
    List<User> findUsersByRoleType(@Param("k") String keyword);

    @Query("select u from User u where u.username like CONCAT('%', :k, '%')")
    List<User> findUsersByUsername(@Param("k") String keyword);

    @Query("Select u from User u where u.email like CONCAT('%', :k, '%')")
    List<User> findUsersByEmail(@Param("k") String keyword);

    @Query("Select u from User u where u.id = :id")
    List<User> findUsersById();
}
