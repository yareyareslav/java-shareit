package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    User create(User user);

    User update(User user);

    void delete(Long id);
}
