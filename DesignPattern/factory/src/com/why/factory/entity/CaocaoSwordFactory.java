package com.why.factory.entity;

/**
 * Created by jianghw on 14-6-14.
 */
public class CaocaoSwordFactory implements ISwordFactory {
    @Override
    public AbstractSword createSword() {
        return new QixingSword();
    }
}
