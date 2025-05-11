package com.ilhanozkan.libraryManagementSystem.security;

import com.ilhanozkan.libraryManagementSystem.model.entity.User;
import com.ilhanozkan.libraryManagementSystem.model.entity.UserPrincipal;
import com.ilhanozkan.libraryManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    logger.debug("Attempting to load user by username: {}", username);
    
    User user = userRepository.findByUsername(username);

    if (user == null) {
      logger.error("User not found with username: {}", username);
      throw new UsernameNotFoundException(username);
    }

    logger.debug("User found: {}", user.getUsername());
    return new UserPrincipal(user);
  }
}
