package vn.dangthehao.train.security.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.entity.AppUser;
import vn.dangthehao.train.entity.AppUsersRole;
import vn.dangthehao.train.repository.UserRepository;
import vn.dangthehao.train.repository.UserRoleRepository;
import vn.dangthehao.train.security.CustomUserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class CustomUserDetailsService implements UserDetailsService {
  UserRepository userRepository;
  UserRoleRepository userRoleRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));

    return new CustomUserDetails(
        user.getId(), user.getUsername(), user.getPassword(), getAuthorities(user));
  }

  private Collection<? extends GrantedAuthority> getAuthorities(AppUser user) {
    List<AppUsersRole> roles = userRoleRepository.findByUserId(user.getId());

    return roles.stream()
        .map(role -> new SimpleGrantedAuthority(role.getRole().getName()))
        .collect(Collectors.toList());
  }
}
