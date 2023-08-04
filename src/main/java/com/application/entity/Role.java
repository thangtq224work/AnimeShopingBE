package com.application.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String role;
//    @JsonIgnore
//    @ToString.Exclude
//    @ManyToMany(mappedBy = "roles")
//    private List<Account> accounts;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "role")
//    @JoinTable(name = "account_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "username"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<AccountRole> accountRoles;
    public Role(Long id, String role) {
        super();
        this.id = id;
        this.role = role;
    }
    public Role(Long id) {
        super();
        this.id = id;
    }
}
