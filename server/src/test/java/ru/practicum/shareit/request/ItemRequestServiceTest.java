package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.UserConstantsTest;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    @DisplayName("getOwnRequests returns requests of user")
    void getOwnRequests_existingUser_returnOwnRequests() {
        ItemRequest request = ItemRequestConstantsTest.defaultRequest();
        when(userRepository.findById(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenReturn(Optional.of(ItemRequestConstantsTest.REQUESTER));
        when(itemRequestRepository.findAllByRequesterId(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIds(anyIterable())).thenReturn(List.of());

        List<ResponseItemRequestDto> result = itemRequestService.getOwnRequests(
                ItemRequestConstantsTest.REQUESTER.getId());

        assertEquals(1, result.size());
        assertEquals(ItemRequestConstantsTest.REQUEST_DESCRIPTION, result.getFirst().getDescription());
        assertEquals(ItemRequestConstantsTest.REQUEST_CREATED, result.getFirst().getCreated());
        verify(itemRequestRepository).findAllByRequesterId(ItemRequestConstantsTest.REQUESTER.getId());
    }

    @Test
    @DisplayName("getOwnRequests returns empty list when user has no requests")
    void getOwnRequests_noRequests_returnEmptyList() {
        when(userRepository.findById(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenReturn(Optional.of(ItemRequestConstantsTest.REQUESTER));
        when(itemRequestRepository.findAllByRequesterId(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenReturn(List.of());

        List<ResponseItemRequestDto> result = itemRequestService.getOwnRequests(
                ItemRequestConstantsTest.REQUESTER.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getOwnRequests throws when user not found")
    void getOwnRequests_nonExistingUser_throwNotFoundException() {
        when(userRepository.findById(UserConstantsTest.NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getOwnRequests(UserConstantsTest.NON_EXISTING_USER_ID));
        verify(itemRequestRepository, never()).findAllByRequesterId(anyLong());
    }

    @Test
    @DisplayName("getOthersRequests returns requests of other users")
    void getOthersRequests_existingUser_returnOthersRequests() {
        ItemRequest request = ItemRequestConstantsTest.otherUserRequest();
        when(userRepository.findById(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenReturn(Optional.of(ItemRequestConstantsTest.REQUESTER));
        when(itemRequestRepository.findAllRequestsOfOthers(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIds(anyIterable())).thenReturn(List.of());

        List<ResponseItemRequestDto> result = itemRequestService.getOthersRequests(
                ItemRequestConstantsTest.REQUESTER.getId());

        assertEquals(1, result.size());
        assertEquals(ItemRequestConstantsTest.OTHER_REQUEST_DESCRIPTION, result.getFirst().getDescription());
        verify(itemRequestRepository).findAllRequestsOfOthers(ItemRequestConstantsTest.REQUESTER.getId());
    }

    @Test
    @DisplayName("getOthersRequests returns empty list when no other requests exist")
    void getOthersRequests_noRequests_returnEmptyList() {
        when(userRepository.findById(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenReturn(Optional.of(ItemRequestConstantsTest.REQUESTER));
        when(itemRequestRepository.findAllRequestsOfOthers(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenReturn(List.of());

        List<ResponseItemRequestDto> result = itemRequestService.getOthersRequests(
                ItemRequestConstantsTest.REQUESTER.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getOthersRequests throws when user not found")
    void getOthersRequests_nonExistingUser_throwNotFoundException() {
        when(userRepository.findById(UserConstantsTest.NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getOthersRequests(UserConstantsTest.NON_EXISTING_USER_ID));
        verify(itemRequestRepository, never()).findAllRequestsOfOthers(anyLong());
    }

    @Test
    @DisplayName("getRequestById returns request when it exists")
    void getRequestById_existingRequest_returnRequest() {
        ItemRequest request = ItemRequestConstantsTest.defaultRequest();
        when(itemRequestRepository.findById(ItemRequestConstantsTest.DEFAULT_REQUEST_ID))
                .thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(ItemRequestConstantsTest.DEFAULT_REQUEST_ID))
                .thenReturn(List.of());

        ResponseItemRequestDto result = itemRequestService.getRequestById(
                ItemRequestConstantsTest.DEFAULT_REQUEST_ID);

        assertEquals(ItemRequestConstantsTest.REQUEST_DESCRIPTION, result.getDescription());
        assertEquals(ItemRequestConstantsTest.REQUEST_CREATED, result.getCreated());
    }

    @Test
    @DisplayName("getRequestById throws when request not found")
    void getRequestById_nonExistingRequest_throwNotFoundException() {
        when(itemRequestRepository.findById(ItemRequestConstantsTest.NON_EXISTING_REQUEST_ID))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(ItemRequestConstantsTest.NON_EXISTING_REQUEST_ID));
    }

    @Test
    @DisplayName("addRequest saves and returns created request")
    void addRequest_existingUser_saveAndReturnRequest() {
        ItemRequestDto requestDto = ItemRequestConstantsTest.VALID_ITEM_REQUEST_DTO;
        ItemRequest savedRequest = ItemRequestConstantsTest.defaultRequest();
        when(userRepository.findById(ItemRequestConstantsTest.REQUESTER.getId()))
                .thenReturn(Optional.of(ItemRequestConstantsTest.REQUESTER));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedRequest);

        ResponseItemRequestDto result = itemRequestService.addRequest(
                ItemRequestConstantsTest.REQUESTER.getId(), requestDto);

        assertEquals(1, result.getId());
        assertEquals(ItemRequestConstantsTest.REQUEST_DESCRIPTION, result.getDescription());
        assertEquals(ItemRequestConstantsTest.REQUEST_CREATED, result.getCreated());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    @DisplayName("addRequest throws when user not found")
    void addRequest_nonExistingUser_throwNotFoundException() {
        when(userRepository.findById(UserConstantsTest.NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.addRequest(
                        UserConstantsTest.NON_EXISTING_USER_ID,
                        ItemRequestConstantsTest.VALID_ITEM_REQUEST_DTO));
        verify(itemRequestRepository, never()).save(any());
    }
}
