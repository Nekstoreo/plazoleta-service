package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Traceability;

import java.util.List;

public interface ITraceabilityPort {
    void saveTraceability(Traceability traceability);
    List<Traceability> getTraceabilityByOrderId(Long orderId);
}
