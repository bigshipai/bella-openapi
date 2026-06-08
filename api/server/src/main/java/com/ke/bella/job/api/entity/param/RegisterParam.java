package com.ke.bella.job.api.entity.param;

import lombok.Data;

public class RegisterParam {

    @Data
    public static class RegisterEndpointParam {
        private String endpoint;
    }

    @Data
    public static class RegisterModelParam {
        private String model;
        private String endpoint;
    }
}
