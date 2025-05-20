package com.matech.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


/**
 * Lombok annotations:
 * <p>
 *     <code> @Data </code> generate getters and setters
 * </p>
 * <p><code> @Builder </code> create this class using the builder design pattern</p>
 * <p><code> @NoArgsConstructor </code> </p>
 * <p><code> @AllArgsConstructor </code> used in the Builder design pattern
 * <p><code> @Entity </code> this user class need to be an entity
 * <p><code> @Table </code> create the table name as entity
 * <p>
 * This user class matches an entity correspondent on the database - hibernate will use it to create the sql statements
 * to generate this table. For every User entity you would like to implement as UserDetails interface.
 * </p>
 * <p>
 * We can always extend the User.java from Spring boot which would be the implementation of UserDetails interface but
 * you would have less control of that class.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {
    // my user has the following features**
    @Id
    @GeneratedValue
    private Integer id; //Unique identifier of this class - auto generated as well
    private String password;
    private String email; //username
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * @return list of roles
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired(); //true
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked(); //true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired(); //true
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled(); //true
    }
}
