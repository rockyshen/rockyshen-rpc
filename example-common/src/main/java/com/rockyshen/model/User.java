package com.rockyshen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author rockyshen
 * @date 2024/10/30 12:34
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    
    private String name;
    private Integer age;
    private String sex;

}
