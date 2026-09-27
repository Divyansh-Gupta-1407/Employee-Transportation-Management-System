package com.ets.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteStopResponse {
    private Long id;
    private String stopName;
    private Integer stopOrder;
    private Double latitude;
    private Double longitude;
}
