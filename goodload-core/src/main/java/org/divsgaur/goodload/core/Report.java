package org.divsgaur.goodload.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    private String stepName;
    private long timeInMillis;
    private List<Report> subSteps;
}
