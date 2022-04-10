package com.qingfeng.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingfeng.system.entity.UserGroup;
import com.qingfeng.system.mapper.UserGroupMapper;
import com.qingfeng.system.service.IUserGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ProjectName UserGroupServiceImpl
 * @author Administrator
 * @version 1.0.0
 * @Description UserGroupServiceImpl实现类
 * @createTime 2022/1/20 0020 0:19
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroup> implements IUserGroupService {


}