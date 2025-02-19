package com.ldh.forum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProfanityFilterServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProfanityFilterService profanityFilterService;

    @Test
    void testContainsProfanity() {

        when(restTemplate.getForObject(anyString(), eq(Boolean.class)))
                .thenAnswer(invocation -> {
                    String url = invocation.getArgument(0);
                    return url.contains("fuck");
                });


        assertTrue(profanityFilterService.containsProfanity("fuck"));
        assertFalse(profanityFilterService.containsProfanity("normalusername"));
    }
}

