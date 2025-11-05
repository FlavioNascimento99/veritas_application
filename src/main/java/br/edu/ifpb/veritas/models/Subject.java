package br.edu.ifpb.veritas.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Subject {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false, unique = true)
   private String title;

   private String description;

   @Column(nullable = false)
   private Boolean active = true;

   @CreationTimestamp
   private LocalDateTime createdAt;

   @CreationTimestamp
   private LocalDateTime modifiedAt;
}
