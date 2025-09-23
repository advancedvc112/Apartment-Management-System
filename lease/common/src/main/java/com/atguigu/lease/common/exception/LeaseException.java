package com.atguigu.lease.common.exception;

import com.atguigu.lease.common.result.ResultCodeEnum;
import lombok.Data;

@Data
public class LeaseException extends RuntimeException {
    private Integer code;

    public LeaseException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    /**
     * 根据响应结果枚举对象创建异常对象
     * @param resultCodeEnum
     */
    public LeaseException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "LeaseException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }


}
