package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingConstantsTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestConstantsTest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.shared.error.BadRequestException;
import ru.practicum.shareit.shared.error.ForbiddenException;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.UserConstantsTest;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    @DisplayName("Get items by owner")
    void getItemsByOwner_existingOwner_returnItems() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        item.setComments(List.of(new Comment(1L, null, item, "Отзыв", null)));
        when(userRepository.findById(1L)).thenReturn(Optional.of(ItemConstantsTest.OWNER));
        when(itemRepository.findAllWithFetchedComments(1L)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdsAndStatusApproved(List.of(1L))).thenReturn(null);
        when(bookingRepository.findAllByItemIdsAndStatusApproved(List.of(1L))).thenReturn(null);

        List<ResponseItemDto> items = itemService.getItemsByOwner(1L);

        assertEquals(1, items.size());
        assertEquals("Дрель", items.getFirst().getName());
        assertEquals(1, items.getFirst().getComments().size());
        assertEquals("Отзыв", items.getFirst().getComments().getFirst().getText());
    }

    @Test
    @DisplayName("Get items by non-existing owner")
    void getItemsByOwner_nonExistingOwner_throwNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemsByOwner(99L));
    }

    @Test
    @DisplayName("Get item by id")
    void getItemById_existingItem_returnItemDto() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        item.setComments(List.of(new Comment(1L, null, item, "Комментарий", null)));
        when(itemRepository.findByIdWithFetchedComments(1L)).thenReturn(Optional.of(item));

        ResponseItemDto dto = itemService.getItemById(1L);

        assertEquals("Дрель", dto.getName());
        assertEquals(1, dto.getComments().size());
        assertEquals("Комментарий", dto.getComments().getFirst().getText());
    }

    @Test
    @DisplayName("Get non-existing item by id")
    void getItemById_nonExistingItem_throwNotFoundException() {
        when(itemRepository.findByIdWithFetchedComments(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(99L));
    }

    @Test
    @DisplayName("Create item")
    void createItem_validOwner_returnItemDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(ItemConstantsTest.OWNER));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setId(10L);
            return item;
        });

        ItemDto dto = itemService.createItem(1L, ItemConstantsTest.VALID_ITEM_DTO);

        assertEquals(10L, dto.getId());
        assertEquals("Дрель", dto.getName());
    }

    @Test
    @DisplayName("Update item by owner")
    void updateItem_ownerUpdatesOwnItem_returnUpdatedItemDto() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto dto = itemService.updateItem(1L, 1L, ItemConstantsTest.ITEM_UPDATE_DTO);

        assertEquals("Отвёртка", dto.getName());
        assertEquals(false, dto.getAvailable());
    }

    @Test
    @DisplayName("Update item by non-owner")
    void updateItem_nonOwner_throwForbiddenException() {
        Item item = ItemConstantsTest.createItem(
                1L, "Дрель", "Описание", true, UserConstantsTest.VALID_USER_2
        );
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class,
                () -> itemService.updateItem(1L, 1L, ItemConstantsTest.ITEM_UPDATE_DTO));
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Search items by text")
    void searchItems_matchingText_returnItems() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        when(itemRepository.findAllByAvailableAndText(ItemConstantsTest.SEARCH_TEXT)).thenReturn(List.of(item));

        List<ItemDto> items = itemService.searchItems(ItemConstantsTest.SEARCH_TEXT);

        assertEquals(1, items.size());
    }

    @Test
    @DisplayName("Get items by owner with bookings")
    void getItemsByOwner_withBookings_returnItemsWithLastAndNextBooking() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        Booking lastBooking = BookingConstantsTest.createLastApprovedBooking(item);
        Booking nextBooking = BookingConstantsTest.createNextApprovedBooking(item);
        when(userRepository.findById(1L)).thenReturn(Optional.of(ItemConstantsTest.OWNER));
        when(itemRepository.findAllWithFetchedComments(1L)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdsAndStatusApproved(List.of(1L)))
                .thenReturn(List.of(lastBooking, nextBooking));

        List<ResponseItemDto> items = itemService.getItemsByOwner(1L);

        assertEquals(1, items.size());
        assertEquals(BookingConstantsTest.LAST_BOOKING_ID, items.getFirst().getLastBooking().getId());
        assertEquals(BookingConstantsTest.NEXT_BOOKING_ID, items.getFirst().getNextBooking().getId());
    }

    @Test
    @DisplayName("Create item linked to item request")
    void createItem_withItemRequest_returnItemDto() {
        ItemRequest request = ItemRequestConstantsTest.defaultRequest();
        ItemDto dto = new ItemDto(null, "Дрель", "Описание", true, null, ItemRequestConstantsTest.DEFAULT_REQUEST_ID);
        when(userRepository.findById(1L)).thenReturn(Optional.of(ItemConstantsTest.OWNER));
        when(itemRequestRepository.findById(ItemRequestConstantsTest.DEFAULT_REQUEST_ID))
                .thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            item.setId(10L);
            return item;
        });

        ItemDto result = itemService.createItem(1L, dto);

        assertEquals(10L, result.getId());
        assertEquals(ItemRequestConstantsTest.DEFAULT_REQUEST_ID, result.getRequest());
    }

    @Test
    @DisplayName("Create item with non-existing item request")
    void createItem_nonExistingItemRequest_throwNotFoundException() {
        ItemDto dto = new ItemDto(null, "Дрель", "Описание", true, null, 999L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(ItemConstantsTest.OWNER));
        when(itemRequestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(1L, dto));
    }

    @Test
    @DisplayName("Create comment after completed booking")
    void createComment_completedBooking_returnComment() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        Booking booking = BookingConstantsTest.createLastApprovedBooking(item);
        CommentDto commentDto = new CommentDto(null, null, "Отличная вещь");
        when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
                .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerId(1L, BookingConstantsTest.BOOKER.getId()))
                .thenReturn(booking);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(5L);
            return comment;
        });

        ResponseCommentDto result = itemService.createComment(
                BookingConstantsTest.BOOKER.getId(), 1L, commentDto);

        assertEquals(5L, result.getId());
        assertEquals("Отличная вещь", result.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("Create comment without booking")
    void createComment_noBooking_throwBadRequestException() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
                .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerId(1L, BookingConstantsTest.BOOKER.getId()))
                .thenReturn(null);

        assertThrows(BadRequestException.class,
                () -> itemService.createComment(
                        BookingConstantsTest.BOOKER.getId(), 1L, new CommentDto(null, null, "text")));
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create comment during active approved booking")
    void createComment_activeApprovedBooking_throwBadRequestException() {
        Item item = ItemConstantsTest.createItem(1L, "Дрель", "Описание", true, ItemConstantsTest.OWNER);
        Booking booking = BookingConstantsTest.createBooking(
                9L,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1),
                item,
                BookingConstantsTest.BOOKER,
                BookingStatus.APPROVED
        );
        when(userRepository.findById(BookingConstantsTest.BOOKER.getId()))
                .thenReturn(Optional.of(BookingConstantsTest.BOOKER));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerId(1L, BookingConstantsTest.BOOKER.getId()))
                .thenReturn(booking);

        assertThrows(BadRequestException.class,
                () -> itemService.createComment(
                        BookingConstantsTest.BOOKER.getId(), 1L, new CommentDto(null, null, "text")));
        verify(commentRepository, never()).save(any());
    }
}
