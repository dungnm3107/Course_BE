package com.example.course_be.service.impl;

import com.example.course_be.entity.Role;
import com.example.course_be.entity.User;
import com.example.course_be.repository.RoleRepository;
import com.example.course_be.repository.UserRepository;
import com.example.course_be.service.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserSecurityServiceImpl implements UserSecurityService {


    private UserRepository userRepository;
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user;

        // Kiểm tra xem identifier là email hay username
        if (identifier.contains("@")) {  // Nếu là email, tìm theo email
            user = userRepository.findByEmail(identifier);
        } else {  // Nếu là username, tìm theo username
            user = findByUserName(identifier);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Debugging output
        System.out.println("Identifier: " + identifier);
        System.out.println("Password: " + user.getPassword());

        // Kiểm tra nếu là đăng nhập bằng username và password (cần password)
        if (!identifier.contains("@")) {
            if (user.getUserName() == null || user.getUserName().isEmpty() ||
                    user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new IllegalArgumentException("User details cannot be null or empty for username login");
            }
        }

        // Nếu là đăng nhập qua Google (email), password có thể không cần kiểm tra
        org.springframework.security.core.userdetails.User userDetail = new org.springframework.security.core.userdetails.User(
                user.getUserName() != null ? user.getUserName() : user.getEmail(), // Username hoặc email cho phần identifier
                user.getPassword() != null ? user.getPassword() : "", // Password có thể để trống nếu đăng nhập bằng Google
                rolesToAuthorities(user.getListRoles())
        );
        return userDetail;
    }


    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
    }
}
