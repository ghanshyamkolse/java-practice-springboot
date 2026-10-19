package com.energy.reading;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ReadingRequest(@NotBlank String meterId, @PositiveOrZero double value) {
}
