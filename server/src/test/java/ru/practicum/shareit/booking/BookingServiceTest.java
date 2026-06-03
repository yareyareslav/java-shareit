package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemConstantsTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.shared.error.BadRequestException;
import ru.practicum.shareit.shared.error.ForbiddenException;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserConstantsTest;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  private static final User OTHER_USER = new User(3L, "Other User", "other.user@gmail.com");

  @Mock
  BookingRepository bookingRepository;

  @Mock
  ItemRepository itemRepository;

  @Mock
  UserRepository userRepository;

  @InjectMocks
  BookingServiceImpl bookingService;

  @Test
  @DisplayName("getBookings returns all booker bookings for ALL state")
  void getBookings_allState_returnBookings() {
    Item item = defaultItem();
    Booking booking = waitingBooking(item);
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(bookingRepository.findAllByBookerId(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(List.of(booking));

    List<ResponseBookingDto> result = bookingService.getBookings(
        BookingConstantsTest.BOOKER.getId(), BookingState.ALL);

    assertEquals(1, result.size());
    assertEquals(booking.getId(), result.getFirst().getId());
    verify(bookingRepository).findAllByBookerId(BookingConstantsTest.BOOKER.getId());
  }

  @Test
  @DisplayName("getBookings uses past query for PAST state")
  void getBookings_pastState_callPastRepositoryMethod() {
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(bookingRepository.findAllByBookerIdAndInPast(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(List.of());

    bookingService.getBookings(BookingConstantsTest.BOOKER.getId(), BookingState.PAST);

    verify(bookingRepository).findAllByBookerIdAndInPast(BookingConstantsTest.BOOKER.getId());
    verify(bookingRepository, never()).findAllByBookerId(any());
  }

  @Test
  @DisplayName("getBookings uses future query for FUTURE state")
  void getBookings_futureState_callFutureRepositoryMethod() {
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(bookingRepository.findAllByBookerIdAndInFuture(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(List.of());

    bookingService.getBookings(BookingConstantsTest.BOOKER.getId(), BookingState.FUTURE);

    verify(bookingRepository).findAllByBookerIdAndInFuture(BookingConstantsTest.BOOKER.getId());
  }

  @Test
  @DisplayName("getBookings uses current query for CURRENT state")
  void getBookings_currentState_callCurrentRepositoryMethod() {
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(bookingRepository.findAllByBookerIdAndInCurrentPeriod(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(List.of());

    bookingService.getBookings(BookingConstantsTest.BOOKER.getId(), BookingState.CURRENT);

    verify(bookingRepository).findAllByBookerIdAndInCurrentPeriod(BookingConstantsTest.BOOKER.getId());
  }

  @Test
  @DisplayName("getBookings uses status query for WAITING state")
  void getBookings_waitingState_callStatusRepositoryMethod() {
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(bookingRepository.findAllByBookerIdAndStatus(
        BookingConstantsTest.BOOKER.getId(), BookingStatus.WAITING))
        .thenReturn(List.of());

    bookingService.getBookings(BookingConstantsTest.BOOKER.getId(), BookingState.WAITING);

    verify(bookingRepository).findAllByBookerIdAndStatus(
        BookingConstantsTest.BOOKER.getId(), BookingStatus.WAITING);
  }

  @Test
  @DisplayName("getBookings throws when user not found")
  void getBookings_nonExistingUser_throwNotFoundException() {
    when(userRepository.findById(UserConstantsTest.NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> bookingService.getBookings(UserConstantsTest.NON_EXISTING_USER_ID, BookingState.ALL));
    verify(bookingRepository, never()).findAllByBookerId(any());
  }

  @Test
  @DisplayName("getBookingsOfItemsOwnedByUser returns owner item bookings for ALL state")
  void getBookingsOfItemsOwnedByUser_allState_returnBookings() {
    Item item = defaultItem();
    Booking booking = waitingBooking(item);
    when(userRepository.findById(ItemConstantsTest.OWNER.getId()))
        .thenReturn(Optional.of(ItemConstantsTest.OWNER));
    when(bookingRepository.findAllByItemOwnerId(ItemConstantsTest.OWNER.getId()))
        .thenReturn(List.of(booking));

    List<ResponseBookingDto> result = bookingService.getBookingsOfItemsOwnedByUser(
        ItemConstantsTest.OWNER.getId(), BookingState.ALL);

    assertEquals(1, result.size());
    verify(bookingRepository).findAllByItemOwnerId(ItemConstantsTest.OWNER.getId());
  }

  @Test
  @DisplayName("getBookingsOfItemsOwnedByUser uses past query for PAST state")
  void getBookingsOfItemsOwnedByUser_pastState_callPastRepositoryMethod() {
    when(userRepository.findById(ItemConstantsTest.OWNER.getId()))
        .thenReturn(Optional.of(ItemConstantsTest.OWNER));
    when(bookingRepository.findAllByItemOwnerIdAndInPast(ItemConstantsTest.OWNER.getId()))
        .thenReturn(List.of());

    bookingService.getBookingsOfItemsOwnedByUser(ItemConstantsTest.OWNER.getId(), BookingState.PAST);

    verify(bookingRepository).findAllByItemOwnerIdAndInPast(ItemConstantsTest.OWNER.getId());
  }

  @Test
  @DisplayName("getBookingsOfItemsOwnedByUser throws when user not found")
  void getBookingsOfItemsOwnedByUser_nonExistingUser_throwNotFoundException() {
    when(userRepository.findById(UserConstantsTest.NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> bookingService.getBookingsOfItemsOwnedByUser(
            UserConstantsTest.NON_EXISTING_USER_ID, BookingState.ALL));
  }

  @Test
  @DisplayName("getBookingById returns booking for booker")
  void getBookingById_booker_returnBooking() {
    Item item = defaultItem();
    Booking booking = waitingBooking(item);
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

    ResponseBookingDto result = bookingService.getBookingById(
        booking.getId(), BookingConstantsTest.BOOKER.getId());

    assertEquals(booking.getId(), result.getId());
    assertEquals(BookingStatus.WAITING, result.getStatus());
  }

  @Test
  @DisplayName("getBookingById returns booking for item owner")
  void getBookingById_itemOwner_returnBooking() {
    Item item = defaultItem();
    Booking booking = waitingBooking(item);
    when(userRepository.findById(ItemConstantsTest.OWNER.getId()))
        .thenReturn(Optional.of(ItemConstantsTest.OWNER));
    when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

    ResponseBookingDto result = bookingService.getBookingById(
        booking.getId(), ItemConstantsTest.OWNER.getId());

    assertEquals(booking.getId(), result.getId());
  }

  @Test
  @DisplayName("getBookingById throws for unrelated user")
  void getBookingById_otherUser_throwForbiddenException() {
    Item item = defaultItem();
    Booking booking = waitingBooking(item);
    when(userRepository.findById(OTHER_USER.getId())).thenReturn(Optional.of(OTHER_USER));
    when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

    assertThrows(ForbiddenException.class,
        () -> bookingService.getBookingById(booking.getId(), OTHER_USER.getId()));
  }

  @Test
  @DisplayName("getBookingById throws when booking not found")
  void getBookingById_nonExistingBooking_throwNotFoundException() {
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(bookingRepository.findById(BookingConstantsTest.NON_EXISTING_BOOKING_ID))
        .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> bookingService.getBookingById(
            BookingConstantsTest.NON_EXISTING_BOOKING_ID, BookingConstantsTest.BOOKER.getId()));
  }

  @Test
  @DisplayName("create saves booking with WAITING status")
  void create_validRequest_returnSavedBooking() {
    Item item = defaultItem();
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(itemRepository.findById(BookingConstantsTest.DEFAULT_ITEM_ID)).thenReturn(Optional.of(item));
    when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
      Booking saved = invocation.getArgument(0);
      return BookingConstantsTest.createBooking(
          BookingConstantsTest.LAST_BOOKING_ID,
          saved.getStart(),
          saved.getEnd(),
          saved.getItem(),
          saved.getBooker(),
          saved.getStatus()
      );
    });

    ResponseBookingDto result = bookingService.create(
        BookingConstantsTest.BOOKER.getId(), BookingConstantsTest.VALID_BOOKING_DTO);

    assertEquals(BookingConstantsTest.LAST_BOOKING_ID, result.getId());
    assertEquals(BookingStatus.WAITING, result.getStatus());
    verify(bookingRepository).save(any(Booking.class));
  }

  @Test
  @DisplayName("create throws when item is unavailable")
  void create_unavailableItem_throwBadRequestException() {
    Item item = ItemConstantsTest.createItem(
        BookingConstantsTest.DEFAULT_ITEM_ID, "Дрель", "Описание", false, ItemConstantsTest.OWNER);
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(itemRepository.findById(BookingConstantsTest.DEFAULT_ITEM_ID)).thenReturn(Optional.of(item));

    assertThrows(BadRequestException.class,
        () -> bookingService.create(BookingConstantsTest.BOOKER.getId(), BookingConstantsTest.VALID_BOOKING_DTO));
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @DisplayName("create throws when booker not found")
  void create_nonExistingUser_throwNotFoundException() {
    when(userRepository.findById(UserConstantsTest.NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> bookingService.create(UserConstantsTest.NON_EXISTING_USER_ID, BookingConstantsTest.VALID_BOOKING_DTO));
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @DisplayName("create throws when item not found")
  void create_nonExistingItem_throwNotFoundException() {
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(itemRepository.findById(BookingConstantsTest.DEFAULT_ITEM_ID)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> bookingService.create(BookingConstantsTest.BOOKER.getId(), BookingConstantsTest.VALID_BOOKING_DTO));
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @DisplayName("updateStatus approves booking")
  void updateStatus_approved_true_setApprovedStatus() {
    Item item = defaultItem();
    Booking booking = waitingBooking(item);
    when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
    when(userRepository.findById(ItemConstantsTest.OWNER.getId()))
        .thenReturn(Optional.of(ItemConstantsTest.OWNER));
    when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ResponseBookingDto result = bookingService.updateStatus(
        ItemConstantsTest.OWNER.getId(), booking.getId(), true);

    assertEquals(BookingStatus.APPROVED, result.getStatus());
    verify(bookingRepository).save(booking);
  }

  @Test
  @DisplayName("updateStatus rejects booking")
  void updateStatus_approved_false_setRejectedStatus() {
    Item item = defaultItem();
    Booking booking = waitingBooking(item);
    when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
    when(userRepository.findById(ItemConstantsTest.OWNER.getId()))
        .thenReturn(Optional.of(ItemConstantsTest.OWNER));
    when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ResponseBookingDto result = bookingService.updateStatus(
        ItemConstantsTest.OWNER.getId(), booking.getId(), false);

    assertEquals(BookingStatus.REJECTED, result.getStatus());
  }

  @Test
  @DisplayName("updateStatus throws when booking not found")
  void updateStatus_nonExistingBooking_throwNotFoundException() {
    when(bookingRepository.findById(BookingConstantsTest.NON_EXISTING_BOOKING_ID))
        .thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> bookingService.updateStatus(
            ItemConstantsTest.OWNER.getId(), BookingConstantsTest.NON_EXISTING_BOOKING_ID, true));
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @DisplayName("getBookings uses rejected query for REJECTED state")
  void getBookings_rejectedState_callRejectedRepositoryMethod() {
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(bookingRepository.findAllByBookerIdAndStatus(
        BookingConstantsTest.BOOKER.getId(), BookingStatus.REJECTED))
        .thenReturn(List.of());

    bookingService.getBookings(BookingConstantsTest.BOOKER.getId(), BookingState.REJECTED);

    verify(bookingRepository).findAllByBookerIdAndStatus(
        BookingConstantsTest.BOOKER.getId(), BookingStatus.REJECTED);
  }

  @Test
  @DisplayName("getBookingsOfItemsOwnedByUser uses current query for CURRENT state")
  void getBookingsOfItemsOwnedByUser_currentState_callCurrentRepositoryMethod() {
    when(userRepository.findById(ItemConstantsTest.OWNER.getId()))
        .thenReturn(Optional.of(ItemConstantsTest.OWNER));
    when(bookingRepository.findAllByItemOwnerIdAndInCurrentPeriod(ItemConstantsTest.OWNER.getId()))
        .thenReturn(List.of());

    bookingService.getBookingsOfItemsOwnedByUser(ItemConstantsTest.OWNER.getId(), BookingState.CURRENT);

    verify(bookingRepository).findAllByItemOwnerIdAndInCurrentPeriod(ItemConstantsTest.OWNER.getId());
  }

  @Test
  @DisplayName("getBookingsOfItemsOwnedByUser uses future query for FUTURE state")
  void getBookingsOfItemsOwnedByUser_futureState_callFutureRepositoryMethod() {
    when(userRepository.findById(ItemConstantsTest.OWNER.getId()))
        .thenReturn(Optional.of(ItemConstantsTest.OWNER));
    when(bookingRepository.findAllByItemOwnerIdAndInFuture(ItemConstantsTest.OWNER.getId()))
        .thenReturn(List.of());

    bookingService.getBookingsOfItemsOwnedByUser(ItemConstantsTest.OWNER.getId(), BookingState.FUTURE);

    verify(bookingRepository).findAllByItemOwnerIdAndInFuture(ItemConstantsTest.OWNER.getId());
  }

  @Test
  @DisplayName("getBookingsOfItemsOwnedByUser uses waiting query for WAITING state")
  void getBookingsOfItemsOwnedByUser_waitingState_callWaitingRepositoryMethod() {
    when(userRepository.findById(ItemConstantsTest.OWNER.getId()))
        .thenReturn(Optional.of(ItemConstantsTest.OWNER));
    when(bookingRepository.findAllByItemOwnerIdAndStatus(ItemConstantsTest.OWNER.getId(), BookingStatus.WAITING))
        .thenReturn(List.of());

    bookingService.getBookingsOfItemsOwnedByUser(ItemConstantsTest.OWNER.getId(), BookingState.WAITING);

    verify(bookingRepository).findAllByItemOwnerIdAndStatus(
        ItemConstantsTest.OWNER.getId(), BookingStatus.WAITING);
  }

  @Test
  @DisplayName("getBookingsOfItemsOwnedByUser uses rejected query for REJECTED state")
  void getBookingsOfItemsOwnedByUser_rejectedState_callRejectedRepositoryMethod() {
    when(userRepository.findById(ItemConstantsTest.OWNER.getId()))
        .thenReturn(Optional.of(ItemConstantsTest.OWNER));
    when(bookingRepository.findAllByItemOwnerIdAndStatus(ItemConstantsTest.OWNER.getId(), BookingStatus.REJECTED))
        .thenReturn(List.of());

    bookingService.getBookingsOfItemsOwnedByUser(ItemConstantsTest.OWNER.getId(), BookingState.REJECTED);

    verify(bookingRepository).findAllByItemOwnerIdAndStatus(
        ItemConstantsTest.OWNER.getId(), BookingStatus.REJECTED);
  }

  @Test
  @DisplayName("create throws when booker is item owner")
  void create_bookerIsOwner_throwBadRequestException() {
    Item item = ItemConstantsTest.createItem(
        BookingConstantsTest.DEFAULT_ITEM_ID, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
    when(userRepository.findById(ItemConstantsTest.OWNER.getId()))
        .thenReturn(Optional.of(ItemConstantsTest.OWNER));
    when(itemRepository.findById(BookingConstantsTest.DEFAULT_ITEM_ID)).thenReturn(Optional.of(item));

    assertThrows(BadRequestException.class,
        () -> bookingService.create(ItemConstantsTest.OWNER.getId(), BookingConstantsTest.VALID_BOOKING_DTO));
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @DisplayName("create throws when end is not after start")
  void create_invalidDates_throwBadRequestException() {
    Item item = defaultItem();
    BookingDto invalidDto = new BookingDto(
        null,
        BookingConstantsTest.FUTURE_END,
        BookingConstantsTest.FUTURE_START,
        BookingConstantsTest.DEFAULT_ITEM_ID,
        null,
        null
    );
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
    when(itemRepository.findById(BookingConstantsTest.DEFAULT_ITEM_ID)).thenReturn(Optional.of(item));

    assertThrows(BadRequestException.class,
        () -> bookingService.create(BookingConstantsTest.BOOKER.getId(), invalidDto));
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @DisplayName("updateStatus throws when booking is not waiting")
  void updateStatus_notWaiting_throwForbiddenException() {
    Item item = defaultItem();
    Booking booking = BookingConstantsTest.createLastApprovedBooking(item);
    when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
    when(userRepository.findById(ItemConstantsTest.OWNER.getId()))
        .thenReturn(Optional.of(ItemConstantsTest.OWNER));

    assertThrows(ForbiddenException.class,
        () -> bookingService.updateStatus(ItemConstantsTest.OWNER.getId(), booking.getId(), true));
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @DisplayName("updateStatus throws when user is not item owner")
  void updateStatus_notOwner_throwForbiddenException() {
    Item item = defaultItem();
    Booking booking = waitingBooking(item);
    when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
    when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
        .thenReturn(Optional.of(BookingConstantsTest.BOOKER));

    assertThrows(ForbiddenException.class,
        () -> bookingService.updateStatus(BookingConstantsTest.BOOKER.getId(), booking.getId(), true));
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @DisplayName("updateStatus throws when user not found")
  void updateStatus_nonExistingUser_throwForbiddenException() {
    Item item = defaultItem();
    Booking booking = waitingBooking(item);
    when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
    when(userRepository.findById(UserConstantsTest.NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

    assertThrows(ForbiddenException.class,
        () -> bookingService.updateStatus(
            UserConstantsTest.NON_EXISTING_USER_ID, booking.getId(), true));
    verify(bookingRepository, never()).save(any());
  }

  private Item defaultItem() {
    return ItemConstantsTest.createItem(
        BookingConstantsTest.DEFAULT_ITEM_ID,
        "Дрель",
        "Описание",
        true,
        ItemConstantsTest.OWNER
    );
  }

  private Booking waitingBooking(Item item) {
    return BookingConstantsTest.createBooking(
        BookingConstantsTest.LAST_BOOKING_ID,
        BookingConstantsTest.FUTURE_START,
        BookingConstantsTest.FUTURE_END,
        item,
        BookingConstantsTest.BOOKER,
        BookingStatus.WAITING
    );
  }
}
