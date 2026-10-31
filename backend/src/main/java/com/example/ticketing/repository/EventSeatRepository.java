package com.example.ticketing.repository;

import com.example.ticketing.entity.EventSeat;
import com.example.ticketing.enums.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select es from EventSeat es where es.event.id = :eventId and es.id in :ids")
    List<EventSeat> findAllForUpdate(@Param("eventId") Long eventId, @Param("ids") List<Long> ids);

    List<EventSeat> findByEventId(Long eventId);
    List<EventSeat> findByEventIdAndStatus(Long eventId, SeatStatus status);
}
