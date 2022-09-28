package com.dju.gdsc.domain.admin.service;

import com.dju.gdsc.domain.admin.dto.SupportDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class SupportServiceTest {
    @InjectMocks
    private SupportService supportService;


    @Test
    void 지원제한() {
        SupportDto supportDto = supportService.지원제한();
        assertFalse(supportDto.isBackend());
        assertFalse(supportDto.isAndroid());
        assertFalse(supportDto.isDesign());
        assertFalse(supportDto.isFrontend());
        assertFalse(supportDto.isHome());
        assertFalse(supportDto.isMl());
    }

    @Test
    void 지원제한업데이트() {
        SupportDto supportDto = SupportDto.builder()
                .android(true)
                .backend(true)
                .design(true)
                .frontend(true)
                .home(true)
                .ml(true)
                .build();
        supportService.지원제한업데이트(supportDto);
        SupportDto supportDto1 = supportService.지원제한();
        assertTrue(supportDto1.isBackend());
        assertTrue(supportDto1.isAndroid());
        assertTrue(supportDto1.isDesign());
        assertTrue(supportDto1.isFrontend());
        assertTrue(supportDto1.isHome());
        assertTrue(supportDto1.isMl());
    }
}