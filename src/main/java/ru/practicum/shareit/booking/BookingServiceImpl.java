package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.shared.error.ForbiddenException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

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

    private List<Booking> handleStateForBookingsOfUser (Long bookerId, BookingState state) {
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
    public List<BookingDto> getBookings(Long userId, BookingState state) {
        return handleStateForBookingsOfItemsOwnedByUser(userId, state)
                .stream()
                .map(bookingMapper::toPresentation)
                .toList();
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Вы не являетесь создателем запроса или владельцем предмета из запроса.");
        }
        return bookingMapper.toPresentation(booking);
    }

    @Override
    public BookingDto create(Long requestorId, BookingDto bookingDto) {
        Booking booking = bookingMapper.toData(bookingDto);
        return bookingMapper.toPresentation(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);
        return bookingMapper.toPresentation(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getBookingsOfCurrentUser(Long userId, BookingState state) {
        List<Booking> bookings = handleStateForBookingsOfUser(userId, state);
        return bookings.stream()
                .map(bookingMapper::toPresentation)
                .toList();
    }
}
