package com.pia.ticketmanagement.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopItemResponse {

    private String name;
    private Long value;

}