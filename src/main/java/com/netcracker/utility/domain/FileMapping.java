package com.netcracker.utility.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "file_mapping")
public class FileMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Embedded
    private FileInfo fileInfo;

    @Basic(fetch = FetchType.LAZY)
    @CreationTimestamp
    private Instant createdAt;

    public FileMapping(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }
}
