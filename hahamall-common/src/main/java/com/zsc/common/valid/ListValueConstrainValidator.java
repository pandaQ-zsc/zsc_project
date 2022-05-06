package com.zsc.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义校验器
 */
public class ListValueConstrainValidator implements ConstraintValidator<ListValue,Integer> {
    private Set<Integer> set = new HashSet<>();
    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        //constraintAnnotation.vals()  获取注解给定的值@ListValue(vals={0,1})
        int[] vals = constraintAnnotation.vals();
        for(int val : vals){
            set.add(val);
        }
    }

    //判断是否校验成功

     /**
     *
     * @param value  需要校验的值。  也就是被注解@ListValue后变量的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
