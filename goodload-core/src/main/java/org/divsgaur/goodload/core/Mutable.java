package org.divsgaur.goodload.core;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Mutable<T> {
    private T object;
}
