package com.fsdesa.ocrsvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResultDto {
    private String thumbnail;
    private List<String> lines;
}
