package com.example.backendapi.users;

import com.example.backendapi.api.ApiConstants;
import com.example.backendapi.users.dto.CreateUserRequest;
import com.example.backendapi.users.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** User CRUD endpoints (mirrors users.jsp and UserDAO). */
@RestController
@RequestMapping(ApiConstants.API_BASE + "/users")
@Tag(name = ApiConstants.TAG_USERS)
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    // PUBLIC_INTERFACE
    @GetMapping
    @Operation(summary = "List users", description = "Returns all users.")
    public List<UserDto> list() {
        /** Lists all users. */
        return usersService.listUsers();
    }

    // PUBLIC_INTERFACE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user", description = "Creates a new user.")
    public UserDto create(@Valid @RequestBody CreateUserRequest request) {
        /** Creates a user. */
        return usersService.createUser(request);
    }
}
