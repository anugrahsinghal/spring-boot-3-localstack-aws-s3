package com.netcracker.utility.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UploadedLinks {
	private List<FileWithLink> fileWithLinks;

}

