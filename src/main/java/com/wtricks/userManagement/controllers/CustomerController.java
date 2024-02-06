package com.wtricks.userManagement.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wtricks.userManagement.dtos.CustomerRequest;
import com.wtricks.userManagement.dtos.ResponseDto;
import com.wtricks.userManagement.exceptions.CustomerAlreadyExist;
import com.wtricks.userManagement.exceptions.CustomerNotFound;
import com.wtricks.userManagement.services.CustomerService;

import jakarta.validation.Valid;


@RestController
@RequestMapping(path = "/api/v1/customer")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @PostMapping(path = "/create")
    public ResponseEntity<ResponseDto> createCustomer(@Valid @RequestBody CustomerRequest customerDto) {
        ResponseDto response = new ResponseDto();

        try {
            response.setData(service.createCustomer(customerDto).getId());
            response.setStatus(HttpStatus.ACCEPTED);
        } catch (CustomerAlreadyExist e) {
            response.setStatus(HttpStatus.CONFLICT);
            response.setMessage(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/multicreate")
    public ResponseEntity<ResponseDto> createMultipleCustomer(@Valid @RequestBody List<CustomerRequest> customerDtos) {
        ResponseDto response = new ResponseDto();
        try {
            response.setData(service.createMultipleCustomers(customerDtos));
            response.setStatus(HttpStatus.ACCEPTED);
        } catch (Exception e) {  }

        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/update/{customerid}")
    public ResponseEntity<ResponseDto> updateCustomer(@PathVariable Long customerid, @Valid @RequestBody CustomerRequest customerDto) {
        ResponseDto response = new ResponseDto();

        try {
            response.setData(service.updateCustomer(customerid, customerDto).getId());
            response.setStatus(HttpStatus.ACCEPTED);
        } catch (CustomerNotFound e) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setMessage(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/delete/{customerId}")
    public ResponseEntity<ResponseDto> deleteCustomer(@PathVariable Long customerId) {
        ResponseDto response = new ResponseDto();
        service.deleteCustomer(customerId);

        response.setData(customerId);
        response.setStatus(HttpStatus.ACCEPTED);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get/{customerId}")
    public ResponseEntity<ResponseDto> getCustomer(@PathVariable Long customerId) {
        ResponseDto response = new ResponseDto();

        try {
            response.setData(service.getCustomer(customerId));
            response.setStatus(HttpStatus.ACCEPTED);
        } catch (CustomerNotFound e) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setMessage(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/getall")
    public ResponseEntity<ResponseDto> getAllCustomers(
        @RequestParam(defaultValue = "1", value = "limit") int limit,
        @RequestParam(defaultValue = "1", value = "page") int page,
        @RequestParam(defaultValue = "", value = "search") String search,
        @RequestParam(defaultValue = "firstName", value = "sortBy") String sortBy,
        @RequestParam(defaultValue = "asc", value = "order") String order
    ) {
        ResponseDto response = new ResponseDto();
        response.setData(service.getAllCustomers(limit, page, search, sortBy, order));
        response.setStatus(HttpStatus.ACCEPTED);

        return ResponseEntity.ok(response);
    }
}