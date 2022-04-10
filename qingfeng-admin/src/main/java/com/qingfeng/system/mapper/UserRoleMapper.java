package com.qingfeng.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingfeng.system.entity.UserRole;
import org.apache.ibatis.annotations.Param;

/**
 * @ProjectName UserRoleMapper
 * @author Administrator
 * @version 1.0.0
 * @Description UserRoleMapper
 * @createTime 2022/1/20 0020 0:12
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户Id删除该用户的角色关系
     *
     * @param userId 用户 ID
     * @return boolean
     */
    Boolean deleteByUserId(@Param("userId") String userId);

    /**
     * 根据角色Id删除该角色的用户关系
     *
     * @param roleId 角色 ID
     * @return boolean
     */
    Boolean deleteByRoleId(@Param("roleId") String roleId);
}
