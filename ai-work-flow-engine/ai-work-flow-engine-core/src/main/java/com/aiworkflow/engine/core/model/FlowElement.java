package com.aiworkflow.engine.core.model;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class FlowElement implements Serializable {

    private String key;

    private String name;

    // 画布上的坐标
    private int x;

    // 画布上的坐标
    private int y;

    public abstract String type();

    
}
