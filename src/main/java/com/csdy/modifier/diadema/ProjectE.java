package com.csdy.modifier.diadema;

import com.csdy.diadema.DiademaRegister;
import com.csdy.DiademaModifier;
import com.csdy.frames.diadema.DiademaType;
public class ProjectE extends DiademaModifier {
    @Override
    protected DiademaType getDiademaType() {
        return DiademaRegister.PROJECTE.get();
    }
}
