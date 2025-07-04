package com.aiworkflow.domain.shared;

import java.io.Serializable;

/**
 * 值对象基类
 * 值对象的特征：
 * 1. 不可变性
 * 2. 无标识
 * 3. 等值性比较
 */
public abstract class ValueObject implements Serializable {
    
    /**
     * 值对象通过内容判断相等性
     */
    @Override
    public abstract boolean equals(Object o);
    
    /**
     * hashCode必须与equals保持一致
     */
    @Override
    public abstract int hashCode();
    
    /**
     * 提供有意义的字符串表示
     */
    @Override
    public abstract String toString();
    
    /**
     * 验证值对象的业务规则
     * 子类应该在构造函数中调用此方法
     */
    protected abstract void validate();
}