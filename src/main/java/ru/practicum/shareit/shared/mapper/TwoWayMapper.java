package ru.practicum.shareit.shared.mapper;

public interface TwoWayMapper<D, P> {
    D toData(P p);

    P toPresentation(D d);
}
