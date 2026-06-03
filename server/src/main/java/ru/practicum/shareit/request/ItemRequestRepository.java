package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterId(long requesterId);

    @Query("""
            SELECT r
            FROM ItemRequest r
            WHERE r.requester.id != :requester_id
            """)
    List<ItemRequest> findAllRequestsOfOthers(@Param("requester_id") long requesterId);
}
