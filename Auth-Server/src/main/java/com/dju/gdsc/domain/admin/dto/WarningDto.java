package com.dju.gdsc.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WarningDto {
    @Schema(description = "경고재목" , example = "경고재목")
    String title;
    @Schema(description = "경고내용" , example = "경고내용")
    String content;
    @Schema(description = "경고를 줄 사람" , example = "누구한테 줄건지")
    String ToUser;
}
