package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Role;
import com.individual.thinking.traitorstown.model.exceptions.UnknownRoleException;

public enum SpecialEffectType {
    MAYOR,
    CITIZEN,
    TRAITOR,
    DEATH,
    ELECTIONS;

    public static SpecialEffectType fromRole(Role role){
        switch (role) {
            case CITIZEN: return CITIZEN;
            case TRAITOR: return TRAITOR;
            default: throw new UnknownRoleException("Role " + role + " is not known!");
        }
    }
}
