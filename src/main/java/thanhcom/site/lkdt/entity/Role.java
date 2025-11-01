package thanhcom.site.lkdt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<Account> account =new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "role_permission",
            joinColumns = {@JoinColumn(name = "roleid")},
            inverseJoinColumns = {@JoinColumn(name = "permissionid")}

    )
    private Set<Permission> permissions=new HashSet<>();

}