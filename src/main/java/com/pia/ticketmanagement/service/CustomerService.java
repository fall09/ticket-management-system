package com.pia.ticketmanagement.service;

import com.pia.ticketmanagement.dto.request.CreateCustomerRequest;
import com.pia.ticketmanagement.dto.request.UpdateCustomerRequest;
import com.pia.ticketmanagement.dto.request.UpdateCustomerStatusRequest;
import com.pia.ticketmanagement.dto.response.CustomerResponse;
import com.pia.ticketmanagement.dto.response.CustomerStatusHistoryResponse;
import com.pia.ticketmanagement.dto.response.TicketResponse;
import com.pia.ticketmanagement.exception.BadRequestException;
import com.pia.ticketmanagement.exception.ConflictException;
import com.pia.ticketmanagement.exception.NotFoundException;
import com.pia.ticketmanagement.model.*;
import com.pia.ticketmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final CustomerStatusHistoryRepository statusHistoryRepository;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;

    public Page<CustomerResponse> getAllCustomers(
            String search,
            CustomerStatus status,
            Long provinceId,
            Long districtId,
            Boolean hasTicket,
            int page,
            int size
    ) {
        String keyword = (search == null || search.isBlank()) ? null : search.trim();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        return customerRepository
                .filterCustomers(
                        keyword,
                        status == null ? null : status.name(),
                        provinceId,
                        districtId,
                        hasTicket,
                        pageable
                )
                .map(this::mapToResponse);
    }

    public CustomerResponse getCustomerById(Long id) {
        return mapToResponse(findCustomerById(id));
    }

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        validateUniquePhoneAndEmail(request.getPhoneNumber(), request.getEmail(), null);

        Province province = findProvinceById(request.getProvinceId());
        District district = findDistrictById(request.getDistrictId());

        validateDistrictBelongsToProvince(district, province);

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .province(province)
                .district(district)
                .status(request.getStatus() != null ? request.getStatus() : CustomerStatus.ACTIVE)
                .build();

        return mapToResponse(customerRepository.save(customer));
    }

    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = findCustomerById(id);

        validateUniquePhoneAndEmail(request.getPhoneNumber(), request.getEmail(), id);

        Province province = findProvinceById(request.getProvinceId());
        District district = findDistrictById(request.getDistrictId());

        validateDistrictBelongsToProvince(district, province);

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setEmail(request.getEmail());
        customer.setProvince(province);
        customer.setDistrict(district);

        return mapToResponse(customerRepository.save(customer));
    }

    public void deleteCustomer(Long id) {
        Customer customer = findCustomerById(id);
        customerRepository.delete(customer);
    }

    public CustomerResponse updateCustomerStatus(Long id, UpdateCustomerStatusRequest request) {
        Customer customer = findCustomerById(id);

        if (request.getStatus() == null) {
            throw new BadRequestException("Customer status is required.");
        }

        if (request.getStatus() == CustomerStatus.INACTIVE && request.getInactiveReason() == null) {
            throw new BadRequestException("Inactive reason is required.");
        }

        if (request.getStatus() == CustomerStatus.SUSPENDED && request.getSuspendedReason() == null) {
            throw new BadRequestException("Suspended reason is required.");
        }

        if (request.getStatus() == CustomerStatus.ACTIVE) {
            request.setInactiveReason(null);
            request.setSuspendedReason(null);
        }
        CustomerStatus oldStatus = customer.getStatus();

        if (oldStatus == request.getStatus()) {
            throw new BadRequestException("New status is same as current status.");
        }

        customer.setStatus(request.getStatus());

        CustomerStatusHistory history = CustomerStatusHistory.builder()
                .customer(customer)
                .oldStatus(oldStatus)
                .newStatus(request.getStatus())
                .inactiveReason(request.getInactiveReason())
                .suspendedReason(request.getSuspendedReason())
                .note(request.getNote())
                .changedAt(LocalDateTime.now())
                .build();

        statusHistoryRepository.save(history);

        return mapToResponse(customerRepository.save(customer));
    }

    public List<CustomerStatusHistoryResponse> getCustomerStatusHistory(Long customerId) {
        findCustomerById(customerId);

        return statusHistoryRepository.findByCustomerIdOrderByChangedAtDesc(customerId)
                .stream()
                .map(history -> CustomerStatusHistoryResponse.builder()
                        .id(history.getId())
                        .oldStatus(history.getOldStatus())
                        .newStatus(history.getNewStatus())
                        .inactiveReason(history.getInactiveReason())
                        .suspendedReason(history.getSuspendedReason())
                        .note(history.getNote())
                        .changedAt(history.getChangedAt())
                        .build())
                .toList();
    }

    public List<TicketResponse> getCustomerTickets(Long customerId) {
        findCustomerById(customerId);

        return ticketRepository.findByCustomerId(customerId)
                .stream()
                .map(ticketService::mapToResponse)
                .toList();
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .province(customer.getProvince().getName())
                .district(customer.getDistrict().getName())
                .status(customer.getStatus())
                .ticketCount(ticketRepository.countByCustomerId(customer.getId()))
                .build();
    }

    private void validateDistrictBelongsToProvince(District district, Province province) {
        if (!district.getProvince().getId().equals(province.getId())) {
            throw new BadRequestException("Selected district does not belong to selected province.");
        }
    }

    private void validateUniquePhoneAndEmail(String phoneNumber, String email, Long currentCustomerId) {
        customerRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(customer -> {
                    if (currentCustomerId == null || !customer.getId().equals(currentCustomerId)) {
                        throw new ConflictException("Phone number already exists.");
                    }
                });

        customerRepository.findByEmail(email)
                .ifPresent(customer -> {
                    if (currentCustomerId == null || !customer.getId().equals(currentCustomerId)) {
                        throw new ConflictException("Email already exists.");
                    }
                });
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found."));
    }

    private Province findProvinceById(Long id) {
        return provinceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Province not found."));
    }

    private District findDistrictById(Long id) {
        return districtRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("District not found."));
    }
    public Map<String, Long> getCustomerStatusCounts() {
        List<Customer> customers = customerRepository.findAll();

        long totalCustomers = customers.size();

        long activeCustomers = customers.stream()
                .filter(customer -> customer.getStatus() == CustomerStatus.ACTIVE)
                .count();

        long inactiveCustomers = customers.stream()
                .filter(customer -> customer.getStatus() == CustomerStatus.INACTIVE)
                .count();

        long suspendedCustomers = customers.stream()
                .filter(customer -> customer.getStatus() == CustomerStatus.SUSPENDED)
                .count();

        long potentialCustomers = customers.stream()
                .filter(customer -> customer.getStatus() == CustomerStatus.POTENTIAL)
                .count();

        Map<String, Long> result = new LinkedHashMap<>();
        result.put("totalCustomers", totalCustomers);
        result.put("activeCustomers", activeCustomers);
        result.put("inactiveCustomers", inactiveCustomers);
        result.put("suspendedCustomers", suspendedCustomers);
        result.put("potentialCustomers", potentialCustomers);

        return result;
    }

}