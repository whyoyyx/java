package com.why.factory.entity;

/**
 * Created by jianghw on 14-6-14.
 */
public class Client {
    public static void main(String[] args){
        ISwordFactory iSwordFactory = new CaocaoSwordFactory();
        AbstractSword abstractSword = iSwordFactory.createSword();
    }
}
