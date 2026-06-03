package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemConstantsTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    private Item item;

    @BeforeEach
    void setUp() {
        userRepository.save(ItemConstantsTest.OWNER);
        userRepository.save(BookingConstantsTest.BOOKER);
        item = itemRepository.save(ItemConstantsTest.createItem(
                null, "Дрель", "Описание", true, ItemConstantsTest.OWNER
        ));
    }

    @Test
    @DisplayName("findPastApprovedByItemOwnerId returns latest past booking for owner items")
    void findPastApprovedByItemOwnerId_ownerHasPastBooking_returnBooking() {
        Booking past = persistBooking(
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                BookingStatus.APPROVED
        );
        persistBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED
        );

        Booking result = bookingRepository.findPastApprovedByItemOwnerId(ItemConstantsTest.OWNER.getId());

        assertNotNull(result);
        assertEquals(past.getId(), result.getId());
    }

    @Test
    @DisplayName("findPastApprovedByItemOwnerId returns null when owner has no past bookings")
    void findPastApprovedByItemOwnerId_noPastBookings_returnNull() {
        persistBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED
        );

        Booking result = bookingRepository.findPastApprovedByItemOwnerId(ItemConstantsTest.OWNER.getId());

        assertNull(result);
    }

    @Test
    @DisplayName("findFutureApprovedByItemOwnerId returns nearest future booking for owner items")
    void findFutureApprovedByItemOwnerId_ownerHasFutureBooking_returnBooking() {
        persistBooking(
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                BookingStatus.APPROVED
        );
        Booking future = persistBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED
        );

        Booking result = bookingRepository.findFutureApprovedByItemOwnerId(ItemConstantsTest.OWNER.getId());

        assertNotNull(result);
        assertEquals(future.getId(), result.getId());
    }

    @Test
    @DisplayName("findFutureApprovedByItemOwnerId returns null when owner has no future bookings")
    void findFutureApprovedByItemOwnerId_noFutureBookings_returnNull() {
        persistBooking(
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3),
                BookingStatus.APPROVED
        );

        Booking result = bookingRepository.findFutureApprovedByItemOwnerId(ItemConstantsTest.OWNER.getId());

        assertNull(result);
    }

    @Test
    @DisplayName("findAllByItemOwnerIdAndInPast returns only finished bookings")
    void findAllByItemOwnerIdAndInPast_mixedBookings_returnPastOnly() {
        Booking past = persistBooking(
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2),
                BookingStatus.APPROVED
        );
        persistBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING
        );
        persistBooking(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED
        );

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndInPast(ItemConstantsTest.OWNER.getId());

        assertEquals(1, result.size());
        assertEquals(past.getId(), result.getFirst().getId());
    }

    @Test
    @DisplayName("findAllByItemOwnerIdAndInFuture returns only upcoming bookings")
    void findAllByItemOwnerIdAndInFuture_mixedBookings_returnFutureOnly() {
        persistBooking(
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2),
                BookingStatus.APPROVED
        );
        Booking future = persistBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING
        );
        persistBooking(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED
        );

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndInFuture(ItemConstantsTest.OWNER.getId());

        assertEquals(1, result.size());
        assertEquals(future.getId(), result.getFirst().getId());
    }

    @Test
    @DisplayName("findAllByItemOwnerIdAndInCurrentPeriod returns only active bookings")
    void findAllByItemOwnerIdAndInCurrentPeriod_mixedBookings_returnCurrentOnly() {
        persistBooking(
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2),
                BookingStatus.APPROVED
        );
        persistBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING
        );
        Booking current = persistBooking(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED
        );

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndInCurrentPeriod(
                ItemConstantsTest.OWNER.getId()
        );

        assertEquals(1, result.size());
        assertEquals(current.getId(), result.getFirst().getId());
    }

    @Test
    @DisplayName("findAllByBookerIdAndInPast returns only finished bookings of booker")
    void findAllByBookerIdAndInPast_mixedBookings_returnPastOnly() {
        Booking past = persistBooking(
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2),
                BookingStatus.APPROVED
        );
        persistBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED
        );

        List<Booking> result = bookingRepository.findAllByBookerIdAndInPast(BookingConstantsTest.BOOKER.getId());

        assertEquals(1, result.size());
        assertEquals(past.getId(), result.getFirst().getId());
    }

    @Test
    @DisplayName("findAllByBookerIdAndInFuture returns only upcoming bookings of booker")
    void findAllByBookerIdAndInFuture_mixedBookings_returnFutureOnly() {
        persistBooking(
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2),
                BookingStatus.APPROVED
        );
        Booking future = persistBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED
        );

        List<Booking> result = bookingRepository.findAllByBookerIdAndInFuture(BookingConstantsTest.BOOKER.getId());

        assertEquals(1, result.size());
        assertEquals(future.getId(), result.getFirst().getId());
    }

    @Test
    @DisplayName("findAllByBookerIdAndInCurrentPeriod returns only active bookings of booker")
    void findAllByBookerIdAndInCurrentPeriod_mixedBookings_returnCurrentOnly() {
        persistBooking(
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2),
                BookingStatus.APPROVED
        );
        persistBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED
        );
        Booking current = persistBooking(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                BookingStatus.APPROVED
        );

        List<Booking> result = bookingRepository.findAllByBookerIdAndInCurrentPeriod(
                BookingConstantsTest.BOOKER.getId()
        );

        assertEquals(1, result.size());
        assertEquals(current.getId(), result.getFirst().getId());
    }

    @Test
    @DisplayName("Owner-scoped queries do not return bookings of another owner's items")
    void findAllByItemOwnerIdAndInPast_otherOwnerItems_notIncluded() {
        Item otherItem = itemRepository.save(ItemConstantsTest.createItem(
                null, "Книга", "Описание", true, BookingConstantsTest.BOOKER
        ));
        bookingRepository.save(BookingConstantsTest.createBooking(
                null,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2),
                otherItem,
                ItemConstantsTest.OWNER,
                BookingStatus.APPROVED
        ));
        persistBooking(
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED
        );

        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndInPast(ItemConstantsTest.OWNER.getId());

        assertEquals(1, result.size());
        assertEquals(item.getId(), result.getFirst().getItem().getId());
    }

    @Test
    @DisplayName("Booker-scoped queries do not return bookings of another user")
    void findAllByBookerIdAndInPast_otherBooker_notIncluded() {
        bookingRepository.save(BookingConstantsTest.createBooking(
                null,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().minusDays(2),
                item,
                ItemConstantsTest.OWNER,
                BookingStatus.APPROVED
        ));
        Booking bookerPast = persistBooking(
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED
        );

        List<Booking> result = bookingRepository.findAllByBookerIdAndInPast(BookingConstantsTest.BOOKER.getId());

        assertEquals(1, result.size());
        assertEquals(bookerPast.getId(), result.getFirst().getId());
    }

    @Test
    @DisplayName("findAllByItemOwnerIdAndInPast returns empty list when owner has no bookings")
    void findAllByItemOwnerIdAndInPast_noBookings_returnEmpty() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndInPast(ItemConstantsTest.OWNER.getId());

        assertTrue(result.isEmpty());
    }

    private Booking persistBooking(LocalDateTime start, LocalDateTime end, BookingStatus status) {
        return bookingRepository.save(BookingConstantsTest.createBooking(
                null, start, end, item, BookingConstantsTest.BOOKER, status
        ));
    }
}
