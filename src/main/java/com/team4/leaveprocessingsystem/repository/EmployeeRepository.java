package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByName(String name);

    List<Employee> findByManager(Manager manager);

    @Query("Select emp from Employee as emp where emp.name like CONCAT('%', :k, '%') ")
    List<Employee> findEmployeesByName(@Param("k") String keyword);

    @Query("Select e from Employee e join e.jobDesignation jd where jd.name like CONCAT('%', :k, '%')")
    List<Employee> findEmployeesByJobDesignation(@Param("k") String keyword);

    @Query("Select e from Employee e where e.manager.name like CONCAT('%', :k, '%')")
    List<Employee> findEmployeesByManager(@Param("k") String keyword);

    @Query("Select emp from Employee emp where emp.isDeleted = false")
    List<Employee> findAllExcludeDeleted();

    @Query("Select emp from Employee emp where emp.isDeleted = true")
    List<Employee> findOnlyDeleted();

}
