package org.jor.shared.utils;

import java.util.Comparator;

public class EnumNameComparator implements Comparator<Enum<?>>
{
    @Override
    public int compare(Enum<?> o1, Enum<?> o2)
    {
        return o1.name().compareTo(o2.name());
    }
}
