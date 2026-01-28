package br.edu.ifpb.veritas.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_ADMINISTRATOR")
@DiscriminatorValue("ADMINISTRATOR")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TB_ADMIN_NAME")
    private String name;

    @Column(name = "TB_ADMIN_PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "TB_ADMIN_REGISTER")
    private String register;

    @Column(name = "TB_ADMIN_LOGIN")
    private String login;

    @Column(name = "TB_ADMIN_PASSWORD")
    private String password;

    /**
     * Admin isActive full true.
     */
    private Boolean isActive = true;
}
