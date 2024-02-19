package com.cloudator.files.cloudatorfiles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cloudator.files.cloudatorfiles.entity.File;

public interface FileRepository extends JpaRepository<File, Long> {
    // La interfaz está vacía porque JpaRepository ya proporciona los métodos necesarios
}