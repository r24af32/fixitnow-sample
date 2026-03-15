package com.fixitnow.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fixitnow.backend.entity.Booking;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByProviderIdAndTimeSlotAndStatusIn(Long providerId,
            String timeSlot,
            List<String> statuses);

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByProviderId(Long providerId);
}