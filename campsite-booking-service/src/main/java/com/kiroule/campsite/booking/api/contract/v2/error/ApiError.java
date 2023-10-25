package com.kiroule.campsite.booking.api.contract.v2.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class ApiError {

  private HttpStatus status;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LocalDateTime timestamp;

  private String message;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> subErrors;
}
