package com.why.factory.entity;

/**
 * Created by jianghw on 14-6-14.
 */
public abstract class AbstractSword {

    protected AbstractSword(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
