package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findByItemIdAndBookerId(Long itemId, Long bookerId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.id IN :ids
                AND b.status = 'APPROVED'
            ORDER BY b.start DESC
            """)
    List<Booking> findAllByItemIdsAndStatusApproved(@Param("ids") Iterable<Long> ids);

    // -------------------------------------------------
    // ------------- ITEM OWNER ID ---------------------
    // -------------------------------------------------
    List<Booking> findAllByItemOwnerId(Long ownerId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :ownerId
                AND b.end < CURRENT_TIMESTAMP
            ORDER BY b.item.id, b.end DESC
            """)
    Booking findPastApprovedByItemOwnerId(
            @Param("ownerId") Long ownerId
    );

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :ownerId
                AND b.start > CURRENT_TIMESTAMP
            ORDER BY b.item.id, b.start ASC
            """)
    Booking findFutureApprovedByItemOwnerId(
            @Param("ownerId") Long ownerId
    );

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :owner_id
                AND b.start >= CURRENT_TIMESTAMP
            """)
    List<Booking> findAllByItemOwnerIdAndInFuture(@Param("owner_id") Long ownerId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :owner_id
                AND b.end <= CURRENT_TIMESTAMP
            """)
    List<Booking> findAllByItemOwnerIdAndInPast(@Param("owner_id") Long ownerId);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.owner.id = :owner_id
                AND b.start <= CURRENT_TIMESTAMP
                AND b.end >= CURRENT_TIMESTAMP
            """)
    List<Booking> findAllByItemOwnerIdAndInCurrentPeriod(@Param("owner_id") Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status);

    // -------------------------------------------------
    // --------------- BOOKER ID -----------------------
    // -------------------------------------------------
    List<Booking> findAllByBookerId(Long bookerId);

    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.booker.id = :booker_id
            AND b.start >= CURRENT_TIMESTAMP
        """)
    List<Booking> findAllByBookerIdAndInFuture(
            @Param("booker_id") Long bookerId
    );

    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.booker.id = :booker_id
            AND b.end <= CURRENT_TIMESTAMP
        """)
    List<Booking> findAllByBookerIdAndInPast(
            @Param("booker_id") Long bookerId
    );

    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.booker.id = :booker_id
            AND b.start <= CURRENT_TIMESTAMP
            AND b.end >= CURRENT_TIMESTAMP
        """)
    List<Booking> findAllByBookerIdAndInCurrentPeriod(
            @Param("booker_id") Long bookerId
    );

    List<Booking> findAllByBookerIdAndStatus(
            Long bookerId,
            BookingStatus status
    );
}
