package com.example.backendapi.users;

import com.example.backendapi.error.ConflictException;
import com.example.backendapi.users.dto.CreateUserRequest;
import com.example.backendapi.users.dto.UserDto;
import java.util.List;
import org.springframework.stereotype.Service;

/** Business logic for users (mirrors monolith UserDAO behavior). */
@Service
public class UsersService {

    private final UserRepository userRepository;

    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // PUBLIC_INTERFACE
    public List<UserDto> listUsers() {
        /** Lists all users. */
        return userRepository.findAll();
    }

    // PUBLIC_INTERFACE
    public UserDto createUser(CreateUserRequest request) {
        /**
         * Creates a user.
         *
         * <p>Monolith enforces email uniqueness via table constraint; we provide an explicit check for
         * a clearer error response.
         */
        userRepository
                .findByEmail(request.email())
                .ifPresent(
                        u -> {
                            throw new ConflictException("User with this email already exists");
                        });
        return userRepository.insert(request.email(), request.name());
    }
}
