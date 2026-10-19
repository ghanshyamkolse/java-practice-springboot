package com.energy.meter;

import jakarta.validation.constraints.NotBlank;

public record MeterRequest(@NotBlank String meterId, @NotBlank String status) {
}
