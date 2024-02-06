package com.wtricks.userManagement.repositories;

import com.wtricks.userManagement.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhone(String phone);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(CASE WHEN :sortBy = 'firstName' THEN c.firstName " +
            "           WHEN :sortBy = 'lastName' THEN c.lastName " +
            "           WHEN :sortBy = 'email' THEN c.email " +
            "           WHEN :sortBy = 'phone' THEN c.phone " +
            "           WHEN :sortBy = 'street' THEN c.street " +
            "           WHEN :sortBy = 'address' THEN c.address " +
            "           WHEN :sortBy = 'city' THEN c.city " +
            "           ELSE c.state END) " +
            "LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Customer> findAllBySearchTerm(@Param("search") String search, @Param("sortBy") String sortBy, Pageable pageable);
}
