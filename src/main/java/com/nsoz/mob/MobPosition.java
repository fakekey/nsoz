package com.nsoz.mob;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MobPosition {

    private short id;
    private short x;
    private short y;
    private boolean isBeast;

}
