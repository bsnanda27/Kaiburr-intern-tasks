package com.bsnanda.kaiburr.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskExecution {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSX", timezone = "UTC")
        private Date startTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSX", timezone = "UTC")
        private Date endTime;

        private String output;
}
