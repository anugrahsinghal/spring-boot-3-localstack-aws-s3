package com.netcracker.utility.repository;

import com.netcracker.utility.domain.FileMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMappingRepository extends JpaRepository<FileMapping, Long> {
}