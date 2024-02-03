package com.nsoz.map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Tree {

    private int id;
    private short x, y;
}
