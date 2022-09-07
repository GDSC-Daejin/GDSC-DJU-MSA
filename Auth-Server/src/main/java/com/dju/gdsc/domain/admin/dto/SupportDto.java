package com.dju.gdsc.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportDto {
    @Builder.Default
    boolean frontend = false;
    @Builder.Default
    boolean backend = false;
    @Builder.Default
    boolean design = false;
    @Builder.Default
    boolean android = false;
    @Builder.Default
    boolean ml = false;
    @Builder.Default
    boolean beginner = false;
    @Builder.Default
    boolean home = false;
}
