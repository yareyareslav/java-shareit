package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findAllByRequestId(Long requestId);

    @Query("""
            SELECT i
            FROM Item i
            WHERE i.request.id in :request_ids
            """)
    List<Item> findAllByRequestIds(@Param("request_ids") Iterable<Long> requestIds);

    @Query("""
            SELECT i
            FROM Item i
            WHERE i.available = true
                AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%'))
                    OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))
            """)
    List<Item> findAllByAvailableAndText(@Param("text") String text);

    @Query("""
            SELECT DISTINCT i
            FROM Item i
            LEFT JOIN FETCH i.comments
            WHERE i.owner.id = :owner_id
            """)
    List<Item> findAllWithFetchedComments(@Param("owner_id") Long ownerId);

    @Query("""
            SELECT DISTINCT i
            FROM Item i
            LEFT JOIN FETCH i.comments
            WHERE i.id = :item_id
            """)
    Optional<Item> findByIdWithFetchedComments(@Param("item_id") Long itemId);
}
