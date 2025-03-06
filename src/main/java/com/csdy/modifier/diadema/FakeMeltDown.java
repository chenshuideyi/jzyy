package com.csdy.modifier.diadema;

import com.csdy.DiademaModifier;
import com.csdy.diadema.DiademaRegister;
import com.csdy.frames.diadema.DiademaType;

public class FakeMeltDown extends DiademaModifier{
    protected DiademaType getDiademaType() {
        return DiademaRegister.FAKE_MELT_DOWN.get();
    }
}
