package org.divsgaur.goodload.samples.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Sample implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String desc;
}