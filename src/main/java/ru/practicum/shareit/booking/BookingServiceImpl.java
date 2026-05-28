package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.shared.error.BadRequestException;
import ru.practicum.shareit.shared.error.ForbiddenException;
import ru.practicum.shareit.shared.error.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private List<Booking> handleStateForBookingsOfItemsOwnedByUser(Long userId, BookingState state) {
        return switch (state) {
            case CURRENT ->
                    bookingRepository.findAllByItemOwnerIdAndInCurrentPeriod(userId);
            case PAST ->
                    bookingRepository.findAllByItemOwnerIdAndInPast(userId);
            case FUTURE ->
                    bookingRepository.findAllByItemOwnerIdAndInFuture(userId);
            case WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED);
            default ->
                    bookingRepository.findAllByItemOwnerId(userId);
        };
    }

    private List<Booking> handleStateForBookingsOfUser(Long bookerId, BookingState state) {
        return switch (state) {
            case CURRENT ->
                    bookingRepository.findAllByBookerIdAndInCurrentPeriod(bookerId);
            case PAST ->
                    bookingRepository.findAllByBookerIdAndInPast(bookerId);
            case FUTURE ->
                    bookingRepository.findAllByBookerIdAndInFuture(bookerId);
            case WAITING ->
                    bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED);
            default ->
                    bookingRepository.findAllByBookerId(bookerId);
        };
    }

    @Override
    public List<ResponseBookingDto> getBookings(Long userId, BookingState state) {
        getUserByIdOrThrow(userId);
        List<ResponseBookingDto> bookings = handleStateForBookingsOfUser(userId, state)
                .stream()
                .map(BookingMapper::toResponseDto)
                .toList();
        log.info("Loaded {} bookings for bookerId={}, state={}", bookings.size(), userId, state);
        return bookings;
    }

    @Override
    public ResponseBookingDto getBookingById(Long bookingId, Long userId) {
        getUserByIdOrThrow(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking not found: id={}", bookingId);
                    return new NotFoundException("Бронирование с id=" + bookingId + " не найдено");
                });
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("Booking access denied: bookingId={}, userId={}", bookingId, userId);
            throw new ForbiddenException("Вы не являетесь создателем запроса или владельцем предмета из запроса.");
        }
        log.info("Found booking id={} for userId={}", bookingId, userId);
        return BookingMapper.toResponseDto(booking);
    }

    @Override
    public ResponseBookingDto create(Long requestorId, BookingDto bookingDto) {
        User user = getUserByIdOrThrow(requestorId);
        Item item = getItemByIdOrThrow(bookingDto.getItemId());

        if (!item.isAvailable()) {
            log.warn("Booking denied: item id={} is not available", item.getId());
            throw new BadRequestException("Предмет с id=" + item.getId() + " недоступен для бронирования");
        }

        bookingDto.setStatus(BookingStatus.WAITING);

        Booking booking = BookingMapper.toData(bookingDto, item, user);
        Booking saved = bookingRepository.save(booking);
        log.info("Created booking id={} for itemId={} by bookerId={}",
                saved.getId(), item.getId(), requestorId);
        return BookingMapper.toResponseDto(saved);
    }

    @Override
    public ResponseBookingDto updateStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.warn("Booking not found: id={}", bookingId);
                    return new NotFoundException("Бронирование с id=" + bookingId + " не найдено");
                });
        userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found on booking status update: userId={}", userId);
                    return new ForbiddenException("Пользователь с id=" + userId + " не найден");
                });

        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);
        Booking saved = bookingRepository.save(booking);
        log.info("Updated booking id={} status={} by userId={}", bookingId, status, userId);
        return BookingMapper.toResponseDto(saved);
    }

    @Override
    public List<ResponseBookingDto> getBookingsOfItemsOwnedByUser(Long userId, BookingState state) {
        getUserByIdOrThrow(userId);
        List<ResponseBookingDto> bookings = handleStateForBookingsOfItemsOwnedByUser(userId, state)
                .stream()
                .map(BookingMapper::toResponseDto)
                .toList();
        log.info("Loaded {} owner bookings for userId={}, state={}", bookings.size(), userId, state);
        return bookings;
    }

    private Item getItemByIdOrThrow(long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found: id={}", itemId);
                    return new NotFoundException("Вещь с id=" + itemId + " не найдена");
                });
    }

    private User getUserByIdOrThrow(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", userId);
                    return new NotFoundException("Пользователь с id=" + userId + " не найден");
                });
    }
}
