package com.cloudator.files.cloudatorfiles.entity;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class File {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false)
    private String filetype;
    
    @Column(nullable = false)
    private String fileroute;

    @Column(nullable = false)
    private Date filedate;
    
    @Column(nullable = false)
    private float filesize;
    
    @Column(nullable = false)
    private String owner;
    
    @Column(nullable = false)
    private Boolean ispublic;
}
