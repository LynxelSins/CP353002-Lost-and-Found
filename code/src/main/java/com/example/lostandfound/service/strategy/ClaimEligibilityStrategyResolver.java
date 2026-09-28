package com.example.lostandfound.service.strategy;

import com.example.lostandfound.domain.enums.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClaimEligibilityStrategyResolver {

    private final List<ClaimEligibilityStrategy> strategies;
    private Map<ReportType, ClaimEligibilityStrategy> strategyMap;

    private Map<ReportType, ClaimEligibilityStrategy> map() {
        if (strategyMap == null) {
            strategyMap = strategies.stream()
                    .collect(Collectors.toMap(ClaimEligibilityStrategy::supports, s -> s));
        }
        return strategyMap;
    }

    public ClaimEligibilityStrategy resolve(ReportType type) {
        return map().get(type);
    }
}