package com.csdy.until;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CsdyDamage {
    private static final Map<String, CsdyDamage> INTERNAL_DAMAGE_TYPES = new HashMap<>();
    public static final Map<String, CsdyDamage> CSDY_DAMAGE_MAP = Collections.unmodifiableMap(INTERNAL_DAMAGE_TYPES);
    public static final CsdyDamage BYPASS_EVERYTHING_PLAYER_ATTACK = new CsdyDamage();


}
