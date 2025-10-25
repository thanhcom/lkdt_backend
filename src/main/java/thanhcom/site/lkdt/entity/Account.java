package thanhcom.site.lkdt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = 25)
    private String username;

    @Column(name = "fullname", nullable = false)
    private String fullname;

    @Column(name = "active", nullable = false)
    private Boolean active = false;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", nullable = false , length = 25)
    private String phone;

    @Column(name = "email", nullable = false , length = 50)
    private String email;


    @ColumnDefault("CURRENT_TIMESTAMP")
    @CreationTimestamp
    @Column(name = "datecreate", nullable = false)
    private LocalDateTime datecreate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @UpdateTimestamp
    @Column(name = "last_update", nullable = false)
    private LocalDateTime last_update;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "birthday", nullable = false)
    private LocalDateTime birthday;

    @ManyToMany
    @JoinTable(
            name = "account_role",
            joinColumns = {@JoinColumn(name = "accountid")},
            inverseJoinColumns = {@JoinColumn(name = "roleid")}
    )
    private Set<Role> roles = new HashSet<>();

}