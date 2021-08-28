/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.okay.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.noriental.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.noriental.modules.sys.entity.SysUserRoleEntity;

import java.util.List;


/**
 * 用户与角色对应关系
 *
 * @author Jing.Li ja_ckli@126.com
 */
public interface SysUserRoleService extends IService<SysUserRoleEntity> {

    void saveOrUpdate(Long userId, List<Long> roleIdList);

    /**
     * 根据用户ID，获取角色ID列表
     */
    List<Long> queryRoleIdList(Long userId);

    /**
     * 根据角色ID数组，批量删除
     */
    int deleteBatch(Long[] roleIds);
}
