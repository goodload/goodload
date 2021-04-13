package org.divsgaur.goodload.core;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Data
public class Mutable<T> {
    private T object;
}
