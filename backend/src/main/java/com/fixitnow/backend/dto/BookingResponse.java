package com.fixitnow.backend.dto;

import java.time.LocalDateTime;

import com.fixitnow.backend.entity.Booking;
import com.fixitnow.backend.entity.ServiceEntity;
import com.fixitnow.backend.entity.User;

public class BookingResponse {

    private Long id;

    private Long customerId;
    private String customerName;

    private Long providerId;
    private String providerName;

    private Long serviceId;
    private String serviceCategory;
    private String serviceSubcategory;

    private String timeSlot;
    private String status;
    private LocalDateTime createdAt;

    public BookingResponse(Booking booking) {
        this.id = booking.getId();

        User customer = booking.getCustomer();
        if (customer != null) {
            this.customerId = customer.getId();
            this.customerName = customer.getName();
        }

        User provider = booking.getProvider();
        if (provider != null) {
            this.providerId = provider.getId();
            this.providerName = provider.getName();
        }

        ServiceEntity service = booking.getService();
        if (service != null) {
            this.serviceId = service.getId();
            this.serviceCategory = service.getCategory();
            this.serviceSubcategory = service.getSubcategory();
        }

        this.timeSlot = booking.getTimeSlot();
        this.status = booking.getStatus();
        this.createdAt = booking.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getProviderId() {
        return providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public String getServiceSubcategory() {
        return serviceSubcategory;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
