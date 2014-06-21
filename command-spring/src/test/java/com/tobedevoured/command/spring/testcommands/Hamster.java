package com.tobedevoured.command.spring.testcommands;

import com.tobedevoured.command.annotation.ByYourCommand;
import com.tobedevoured.command.spring.annotation.SpringSupport;

@ByYourCommand
@SpringSupport( contexts={"classpath:testContext.xml"} )
public class Hamster  extends com.tobedevoured.testcommands.Hamster {
    HamsterWheel wheel;

    public HamsterWheel getWheel() {
        return wheel;
    }

    public void setWheel(HamsterWheel wheel) {
        this.wheel = wheel;
    }

}
