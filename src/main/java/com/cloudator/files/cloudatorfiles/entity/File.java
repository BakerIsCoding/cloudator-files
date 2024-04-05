package com.cloudator.files.cloudatorfiles.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class File {
    
    private Long id;
    
    private String filename;

    private String filetype;

    private String fileroute;

    private Date filedate;

    private Long filesize;

    private Long owner;

    private Boolean ispublic;

    private String url;
}
