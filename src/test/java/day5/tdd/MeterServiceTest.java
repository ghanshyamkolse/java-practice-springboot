package day5.tdd;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

/**
 * JUnit 5 + Mockito Unit Test verifying MeterService logic.
 * Demonstrates proper use of `@Mock`, `@InjectMocks`, and Mockito stubs/verifications.
 */
@ExtendWith(MockitoExtension.class)
public class MeterServiceTest {

    @Mock
    private MeterRepository meterRepository; // Plain Mockito mock

    @InjectMocks
    private MeterService meterService; // Under test: receives the mocked repository

    @Test
    public void testGetMeterStatus_Success() {
        // 1. Arrange
        SmartMeterMock activeMeter = new SmartMeterMock("M101", "ACTIVE");
        when(meterRepository.findByMeterId("M101")).thenReturn(Optional.of(activeMeter));

        // 2. Act
        String status = meterService.getMeterStatus("M101");

        // 3. Assert
        assertEquals("ACTIVE", status);
        
        // Verify findByMeterId was called exactly once with expected ID
        verify(meterRepository, times(1)).findByMeterId("M101");
    }

    @Test
    public void testGetMeterStatus_ThrowsException_WhenNotFound() {
        // 1. Arrange
        when(meterRepository.findByMeterId("M999")).thenReturn(Optional.empty());

        // 2. Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            meterService.getMeterStatus("M999");
        });

        assertEquals("Meter not registered.", exception.getMessage());
        verify(meterRepository, times(1)).findByMeterId("M999");
    }
}
