package com.anurag.android.treasurehunt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ANURAG NAIK on 2/2/2017.
 */

public class Arena {
    public String name;
    public Boolean completed;

    public Arena(String name, Boolean completed) {
        this.name = name;
        this.completed = completed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
