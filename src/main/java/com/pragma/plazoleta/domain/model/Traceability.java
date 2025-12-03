package com.pragma.plazoleta.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Traceability {
    private String id;
    private Long orderId;
    private Long clientId;
    private String clientEmail;
    private LocalDateTime date;
    private String previousStatus;
    private String newStatus;
    private Long employeeId;
    private String employeeEmail;

    public Traceability() {
    }
    
    public static class ClientInfo {
        private final Long id;
        private final String email;

        public ClientInfo(Long id, String email) {
            this.id = id;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }
    }
    
    public static class EmployeeInfo {
        private final Long id;
        private final String email;

        public EmployeeInfo(Long id, String email) {
            this.id = id;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }
    }

}
