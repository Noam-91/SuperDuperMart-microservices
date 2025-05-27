package com.beaconfire.userservice.service;

import com.beaconfire.userservice.dao.UserDao;
import com.beaconfire.userservice.dto.UserRegisterDTO;
import com.beaconfire.userservice.domain.User;
import com.beaconfire.userservice.domain.UserProfile;
import com.beaconfire.userservice.exception.InvalidCredentialsException;
import com.beaconfire.userservice.request.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserDao userDao;
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserDao userDao, JwtEncoder jwtEncoder, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
    }
    private static final long TOKEN_EXPIRE = 36000L;        // 10 hours
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    /** Validate user and issue token
     * @param loginRequest
     * @return token
     * @throws Exception
     */
    public String validateUserAndIssueToken(LoginRequest loginRequest){
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // validate username, password
        List<User> users= findAllActive();
        Optional<User> existUser = users.stream().filter(user ->
                user.getUsername().equals(username) &&
                        passwordEncoder.matches(password, user.getPassword())
        ).findFirst();
        if(existUser.isEmpty()){
            LOGGER.info("Incorrect credentials");
            throw new InvalidCredentialsException("Incorrect credentials, please try again.");
        }
        User userDetails = existUser.get();

        LOGGER.info("User {} logged in successfully", userDetails.getUsername());

        // issue token
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("user-service")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(TOKEN_EXPIRE))
                .subject(userDetails.getUsername())
                .claim("userId", userDetails.getUserId())
                .claim("role", userDetails.getAuthorities().iterator().next().getAuthority())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public List<User> findAllActive(){
        return userDao.findAllActive();
    }

    @Transactional
    public void registerUser(UserRegisterDTO userRegisterDTO) throws RuntimeException{
        try{
            String hashedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
            User user = User.builder()
                    .username(userRegisterDTO.getUsername())
                    .password(hashedPassword)
                    .build();
            userDao.save(user);

            // create User Profile
            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .firstName(userRegisterDTO.getFirstName())
                    .lastName(userRegisterDTO.getLastName())
                    .email(userRegisterDTO.getEmail())
                    .address(userRegisterDTO.getAddress())
                    .city(userRegisterDTO.getCity())
                    .state(userRegisterDTO.getState())
                    .zip(userRegisterDTO.getZip())
                    .country(userRegisterDTO.getCountry())
                    .build();
            userDao.save(userProfile);
        }catch (RuntimeException e){
            LOGGER.error("Error registering user: {}", e.getMessage());
            if(e.getMessage().contains("Duplicate entry")){
                throw new RuntimeException("Username already exists.");
            }
            throw e;
        }
    }
}
