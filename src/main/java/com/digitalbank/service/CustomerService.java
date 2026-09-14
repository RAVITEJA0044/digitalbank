package com.digitalbank.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.digitalbank.entity.Customer;
import com.digitalbank.repository.BankAccountRepository;
import com.digitalbank.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;

    public CustomerService(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository) {
        this.customerRepository = customerRepository;
            this.bankAccountRepository = bankAccountRepository;

    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
public Customer getCustomerById(Long customerId) {

    return customerRepository.findById(customerId)
            .orElseThrow(() ->
                    new RuntimeException("Customer not found"));
}



public Customer updateCustomerStatus(Long id, String status) {

    Customer customer = customerRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Customer not found"));

    if (!status.equalsIgnoreCase("ACTIVE")
            && !status.equalsIgnoreCase("INACTIVE")) {

        throw new RuntimeException(
                "Invalid customer status. Allowed statuses: ACTIVE, INACTIVE");
    }

    customer.setStatus(status.toUpperCase());

    return customerRepository.save(customer);
}


   public void deleteCustomer(Long id) {

    Customer customer = customerRepository.findById(id)
            .orElseThrow(() ->
                    new RuntimeException("Customer not found"));

    long accountCount =
            bankAccountRepository.countByCustomerCustomerId(id);

    if (accountCount > 0) {
        throw new RuntimeException(
                "Cannot delete customer. Customer has "
                + accountCount
                + " bank account(s)");
    }

    customerRepository.delete(customer);
}
}