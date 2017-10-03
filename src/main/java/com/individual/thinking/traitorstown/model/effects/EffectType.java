package com.individual.thinking.traitorstown.model.effects;

import com.individual.thinking.traitorstown.model.Role;
import com.individual.thinking.traitorstown.model.exceptions.UnknownRoleException;

public enum EffectType {
    MAYOR,
    CITIZEN,
    TRAITOR,
    OTHER;

    public static EffectType fromRole(Role role){
        switch (role) {
            case CITIZEN: return CITIZEN;
            case TRAITOR: return TRAITOR;
            default: throw new UnknownRoleException("Role " + role + " is not known!");
        }
    }
}
