package com.csdy.modifier.diadema;

import com.csdy.DiademaModifier;
import com.csdy.diadema.DiademaRegister;
import com.csdy.frames.diadema.DiademaType;

public class KillAura extends DiademaModifier {
    @Override
    protected DiademaType getDiademaType() {
        return DiademaRegister.FAKE_KILL_AURA.get();
    }

}
