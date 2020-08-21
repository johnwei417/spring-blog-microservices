package com.honglin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String username;

    @Column
    private String firstname;

    @Column
    private String lastname;

    @Column(name = "avatar", columnDefinition = "BLOB")
    @Lob
    private byte[] avatar;

    @Column
    private String email;


    @Column(name = "profile", columnDefinition = "text")
    private String profile;

    @Column
    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @JsonIgnore
    private Date registeredAt;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Post> postList;

}
