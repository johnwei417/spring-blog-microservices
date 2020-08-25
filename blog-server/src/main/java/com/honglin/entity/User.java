package com.honglin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String firstname;

    @Column
    private String lastname;

    @Column
    private String avatar;

    @Column
    private String email;

    @Column
    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @JsonIgnore
    private Date registeredAt;


}
