package com.energy.meter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MeterController.class)
class MeterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeterRepository meterRepository;

    @Test
    void createsMeter() throws Exception {
        when(meterRepository.existsByMeterId("meter-1")).thenReturn(false);
        when(meterRepository.save(any(Meter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/api/meters")
                        .contentType("application/json")
                        .content("""
                                {"meterId":"meter-1","status":"ACTIVE"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.meterId").value("meter-1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void rejectsDuplicateMeterId() throws Exception {
        when(meterRepository.existsByMeterId("meter-1")).thenReturn(true);

        mockMvc.perform(post("/api/meters")
                        .contentType("application/json")
                        .content("""
                                {"meterId":"meter-1","status":"ACTIVE"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsBlankMeterId() throws Exception {
        mockMvc.perform(post("/api/meters")
                        .contentType("application/json")
                        .content("""
                                {"meterId":" ","status":"ACTIVE"}
                                """))
                .andExpect(status().isBadRequest());
    }
}
