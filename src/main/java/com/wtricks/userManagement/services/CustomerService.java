package com.wtricks.userManagement.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.wtricks.userManagement.repositories.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wtricks.userManagement.dtos.CustomerRequest;
import com.wtricks.userManagement.exceptions.CustomerAlreadyExist;
import com.wtricks.userManagement.exceptions.CustomerNotFound;
import com.wtricks.userManagement.models.Customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repo;

    @SuppressWarnings("null")
    public Customer createCustomer(CustomerRequest customerDto) throws CustomerAlreadyExist {
        // we can check customer entry by 'email' and 'phone', if they are exists or not.
        // this way we can avoid duplicate entries of any customers.
        if (repo.existsByEmail(customerDto.getEmail())) {
            throw new CustomerAlreadyExist("Duplicate entry with 'email' - " + customerDto.getEmail());
        }

        if (repo.existsByPhone(customerDto.getPhone())) {
            throw new CustomerAlreadyExist("Duplicate entry with 'phone' - " + customerDto.getPhone());
        }

        // create a new customer based on data we got from user.
        return repo.save(buildCustomerObject(customerDto));
    }

    @SuppressWarnings("null")
    public Customer updateCustomer(Long customerId, CustomerRequest customerDto) throws CustomerNotFound {
        if (!repo.existsById(customerId)) {
            throw new CustomerNotFound("Customer is not found with 'id' - " + customerId);
        }
    
        // we're not checking for any value here.
        // user may not send some values, but that will be checked by the "validation".
        Customer newCustomerObject = buildCustomerObject(customerDto);
        newCustomerObject.setId(customerId);

        return repo.save(newCustomerObject);
    }

    @SuppressWarnings("null")
    public void deleteCustomer(Long customerId) {
        repo.deleteById(customerId);
    }

    @SuppressWarnings("null")
    public Customer getCustomer(Long customerId) throws CustomerNotFound {
        return repo.findById(customerId).orElseThrow(() ->
                new CustomerNotFound("Customer is not found with 'ID' - " + customerId));
    }

    @SuppressWarnings("null")
    public List<Long> createMultipleCustomers(List<CustomerRequest> customeList) throws CustomerAlreadyExist, CustomerNotFound {
        // we'll use our already created method.
        // first we're requires to check if customer is exists or not.
        // I'll use predefined methods only.
        List<Long> customerIds = new ArrayList<>();

        Customer newCustomerObject;
        Optional<Customer> temp;

        for (CustomerRequest customer : customeList) {
            newCustomerObject = buildCustomerObject(customer);

            // if customer entry exists, we'll update it othwerwise create it.
            if ((temp = repo.findByEmail(customer.getEmail())).isPresent()) {
                newCustomerObject.setId(temp.get().getId());
            }

            if (temp.isEmpty() && (temp = repo.findByPhone(customer.getPhone())).isPresent()) {
                newCustomerObject.setId(temp.get().getId());
            }

            repo.save(newCustomerObject);
            customerIds.add(newCustomerObject.getId());
        }

        return customerIds;
    }

    // Helper method to validate the sorting column
    private boolean isValidSortingColumn(String columnName) {
        String[] validColumns = new String[]{"firstName", "lastName", "street", "city", "address", "phone", "email", "state"};
        for(int i = 0; i < 8; i++) {
            if (validColumns[i].equals(columnName)) {
                return true;
            }
        }

        return false;
    }

    private Customer buildCustomerObject(CustomerRequest customerDto) {
        return Customer.builder()
            .firstName(customerDto.getFirstName())
            .lastName(customerDto.getLastName())
            .email(customerDto.getEmail())
            .phone(customerDto.getPhone())
            .address(customerDto.getAddress())
            .city(customerDto.getCity())
            .street(customerDto.getStreet())
            .state(customerDto.getState())
            .build();
    }

    public List<Customer> getAllCustomers(int limit, int page, String search, String sortBy, String order) {
        Sort.Direction sortingDirection = Sort.Direction.ASC;
        if (order.equalsIgnoreCase("desc")) {
            sortingDirection = Sort.Direction.DESC;
        }

        // user may pass wrong column name, which is not in database table.
        // so we need take care of this case.
        if (!isValidSortingColumn(sortBy)) {
            return new ArrayList<>();
        }

        Sort sort = Sort.by(sortingDirection, sortBy);
        Pageable pageable = PageRequest.of(page, limit, sort);

        // get customers based on filter.
        Page<Customer> customerPage = repo.findAllBySearchTerm(
            search,
            sortBy,
            pageable
        );

        return customerPage.getContent();
    }
}