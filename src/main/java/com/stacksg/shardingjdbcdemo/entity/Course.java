package com.stacksg.shardingjdbcdemo.entity;

import lombok.Data;

/**
 * @author 96047
 */
@Data
public class Course {
    private Long cid;
    private String cname;
    private Long userId;
    private String cstatus;
}
